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

package com.vmware.mangle.unittest.configuration;

import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.vmware.mangle.configuration.ApplicationConfig;

/**
 * @author Chethan C(chetanc)
 */
public class  ApplicationConfigTest {

    private ApplicationConfig applicationConfig = new ApplicationConfig();

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPasswordEncoderInitialization() {
        PasswordEncoder passwordEncoder = applicationConfig.passwordEncoder();
        Assert.isInstanceOf(BCryptPasswordEncoder.class, passwordEncoder);
    }

}
