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

package com.vmware.mangle.services.enums;

/**
 * @author jayasankarr
 *
 */
public enum FaultName {

    CPUFAULT("cpuFault", "cpuburn.sh"),
    MEMORYFAULT("memoryFault", "memoryspike.sh"),
    DISKFAULT("diskFault", "ioburn.sh"),
    FILEHANDLERFAULT("fileHandlerFault", "filehandlerleak.sh"),
    NETWORKFAULT_WINDOWS("networkFault_windows", "networklatency_win.bat"),
    NETWORKFAULT("networkFault", "networkFault.sh"),
    DISKSPACEFAULT_WINDOWS("diskSpaceFault_windows", "diskspace_win.bat"),
    DISKSPACEFAULT("diskSpaceFault", "diskspace.sh"),
    KILLPROCESSFAULT("killProcessFault", "killprocess.sh"),
    STOPSERVICEFAULT("stopServiceFault", "stopservice.sh"),
    KILLPROCESSFAULT_WINDOWS("killProcessFault_windows", "killprocess.bat"),
    DISKFUSEFAULT("diskFUSEFault", "diskFUSEfault.py"),
    DBCONNECTIONLEAKFAULT_POSTGRES("dbConnectionLeakFault_postgres", "pgdbconnectionleak.sh"),
    DBCONNECTIONLEAKFAULT_MONGODB("dbConnectionLeakFault_mongodb",  "mongodbconnectionleak.sh"),
    DBTRANSACTIONLATENCYFAULT_POSTGRES("dbTransactionLatencyFault_postgres", "pgdbtransactionlatency.sh"),
    DBTRANSACTIONERRORFAULT_POSTGRES("dbTransactionErrorFault_postgres", "pgdbtransactionerror.sh"),
    DBCONNECTIONLEAKFAULT_CASSANDRA("dbConnectionLeakFault_cassandra",  "cassandradbconnectionleak.sh"),
    THREADLEAKFAULT("threadLeakFault"),
    KERNELPANICFAULT("kernelPanicFault","kernelpanicfault.sh"),
    CLOCKSKEWFAULT("clockSkewFault","clockskew.sh"),
    NETWORKPARTITIONFAULT("networkPartitionFault","networkpartition.sh");

    private String value;
    private String[] scriptFileNames;

    FaultName(final String value, final String... scriptFileNames) {
        this.value = value;
        this.scriptFileNames = scriptFileNames;
    }

    public String getValue() {
        return value;
    }

    public String[] getScriptFileNames() {
        return scriptFileNames;
    }

    public String getScriptFileName() {
        return scriptFileNames[0];
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
