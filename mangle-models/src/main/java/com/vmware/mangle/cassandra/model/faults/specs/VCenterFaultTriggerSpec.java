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

package com.vmware.mangle.cassandra.model.faults.specs;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chetanc
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VCenterFaultTriggerSpec extends MultiTaskSpec implements Serializable {
    protected static final long serialVersionUID = 1L;

    private String childSpecType;

    private CommandExecutionFaultSpec faultSpec;

    private List<String> vms;

    @ApiModelProperty(value = "Enable/Disable Random injection", example = "true")
    @NotNull
    private Boolean enableRandomInjection = true;

    @JsonIgnore
    private Map<String, String> childTaskMap;

    public VCenterFaultTriggerSpec() {
        setSpecType(this.getClass().getName());
    }
}
