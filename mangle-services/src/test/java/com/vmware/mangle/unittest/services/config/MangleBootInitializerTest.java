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

package com.vmware.mangle.unittest.services.config;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.hazelcast.config.Config;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.nio.Address;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.vmware.mangle.cassandra.model.faults.specs.TaskSpec;
import com.vmware.mangle.cassandra.model.hazelcast.HazelcastClusterConfig;
import com.vmware.mangle.cassandra.model.scheduler.SchedulerSpec;
import com.vmware.mangle.cassandra.model.tasks.Task;
import com.vmware.mangle.cassandra.model.tasks.TaskStatus;
import com.vmware.mangle.model.enums.MangleDeploymentMode;
import com.vmware.mangle.model.enums.SchedulerStatus;
import com.vmware.mangle.services.ClusterConfigService;
import com.vmware.mangle.services.SchedulerService;
import com.vmware.mangle.services.TaskService;
import com.vmware.mangle.services.config.MangleBootInitializer;
import com.vmware.mangle.services.enums.MangleQuorumStatus;
import com.vmware.mangle.services.events.web.CustomEventPublisher;
import com.vmware.mangle.services.hazelcast.HazelcastTaskCache;
import com.vmware.mangle.services.mockdata.HazelcastMockData;
import com.vmware.mangle.services.poll.PollingService;
import com.vmware.mangle.utils.constants.Constants;
import com.vmware.mangle.utils.constants.HazelcastConstants;
import com.vmware.mangle.utils.exceptions.MangleException;

/**
 * @author chetanc
 *
 *
 */
public class MangleBootInitializerTest {

    @Mock
    private TaskService taskService;

    @Mock
    private HazelcastTaskCache taskCache;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ClusterConfigService configService;

    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @Mock
    private CustomEventPublisher eventPublisher;

    private MangleBootInitializer bootInitializer;

    private HazelcastMockData mockData;

    @Mock
    private PollingService<?> pollingService;

