/*
 * Copyright (c) 2016-2019 VMware, Inc. All Rights Reserved.
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with separate copyright notices
 * and license terms. Your use of these subcomponents is subject to the terms and
 * conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package com.vmware.mangle.services.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.AbstractLdapAuthenticationProvider;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.vmware.mangle.cassandra.model.security.Privilege;
import com.vmware.mangle.cassandra.model.security.Role;
import com.vmware.mangle.cassandra.model.security.User;
import com.vmware.mangle.services.PrivilegeService;
import com.vmware.mangle.services.UserLoginAttemptsService;
import com.vmware.mangle.services.UserService;
import com.vmware.mangle.utils.exceptions.MangleException;
import com.vmware.mangle.utils.exceptions.handler.ErrorCode;

/**
 * Specialized LDAP authentication provider which uses Active Directory configuration conventions.
 * <p>
 * It will authenticate using the Active Directory
 * <a href="http://msdn.microsoft.com/en-us/library/ms680857%28VS.85%29.aspx">
 * {@code userPrincipalName}</a> or a custom {@link #setSearchFilter(String) searchFilter} in the
 * form {@code username@domain}. If the username does not already end with the domain name, the
 * {@code userPrincipalName} will be built by appending the configured domain name to the username
 * supplied in the authentication request. If no domain name is configured, it is assumed that the
 * username will always contain the domain name.
 * <p>
 * The user authorities are obtained from the data contained in the {@code memberOf} attribute.
 *
 * <h3>Active Directory Sub-Error Codes</h3>
 *
 * When an authentication fails, resulting in a standard LDAP 49 error code, Active Directory also
 * supplies its own sub-error codes within the error message. These will be used to provide
 * additional log information on why an authentication has failed. Typical examples are
 *
 * <ul>
 * <li>525 - user not found</li>
 * <li>52e - invalid credentials</li>
 * <li>530 - not permitted to logon at this time</li>
 * <li>532 - password expired</li>
 * <li>533 - account disabled</li>
 * <li>701 - account expired</li>
 * <li>773 - user must reset password</li>
 * <li>775 - account locked</li>
 * </ul>
 *
 * If you set the {@link #setConvertSubErrorCodesToExceptions(boolean)
 * convertSubErrorCodesToExceptions} property to {@code true}, the codes will also be used to
 * control the exception raised.
 *
 * @author chetanc
 * @author Luke Taylor
 * @author Rob Winch
 * @since 3.1
 */
@Order
@Log4j2
@SuppressWarnings("squid:S1149")
public class CustomActiveDirectoryLdapAuthenticationProvider extends AbstractLdapAuthenticationProvider {
    private static final Pattern SUB_ERROR_CODE = Pattern.compile(".*data\\s([0-9a-f]{3,4}).*");

    // Error codes
    private static final int PASSWORD_EXPIRED = 0x532;
    private static final int ACCOUNT_DISABLED = 0x533;
    private static final int ACCOUNT_EXPIRED = 0x701;
    private static final int ACCOUNT_LOCKED = 0x775;
    private static final String TEST_USER = "test_user";
    private final String domain;
    private final String rootDn;
    private final String url;
    // Only used to allow tests to substitute a mock LdapContext
    ContextFactory contextFactory = new ContextFactory();
    private boolean convertSubErrorCodesToExceptions;
    private String searchFilter = "(&(objectClass=user)(userPrincipalName={0}))";
    private Map<String, Object> contextEnvironmentProperties = new HashMap<>();
    private UserService userService;
    private PrivilegeService privilegeService;
    private UserLoginAttemptsService userLoginAttemptsService;

    /**
     * @param domain
     *            the domain name (may be null or empty)
     * @param url
     *            an LDAP url (or multiple URLs)
     * @param privilegeService
     *            the root DN (may be null or empty)
     */
    public CustomActiveDirectoryLdapAuthenticationProvider(UserService userService,
            UserLoginAttemptsService userLoginAttemptsService, PrivilegeService privilegeService, String domain,
            String url) {
        Assert.isTrue(StringUtils.hasText(url), "Url cannot be empty");
        this.userService = userService;
        this.userLoginAttemptsService = userLoginAttemptsService;
        this.privilegeService = privilegeService;
        this.domain = StringUtils.hasText(domain) ? domain.toLowerCase() : null;
        this.url = url;
        this.rootDn = this.domain == null ? null : rootDnFromDomain(this.domain);
    }

