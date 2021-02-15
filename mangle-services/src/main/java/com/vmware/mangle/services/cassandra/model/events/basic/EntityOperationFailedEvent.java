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

package com.vmware.mangle.services.cassandra.model.events.basic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.data.cassandra.core.mapping.Table;

import com.vmware.mangle.model.enums.EventType;

/**
 * @author chetanc
 */
@Table(value = "Event")
@Data
@EqualsAndHashCode(callSuper = true)
public class EntityOperationFailedEvent extends Event implements ResolvableTypeProvider {

    private static final long serialVersionUID = 1L;
    private String entityId;
    private String entityClass;

    public EntityOperationFailedEvent(String entityId, String operation, String entityClass) {
        super(EventType.ENTITY_OPERATION_FAILED_EVENT.getName(),
                operation + " operation failed: Entity: " + entityClass + " With Id: " + entityId);
        this.entityId = entityId;
        this.entityClass = entityClass;
    }

    public EntityOperationFailedEvent() {
        super();
    }

    @Override
    @JsonIgnore
    public ResolvableType getResolvableType() {
        return ResolvableType.forClass(getClass());
    }
}
