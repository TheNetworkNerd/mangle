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
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.vmware.mangle.cassandra.model.security.ADAuthProviderDto;
import com.vmware.mangle.cassandra.model.security.ADAuthProviderDtoV1;
import com.vmware.mangle.utils.ReadProperty;
import com.vmware.mangle.utils.constants.Constants;

/**
 *
 *
 * @author chetanc
 */
public class AuthProviderMockData {

    private String id1;
    private String id2;
    private String adUrl;
    private String newAdUrl;
    private String adDomain1;
    private String adDomain2;
    private static final String adDomain = "domain.com";
    private static final String adUser = "dummyUser";
    private static final String adUserPassword = "dummyPassword";
    private static final String adId = "_12345";
    private static final String adDummyUser = "dummyUser1@domain.com";

    public AuthProviderMockData() {
        Properties properties = ReadProperty.readProperty(Constants.MOCKDATA_FILE);

        this.id1 = UUID.randomUUID().toString();
        this.id2 = UUID.randomUUID().toString();
        this.adUrl = properties.getProperty("adUrl1");
        this.newAdUrl = properties.getProperty("adUrl2");
        this.adDomain1 = properties.getProperty("adDomain1");
        this.adDomain2 = properties.getProperty("adDomain1");
    }

    public ADAuthProviderDto getADAuthProviderDto() {
        ADAuthProviderDto adAuthProviderDto = new ADAuthProviderDto();
        adAuthProviderDto.setId(adDomain1);
        adAuthProviderDto.setAdDomain(adDomain1);
        adAuthProviderDto.setAdUrl(adUrl);
        return adAuthProviderDto;
    }

    public ADAuthProviderDtoV1 getADAuthProviderDtoV1() {
        ADAuthProviderDtoV1 adAuthProviderDto = new ADAuthProviderDtoV1();
        adAuthProviderDto.setId(adDomain1);
        adAuthProviderDto.setAdDomain(adDomain1);
        adAuthProviderDto.setAdUrl(adUrl);
        return adAuthProviderDto;
    }

    public ADAuthProviderDto getNewADAuthProviderDto() {
        ADAuthProviderDto adAuthProviderDto = new ADAuthProviderDto();
        adAuthProviderDto.setId(adDomain2);
        adAuthProviderDto.setAdDomain(adDomain2);
        adAuthProviderDto.setAdUrl(newAdUrl);
        return adAuthProviderDto;
    }

    public ADAuthProviderDtoV1 getNewADAuthProviderDtoV1() {
        ADAuthProviderDtoV1 adAuthProviderDto = new ADAuthProviderDtoV1();
        adAuthProviderDto.setId(adDomain2);
        adAuthProviderDto.setAdDomain(adDomain2);
        adAuthProviderDto.setAdUrl(newAdUrl);
        return adAuthProviderDto;
    }

    public List<String> getListOfStrings() {
        List<String> lists = new ArrayList<>();
        lists.add(UUID.randomUUID().toString());
        lists.add(UUID.randomUUID().toString());
        lists.add(UUID.randomUUID().toString());
        return lists;
    }

    public ADAuthProviderDto getDummyAuthProvider() {
        ADAuthProviderDto adAuthProviderDto = new ADAuthProviderDto();
        adAuthProviderDto.setId(adId);
        adAuthProviderDto.setAdDomain(adDomain);
        adAuthProviderDto.setAdUrl(adUrl);
        adAuthProviderDto.setAdUser(adUser);
        adAuthProviderDto.setAdUserPassword(adUserPassword);
        return adAuthProviderDto;
    }
}