    /**
     * @param domain
     *            the domain name (may be null or empty)
     * @param url
     *            an LDAP url (or multiple URLs)
     */
    public CustomActiveDirectoryLdapAuthenticationProvider(String domain, String url) {
        Assert.isTrue(StringUtils.hasText(url), "Url cannot be empty");
        this.domain = StringUtils.hasText(domain) ? domain.toLowerCase() : null;
        this.url = url;
        this.rootDn = this.domain == null ? null : rootDnFromDomain(this.domain);
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken auth) {
        String username = auth.getName();
        String password = (String) auth.getCredentials();

        DirContext ctx = bindAsUser(username, password);
        try {
            DirContextOperations userDetails = searchForUser(ctx, username);
            userLoginAttemptsService.resetFailAttempts(username);
            return userDetails;
        } catch (NamingException e) {
            log.error("Failed to locate directory entry for authenticated user: " + username, e);
            throw badCredentials(e);
        } finally {
            LdapUtils.closeContext(ctx);
        }
    }

    /**
     * Creates the user authority list from the values of the {@code memberOf} attribute obtained from
     * the user's Active Directory entry.
     */
    @Override
    protected Collection<? extends GrantedAuthority> loadUserAuthorities(DirContextOperations userData, String username,
            String password) {

        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        Set<Role> roles = userService.getRoleForUser(username);

        if (null != roles) {
            Set<Privilege> privileges = new HashSet<>();
            for (Role role : roles) {
                role.setPrivileges(new HashSet<>(privilegeService.getPrivilegeByNames(role.getPrivilegeNames())));
                privileges.addAll(role.getPrivileges());
            }

            if (privileges.isEmpty()) {
                log.debug("No values for 'memberOf' attribute.");

                return AuthorityUtils.NO_AUTHORITIES;
            }

            if (log.isDebugEnabled()) {
                log.debug("'memberOf' attribute values: " + Arrays.asList(roles));
            }
            for (Privilege privilege : privileges) {
                authorities.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        } else {
            User user = new User();
            user.setName(username);
            Set<Role> userRoles = new HashSet<>();
            Role defaultRole = userService.getDefaultUserRole();
            userRoles.add(defaultRole);
            user.setRoles(userRoles);
            Set<String> userRoleName = new HashSet<>();
            userRoleName.add(defaultRole.getName());
            user.setRoleNames(userRoleName);
            try {
                userService.createUser(user);
            } catch (MangleException e) {
                log.info(String.format("New local user creation failed for username %s", username));
            }
            for (Privilege privilege : defaultRole.getPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(privilege.getName()));
            }
        }
        return authorities;
    }

    public DirContext bindAsUser(String username, String password) {
        // TODO. add DNS lookup based on domain
        final String bindUrl = url;

        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        String bindPrincipal = createBindPrincipal(username);
        env.put(Context.SECURITY_PRINCIPAL, bindPrincipal);
        env.put(Context.PROVIDER_URL, bindUrl);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.OBJECT_FACTORIES, DefaultDirObjectFactory.class.getName());
        env.putAll(this.contextEnvironmentProperties);

