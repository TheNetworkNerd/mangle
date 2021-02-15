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

package com.vmware.mangle.utils;

import org.springframework.util.StringUtils;

/**
 * @author chetanc
 *
 */
public class VCenterAPIEndpoints {
    public static final String REST_VC = "/rest/vcenter";
    public static final String REST_SESSION = "/rest/com/vmware/cis/session";
    public static final String REST_LIBRARY = "/rest/com/vmware/content/library";
    public static final String REST_LIBRARY_ITEM = REST_LIBRARY + "/item";
    public static final String REST_LIBRARY_ITEM_SESSION = REST_LIBRARY_ITEM + "/update-session";
    public static final String REST_LIBRARY_FILE_SESSION = REST_LIBRARY_ITEM + "/updatesession/file/id:";
    public static final String ACTION_ADD = "?~action=add";
    public static final String ACTION_COMPLETE = "?~action=complete";
    public static final String ACTION_VALIDATE = "?~action=validate";
    public static final String REST_VC_VM = REST_VC + "/vm";
    public static final String REST_VC_VM_POWER_STATE = REST_VC + "/vm/%s/power";
    public static final String REST_VC_HOST = REST_VC + "/host";
    public static final String REST_VC_DATCENTER = REST_VC + "/datacenter";
    public static final String REST_VC_FOLDER = REST_VC + "/folder?filter.type=%s";
    public static final String REST_VC_CLUSTER = REST_VC + "/cluster";
    public static final String REST_VC_NETWORK = REST_VC + "/network";
    public static final String REST_VC_DATASTORE = REST_VC + "/datastore";
    public static final String REST_VC_RESOURCE_POOL = REST_VC + "/resource-pool";
    public static final String REST_VC_VM_CPU = "/hardware/cpu";
    public static final String REST_VC_VM_MEMORY = "/hardware/memory";
    public static final String REST_VC_VM_DISK = REST_VC_VM + "/%s/hardware/disk";
    public static final String REST_VC_VM_DISK_OBJ = REST_VC_VM + "/%s/hardware/disk/%s";
    public static final String REST_VC_VM_NETWORK = REST_VC_VM + "/%s/hardware/ethernet";
    public static final String REST_HEALTH_CHECK = "/rest/appliance/health/system";
    public static final String REST_VC_VM_POWER = REST_VC_VM + "/%s/power/%s";
    public static final String REST_VC_HOST_DISCONNECT = REST_VC_HOST + "/%s/disconnect";
    public static final String REST_VC_HOST_CONNECT = REST_VC_HOST + "/%s/connect";
    public static final String REST_VC_HOST_BY_ID = REST_VC_HOST + "?filter.hosts=%s";
    private static final String FILTER_FOLDER = "filter.folders";
    private static final String FILTER_DATA_CENTER = "filter.datacenters";
    private static final String FILTER_CLUSTER = "filter.clusters";
    private static final String FILTER_HOST = "filter.hosts";
    private static final String FILTER_RESOURCE_POOL = "filter.resource_pools";
    private static final String SIGN_EQUALS = "=";

    private VCenterAPIEndpoints() {
    }

    public static String addFolderFilter(String folderName) {
        return StringUtils.hasText(folderName) ? FILTER_FOLDER + SIGN_EQUALS + folderName : "";
    }

    public static String addResourcePoolFilter(String resourcePoolName) {
        return StringUtils.hasText(resourcePoolName) ? FILTER_RESOURCE_POOL + SIGN_EQUALS + resourcePoolName : "";
    }

    public static String addDCFilter(String dataCenterName) {
        return StringUtils.hasText(dataCenterName) ? FILTER_DATA_CENTER + SIGN_EQUALS + dataCenterName : "";
    }

    public static String addClusterFilter(String clusterName) {
        return StringUtils.hasText(clusterName) ? FILTER_CLUSTER + SIGN_EQUALS + clusterName : "";
    }

    public static String addHostFilter(String folderName) {
        return StringUtils.hasText(folderName) ? FILTER_HOST + SIGN_EQUALS + folderName : "";
    }
}
