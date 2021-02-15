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

package com.vmware.mangle.cassandra.model.security;

import java.util.UUID;

import lombok.Data;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

/**
 *
 *
 * @author chetanc
 */
@Data
public class ADAuthProviderDtoV1 {

    private String id;

    private String adUrl;
    @PrimaryKeyColumn(value = "adDomain", ordering = Ordering.ASCENDING, type = PrimaryKeyType.PARTITIONED)
    private String adDomain;

    public ADAuthProviderDtoV1() {
        this.id = UUID.randomUUID().toString();
    }
}