        try {
            return contextFactory.createContext(env);
        } catch (AuthenticationException | OperationNotSupportedException e) {
            handleBindException(bindPrincipal, e);
            throw badCredentials(e);
        } catch (NamingException e) {
            throw LdapUtils.convertLdapException(e);
        }
    }

    private void handleBindException(String bindPrincipal, NamingException exception) {
        if (log.isDebugEnabled()) {
            log.debug("Authentication for " + bindPrincipal + " failed:" + exception);
        }

        handleResolveObj(exception);

        int subErrorCode = parseSubErrorCode(exception.getMessage());

        if (subErrorCode <= 0) {
            log.debug("Failed to locate AD-specific sub-error code in message");
            return;
        }

        log.debug("Active Directory authentication failed");

        if (convertSubErrorCodesToExceptions) {
            raiseExceptionForErrorCode(subErrorCode, exception);
        }
    }

    private void handleResolveObj(NamingException exception) {
        Object resolvedObj = exception.getResolvedObj();
        boolean serializable = resolvedObj instanceof Serializable;
        if (resolvedObj != null && !serializable) {
            exception.setResolvedObj(null);
        }
    }

    private int parseSubErrorCode(String message) {
        Matcher m = SUB_ERROR_CODE.matcher(message);

        if (m.matches()) {
            return Integer.parseInt(m.group(1), 16);
        }

        return -1;
    }

    private void raiseExceptionForErrorCode(int code, NamingException exception) {
        String hexString = Integer.toHexString(code);
        Throwable cause =
                new CustomActiveDirectoryAuthenticationException(hexString, exception.getMessage(), exception);
        switch (code) {
        case PASSWORD_EXPIRED:
            throw new CredentialsExpiredException(messages.getMessage("LdapAuthenticationProvider.credentialsExpired",
                    "User credentials have expired"), cause);
        case ACCOUNT_DISABLED:
            throw new DisabledException(messages.getMessage("LdapAuthenticationProvider.disabled", "User is disabled"),
                    cause);
        case ACCOUNT_EXPIRED:
            throw new AccountExpiredException(
                    messages.getMessage("LdapAuthenticationProvider.expired", "User account has expired"), cause);
        case ACCOUNT_LOCKED:
            throw new LockedException("User account is locked by authentication provider", cause);
        default:
            throw badCredentials(cause);
        }
    }

    private BadCredentialsException badCredentials() {
        return new BadCredentialsException(
                messages.getMessage("LdapAuthenticationProvider.badCredentials", "Bad credentials"));
    }

    private BadCredentialsException badCredentials(Throwable cause) {
        return (BadCredentialsException) badCredentials().initCause(cause);
    }

    public DirContextOperations searchForUser(DirContext context, String username) throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String bindPrincipal = createBindPrincipal(username);
        String searchRoot = rootDn != null ? rootDn : searchRootFromPrincipal(bindPrincipal);

        try {
            return SpringSecurityLdapTemplate.searchForSingleEntryInternal(context, searchControls, searchRoot,
                    searchFilter, new Object[] { bindPrincipal, username });
        } catch (IncorrectResultSizeDataAccessException incorrectResults) {
            // Search should never return multiple results if properly configured - just
            // rethrow
            if (incorrectResults.getActualSize() != 0) {
                throw incorrectResults;
            }
            // If we found no results, then the username/password did not match
            UsernameNotFoundException userNameNotFoundException =
                    new UsernameNotFoundException("User " + username + " not found in directory.", incorrectResults);
            throw badCredentials(userNameNotFoundException);
        }
    }

    public boolean searchForUser(String username, String adUser, String adUserCred) {
        boolean isExist = false;
        DirContext ctx = bindAsUser(adUser, adUserCred);
        try {
            searchForUser(ctx, username);
            isExist = true;
        } catch (NamingException | BadCredentialsException e) {
            log.error("Couldn't find user {} on the AD {}", username, url);
        }
        return isExist;
    }

    private String searchRootFromPrincipal(String bindPrincipal) {
        int atChar = bindPrincipal.lastIndexOf('@');

        if (atChar < 0) {
            log.debug("User principal '" + bindPrincipal
                    + "' does not contain the domain, and no domain has been configured");
            throw badCredentials();
        }

        return rootDnFromDomain(bindPrincipal.substring(atChar + 1, bindPrincipal.length()));
    }

    private String rootDnFromDomain(String domain) {
        String[] tokens = StringUtils.tokenizeToStringArray(domain, ".");
        StringBuilder root = new StringBuilder();

        for (String token : tokens) {
            if (root.length() > 0) {
                root.append(',');
            }
            root.append("dc=").append(token);
        }

        return root.toString();
    }

    String createBindPrincipal(String username) {
        if (domain == null || username.toLowerCase().endsWith(domain)) {
            return username;
        }

        return username + "@" + domain;
    }

    /**
     * By default, a failed authentication (LDAP error 49) will result in a
     * {@code BadCredentialsException}.
     * <p>
     * If this property is set to {@code true}, the exception message from a failed bind attempt will be
     * parsed for the AD-specific error code and a {@link CredentialsExpiredException},
     * {@link DisabledException}, {@link AccountExpiredException} or {@link LockedException} will be
     * thrown for the corresponding codes. All other codes will result in the default
     * {@code BadCredentialsException}.
     *
     * @param convertSubErrorCodesToExceptions
     *            {@code true} to raise an exception based on the AD error code.
     */
    public void setConvertSubErrorCodesToExceptions(boolean convertSubErrorCodesToExceptions) {
        this.convertSubErrorCodesToExceptions = convertSubErrorCodesToExceptions;
    }

    /**
     * The LDAP filter string to search for the user being authenticated. Occurrences of {0} are
     * replaced with the {@code username@domain}. Occurrences of {1} are replaced with the
     * {@code username} only.
     * <p>
     * Defaults to: {@code (&(objectClass=user)(userPrincipalName= 0}))}
     * </p>
     *
     * @param searchFilter
     *            the filter string
     *
     * @since 3.2.6
     */
    public void setSearchFilter(String searchFilter) {
        Assert.hasText(searchFilter, "searchFilter must have text");
        this.searchFilter = searchFilter;
    }

    /**
     * Allows a custom environment properties to be used to create initial LDAP context.
     *
     * @param environment
     *            the additional environment parameters to use when creating the LDAP Context
     */
    public void setContextEnvironmentProperties(Map<String, Object> environment) {
        Assert.notEmpty(environment, "environment must not be empty");
        this.contextEnvironmentProperties = new Hashtable<>(environment);
    }

    public boolean testConnection() {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(TEST_USER, UUID.randomUUID().toString());

        try {
            authenticate(authToken);
        } catch (CommunicationException | UncategorizedLdapException e) {
            return false;
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    public boolean testConnection(String username, String password) throws MangleException {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticate(authToken);
        } catch (Exception e) {
            throw new MangleException(e.getMessage(), ErrorCode.GENERIC_ERROR);
        }
        return true;
    }

    static class ContextFactory {
        DirContext createContext(Hashtable<?, ?> env) throws NamingException {
            return new InitialLdapContext(env, null);
        }
    }
}