    @BeforeMethod
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        bootInitializer = new MangleBootInitializer(taskService, taskCache, schedulerService, configService,
                eventPublisher, pollingService);
        mockData = new HazelcastMockData();
        bootInitializer.setTaskScheduler(taskScheduler);
        bootInitializer.setHazelcastInstance(hazelcastInstance);
        HazelcastConstants.setMangleQourumStatus(MangleQuorumStatus.PRESENT);
    }

    @Test
    public void testUpdateClusterConfigObject() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        HazelcastClusterConfig clusterConfig = mockData.getClusterConfig();
        Config config = new Config();
        config.setProperty(HazelcastConstants.HAZELCAST_PROPERTY_DEPLOYMENT_MODE, MangleDeploymentMode.CLUSTER.name());
        Address address = new Address("127.0.0.1", 90000);

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(configService.getClusterConfiguration()).thenReturn(clusterConfig);
        when(configService.updateClusterConfiguration(clusterConfig)).thenReturn(clusterConfig);
        when(clusterMember.getAddress()).thenReturn(address);
        when(hazelcastInstance.getConfig()).thenReturn(config);

        bootInitializer.updateClusterConfigObject();

        verify(hazelcastInstance, times(1)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(configService, times(1)).getClusterConfiguration();
        verify(configService, times(1)).updateClusterConfiguration(any());
    }

    @Test
    public void testUpdateClusterConfigObjectWithQuorumAsNull() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        HazelcastClusterConfig clusterConfig = mockData.getClusterConfig();
        Config config = new Config();
        config.setProperty(HazelcastConstants.HAZELCAST_PROPERTY_DEPLOYMENT_MODE, MangleDeploymentMode.CLUSTER.name());
        Address address = new Address("127.0.0.1", 90000);
        clusterConfig.setQuorum(0);

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(configService.getClusterConfiguration()).thenReturn(clusterConfig);
        when(configService.updateClusterConfiguration(clusterConfig)).thenReturn(clusterConfig);
        when(clusterMember.getAddress()).thenReturn(address);
        when(hazelcastInstance.getConfig()).thenReturn(config);

        bootInitializer.updateClusterConfigObject();

        verify(hazelcastInstance, times(1)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(configService, times(1)).getClusterConfiguration();
        verify(configService, times(1)).updateClusterConfiguration(any());
    }

    @Test
    public void testUpdateClusterConfigObjectWithClusterAsStandAlone() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        HazelcastClusterConfig clusterConfig = mockData.getClusterConfig();
        Config config = new Config();
        config.setProperty(HazelcastConstants.HAZELCAST_PROPERTY_DEPLOYMENT_MODE,
                MangleDeploymentMode.STANDALONE.name());
        Address address = new Address("127.0.0.1", 90000);

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(configService.getClusterConfiguration()).thenReturn(clusterConfig);
        when(configService.updateClusterConfiguration(clusterConfig)).thenReturn(clusterConfig);
        when(clusterMember.getAddress()).thenReturn(address);
        when(hazelcastInstance.getConfig()).thenReturn(config);

        bootInitializer.updateClusterConfigObject();

        verify(hazelcastInstance, times(1)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(configService, times(1)).getClusterConfiguration();
        verify(configService, times(1)).updateClusterConfiguration(any());
    }

    @Test
    public void testUpdateClusterConfigObjectQuorumNotPresent() {
        HazelcastConstants.setMangleQourumStatus(MangleQuorumStatus.NOT_PRESENT);

        bootInitializer.updateClusterConfigObject();

        verify(hazelcastInstance, times(0)).getCluster();
        verify(configService, times(0)).getClusterConfiguration();
        verify(configService, times(0)).updateClusterConfiguration(any());
    }

    @Test
    public void testUpdateClusterConfigObjectHazelcastNotInitialized() {
        bootInitializer.setHazelcastInstance(null);

        bootInitializer.updateClusterConfigObject();

        verify(hazelcastInstance, times(0)).getCluster();
        verify(configService, times(0)).getClusterConfiguration();
        verify(configService, times(0)).updateClusterConfiguration(any());
    }

    @Test
    public void testInitializeApplicationTasksEmptySchedules() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        Task<TaskSpec> task = mockData.getMockTask();
        task.setScheduledTask(true);

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);

        bootInitializer.initializeApplicationTasks();

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
    }

    @Test
    public void testInitializeApplicationTasksWithSchedules() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        SchedulerSpec schedulerSpec = mockData.getSchedulerSpec();

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);
        when(schedulerService.getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED))
                .thenReturn(Collections.singletonList(schedulerSpec));
        when(taskCache.addTaskToCache(schedulerSpec.getId(), schedulerSpec.getStatus().name()))
                .thenReturn(schedulerSpec.getId());

        bootInitializer.initializeApplicationTasks();

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(cluster, times(1)).getLocalMember();
        verify(clusterMember, times(2)).getAddress();
        verify(schedulerService, times(1)).getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED);
    }

    @Test
    public void testInitializeApplicationTasks() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        Task<TaskSpec> task = mockData.getMockTask();

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);
        when(taskService.getInProgressTasks()).thenReturn(Collections.singletonList(task));

        bootInitializer.initializeApplicationTasks();

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(cluster, times(1)).getLocalMember();
        verify(clusterMember, times(2)).getAddress();
        verify(schedulerService, times(1)).getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED);
    }

    @Test
    public void testInitializeApplicationTasksNoTriggers() throws UnknownHostException, MangleException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        Task<TaskSpec> task = mockData.getMockTask();
        task.setTriggers(null);

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);
        when(taskService.getInProgressTasks()).thenReturn(Collections.singletonList(task));
        when(taskService.addOrUpdateTask(task)).thenReturn(task);

        bootInitializer.initializeApplicationTasks();

        Assert.assertEquals(task.getTaskStatus(), TaskStatus.FAILED);

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(cluster, times(1)).getLocalMember();
        verify(clusterMember, times(2)).getAddress();
        verify(schedulerService, times(1)).getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED);
        verify(taskService, times(1)).getInProgressTasks();
        verify(taskService, times(1)).addOrUpdateTask(task);
    }

    @Test
    public void testInitializeApplicationTasksScheduledTask() throws UnknownHostException, MangleException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        Task<TaskSpec> task = mockData.getMockTask();
        task.setScheduledTask(true);

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);
        when(taskService.getInProgressTasks()).thenReturn(Collections.singletonList(task));
        when(taskService.addOrUpdateTask(task)).thenReturn(task);

        bootInitializer.initializeApplicationTasks();

        Assert.assertEquals(task.getTaskStatus(), TaskStatus.TASK_SKIPPED);

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(cluster, times(1)).getLocalMember();
        verify(clusterMember, times(2)).getAddress();
        verify(schedulerService, times(1)).getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED);
        verify(taskService, times(1)).getInProgressTasks();
        verify(taskService, times(1)).addOrUpdateTask(task);
    }

    @Test
    public void testInitializeApplicationTasksNonScheduledTaskFailed() throws UnknownHostException, MangleException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        Task<TaskSpec> task = mockData.getMockTask();
        task.setScheduledTask(false);
        SimpleDateFormat sdf = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
        task.getTriggers().peek().setStartTime(sdf.format(System.currentTimeMillis() - 3600000));

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);
        when(taskService.getInProgressTasks()).thenReturn(Collections.singletonList(task));
        when(taskService.addOrUpdateTask(task)).thenReturn(task);

        bootInitializer.initializeApplicationTasks();

        Assert.assertEquals(task.getTaskStatus(), TaskStatus.FAILED);

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(cluster, times(1)).getLocalMember();
        verify(clusterMember, times(2)).getAddress();
        verify(schedulerService, times(1)).getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED);
        verify(taskService, times(1)).getInProgressTasks();
        verify(taskService, times(1)).addOrUpdateTask(task);
    }

    @Test
    public void testInitializeApplicationTasksParseException() throws UnknownHostException, MangleException {
        Member clusterMember = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new HashSet<>(Collections.singletonList(clusterMember));
        Address address = new Address("127.0.0.1", 90000);
        Task<TaskSpec> task = mockData.getMockTask();
        task.setScheduledTask(false);
        task.getTriggers().peek().setStartTime(String.valueOf(System.currentTimeMillis() - 3600000));

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(clusterMember.getAddress()).thenReturn(address);
        when(taskService.getInProgressTasks()).thenReturn(Collections.singletonList(task));
        when(taskService.addOrUpdateTask(task)).thenReturn(task);

        bootInitializer.initializeApplicationTasks();

        verify(hazelcastInstance, times(2)).getCluster();
        verify(cluster, times(1)).getMembers();
        verify(cluster, times(1)).getLocalMember();
        verify(clusterMember, times(2)).getAddress();
        verify(schedulerService, times(1)).getAllScheduledJobByStatus(SchedulerStatus.SCHEDULED);
        verify(taskService, times(1)).getInProgressTasks();
        verify(taskService, times(0)).addOrUpdateTask(task);
    }

    @Test
    public void testRemoveCurrentClusterNodesFromClusterConfig() throws UnknownHostException {
        Member clusterMember = mock(Member.class);
        Member clusterMember1 = mock(Member.class);
        Cluster cluster = mock(Cluster.class);
        Set<Member> members = new LinkedHashSet<>();
        members.add(clusterMember);
        members.add(clusterMember1);
        HazelcastClusterConfig clusterConfig = mockData.getClusterConfig();
        Config config = new Config();
        config.setProperty(HazelcastConstants.HAZELCAST_PROPERTY_DEPLOYMENT_MODE, MangleDeploymentMode.CLUSTER.name());
        Address address = new Address("127.0.0.1", 90000);
        Address address1 = new Address("10.196.173.33", 90000);
        Set<String> member = new LinkedHashSet<>();
        member.add(address.getHost());
        member.add(address1.getHost());
        clusterConfig.setMembers(member);
        clusterConfig.setMaster(address.getHost());

        when(hazelcastInstance.getCluster()).thenReturn(cluster);
        when(cluster.getMembers()).thenReturn(members);
        when(cluster.getLocalMember()).thenReturn(clusterMember);
        when(configService.getClusterConfiguration()).thenReturn(clusterConfig);
        when(configService.updateClusterConfiguration(clusterConfig)).thenReturn(clusterConfig);
        when(clusterMember.getAddress()).thenReturn(address);
        when(clusterMember1.getAddress()).thenReturn(address1);
        when(hazelcastInstance.getConfig()).thenReturn(config);

        bootInitializer.removeCurrentClusterNodesFromClusterConfig();

        verify(hazelcastInstance, times(4)).getCluster();
        verify(cluster, times(2)).getMembers();
        verify(configService, times(1)).getClusterConfiguration();
        verify(configService, times(1)).updateClusterConfiguration(any());
    }
}
