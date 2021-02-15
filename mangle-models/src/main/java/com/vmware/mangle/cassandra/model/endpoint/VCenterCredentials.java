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

package com.vmware.mangle.cassandra.model.endpoint;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.cassandra.core.mapping.Table;

import com.vmware.mangle.model.enums.EncryptField;
import com.vmware.mangle.model.enums.EndpointType;

/**
 * VCenter Credentials model class
 *
 * @author kumargautam
 */
@Table(value = "CredentialsSpec")
@Data
@EqualsAndHashCode(callSuper = true)
public class VCenterCredentials extends CredentialsSpec {

    private static final long serialVersionUID = 1L;
    @NotEmpty
    private String userName;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @EncryptField
    @NotEmpty
    private String password;

    public VCenterCredentials() {
        setType(EndpointType.VCENTER);
    }
}
