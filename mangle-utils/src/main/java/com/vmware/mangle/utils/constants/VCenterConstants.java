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

package com.vmware.mangle.utils.constants;

/**
 * @author chetanc
 *
 *         Constants that are used in VCenter Adapter Faults
 */

public class VCenterConstants {

    private VCenterConstants() {
    }

    private static final String DISCONNECT_CMD = "disconnect";
    private static final String CONNECT_CMD = "connect";
    public static final String VC_ADAPTER_CONTEXT_PATH = "mangle-vc-adapter";
    public static final String VC_ADAPTER_HEALTH_CHECK = VC_ADAPTER_CONTEXT_PATH + "/application/health";
    public static final String TEST_CONNECTION = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/testconnection";
    public static final String VMS_LIST_QUERY = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm";
    public static final String VMS_LIST_QUERY_BY_VM = VMS_LIST_QUERY + "/%s";
    public static final String HOST_LIST_QUERY = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/host";
    public static final String HOST_DISCONNECT =
            VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/host/id/%s/" + DISCONNECT_CMD;
    public static final String HOST_CONNECT = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/host/id/%s/" + CONNECT_CMD;
    public static final String HOST_LIST_QUERY_BY_HOST = HOST_LIST_QUERY + "/name?hostName=%s";
    public static final String POWER_OFF = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/id/%s/poweroff";
    public static final String POWER_ON = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/id/%s/poweron";
    public static final String SUSPEND_VM = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/id/%s/suspend";
    public static final String RESET_VM = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/id/%s/reset";
    public static final String VM_NIC = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/%s/nic";
    public static final String VM_NIC_WITH_ID = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/id/%s/nic";
    public static final String VM_DISK = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/%s/disk";
    public static final String VM_DISK_WITH_ID = VC_ADAPTER_CONTEXT_PATH + "/api/v1/vcenter/vm/id/%s/disk";
    public static final String DISCONNECT_NIC = VM_NIC + "/%s/" + DISCONNECT_CMD;
    public static final String DISCONNECT_NIC_WITH_ID = VM_NIC_WITH_ID + "/%s/" + DISCONNECT_CMD;
    public static final String CONNECT_NIC = VM_NIC + "/%s/" + CONNECT_CMD;
    public static final String CONNECT_NIC_WITH_ID = VM_NIC_WITH_ID + "/%s/" + CONNECT_CMD;
    public static final String DISCONNECT_DISK = VM_DISK + "/%s/" + DISCONNECT_CMD;
    public static final String DISCONNECT_DISK_WITH_ID = VM_DISK_WITH_ID + "/%s/" + DISCONNECT_CMD;
    public static final String CONNECT_DISK = VM_DISK + "/%s/" + CONNECT_CMD;
    public static final String CONNECT_DISK_WITH_ID = VM_DISK_WITH_ID + "/%s/" + CONNECT_CMD;
    public static final String TASK_STATUS = VC_ADAPTER_CONTEXT_PATH + "/api/v1/task/%s";

    public static final String TASK_STATUS_COMPLETED = "Completed";
    public static final String TASK_STATUS_FAILED = "Failed";
    public static final String TASK_STATUS_TRIGGERED = "Triggered";

    public static final String ALREADY_POWERED_OFF = "Virtual machine is already powered off";
    public static final String ALREADY_POWERED_ON = "Virtual machine is already powered on";
}
