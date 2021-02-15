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

package com.vmware.mangle.services.mockdata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vmware.mangle.cassandra.model.security.ADAuthProviderDto;
import com.vmware.mangle.cassandra.model.security.PasswordReset;
import com.vmware.mangle.cassandra.model.security.Privilege;
import com.vmware.mangle.cassandra.model.security.Role;
import com.vmware.mangle.cassandra.model.security.User;
import com.vmware.mangle.cassandra.model.security.UserLoginAttempts;
import com.vmware.mangle.model.UserCreationDTO;
import com.vmware.mangle.model.UserPasswordUpdateDTO;
import com.vmware.mangle.model.UserRolesUpdateDTO;
import com.vmware.mangle.utils.constants.Constants;

/**
 *
 *
 * @author chetanc
 */
public class UserMockData {
    private static final String USER1 = "DUMMY_USER@" + Constants.LOCAL_DOMAIN_NAME;
    private static final String USER2 = "DUMMY_USER2@" + Constants.LOCAL_DOMAIN_NAME;
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String pwd = UUID.randomUUID().toString();
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final String adDomain = "domain.com";
    private static final String adUrl = "ldap://127.0.0.1:389";
    private static final String adUser = "dummyUser";
    private static final String adUserPassword = "dummyPassword";
    private static final String adId = "_12345";
    private static final String adDummyUser = "dummyUser1@domain.com";

    private RolesMockData rolesMockData = new RolesMockData();

    public List<String> getUsersList() {
        return new ArrayList<>(Arrays.asList(USER1, USER2));
    }

    public User getMockUser() {
        return new User(USER1, pwd, getDummyRole());
    }

    public UserCreationDTO getUserCreationMockDTO() {
        UserCreationDTO userCreationDTO = new UserCreationDTO();
        userCreationDTO.setName(USER1);
        userCreationDTO.setPassword(pwd);
        userCreationDTO.setRoleNames(new HashSet<>(Arrays.asList(ROLE_ADMIN, ROLE_USER)));
        return userCreationDTO;
    }

    public UserRolesUpdateDTO getUserUpdateDTO() {
        UserRolesUpdateDTO userUpdateDTO = new UserRolesUpdateDTO();
        userUpdateDTO.setName(USER1);
        userUpdateDTO.setAccountLocked(false);
        userUpdateDTO.setRoleNames(new HashSet<>(Arrays.asList(ROLE_ADMIN, ROLE_USER)));
        return userUpdateDTO;
    }

    public Role getDummyRole() {
        Role role = new Role();
        role.setName(ROLE_ADMIN);
        return role;

    }

    public Role getDummyRole3() {
        Role role = new Role();
        role.setName(ROLE_ADMIN);
        Set<Privilege> privilegeSet = new HashSet<>();
        privilegeSet.add(rolesMockData.getDummyPrivilege());
        privilegeSet.add(rolesMockData.getDummy2Privilege());
        role.setPrivileges(privilegeSet);
        role.setPrivilegeNames(privilegeSet.stream().map(Privilege::getName).collect(Collectors.toSet()));
        return role;

    }

    public User getMockUser2() {
        Role role = new Role();
        role.setName(ROLE_ADMIN);
        User user = new User(USER2, pwd, role);
        return user;
    }

    public List<PasswordReset> getPasswordResetListTrue() {
        List<PasswordReset> passwordList = new ArrayList<>();
        PasswordReset element = new PasswordReset();
        element.setReset(true);
        passwordList.add(element);
        passwordList.set(0, element);
        return passwordList;

    }

    public List<PasswordReset> getPasswordResetListFalse() {
        List<PasswordReset> passwordList = new ArrayList<>();
        PasswordReset element = new PasswordReset();
        element.setReset(false);
        passwordList.add(element);
        passwordList.set(0, element);
        return passwordList;

    }

    public User getMockUser3() {
        User user = new User(USER1, pwd, getDummyRole3());
        return user;
    }

    public User getUpdateMockUser() {
        Role role = new Role();
        role.setName(ROLE_USER);
        User user = new User(USER1, pwd, role);
        return user;
    }

    public UserLoginAttempts getUserLoginAttemptsForUser(String username) {
        return new UserLoginAttempts(username, 3, new Date());
    }

    public User getLockedMockUser() {
        User user = getMockUser();
        user.setAccountLocked(true);
        return user;
    }

    public UserPasswordUpdateDTO getUserPasswordUpdateDTO() {
        return new UserPasswordUpdateDTO(pwd, pwd);
    }

    public UserDetails getMockUserDetails() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(rolesMockData.getDummyPrivilege().getName()));
        return new org.springframework.security.core.userdetails.User(USER1, passwordEncoder.encode(pwd), true, true,
                true, true, authorities);
    }

    public UserDetails getMockLockedUserDetails() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(rolesMockData.getDummyPrivilege().getName()));
        return new org.springframework.security.core.userdetails.User(USER1, passwordEncoder.encode(pwd), true, true,
                true, false, authorities);
    }

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public User getDummyADUser() {
        User user = new User();
        user.setName(adDummyUser);
        return user;
    }

    public ADAuthProviderDto getDummyAuthProvider(User user) {
        ADAuthProviderDto adAuthProviderDto = new ADAuthProviderDto();
        adAuthProviderDto.setId(adId);
        adAuthProviderDto.setAdDomain(adDomain);
        adAuthProviderDto.setAdUrl(adUrl);
        adAuthProviderDto.setAdUser(user.getName());
        adAuthProviderDto.setAdUserPassword(adUserPassword);
        return adAuthProviderDto;
    }

    public ADAuthProviderDto getDummyAuthProviderForV1() {
        ADAuthProviderDto adAuthProviderDto = new ADAuthProviderDto();
        adAuthProviderDto.setId(adId);
        adAuthProviderDto.setAdDomain(adDomain);
        adAuthProviderDto.setAdUrl(adUrl);
        return adAuthProviderDto;
    }
}
