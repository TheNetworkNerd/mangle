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

package com.vmware.mangle.services.deletionutils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.OperationType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.vmware.mangle.cassandra.model.faults.specs.TaskSpec;
import com.vmware.mangle.cassandra.model.scheduler.SchedulerSpec;
import com.vmware.mangle.cassandra.model.tasks.Task;
import com.vmware.mangle.cassandra.model.tasks.TaskStatus;
import com.vmware.mangle.model.response.DeleteOperationResponse;
import com.vmware.mangle.services.SchedulerService;
import com.vmware.mangle.services.cassandra.model.events.basic.EntityOperationFailedEvent;
import com.vmware.mangle.services.events.web.CustomEventPublisher;
import com.vmware.mangle.services.repository.TaskRepository;
import com.vmware.mangle.utils.constants.ErrorConstants;
import com.vmware.mangle.utils.exceptions.MangleException;
import com.vmware.mangle.utils.exceptions.handler.ErrorCode;

/**
 *
 *
 * @author chetanc
 */
@Component
@Log4j2
public class TaskDeletionService {

    private TaskRepository taskRepository;
    private SchedulerService schedulerService;
    private CustomEventPublisher eventPublisher;

    @Autowired
    public TaskDeletionService(TaskRepository taskRepository, SchedulerService schedulerService,
            CustomEventPublisher eventPublisher) {
        this.taskRepository = taskRepository;
        this.schedulerService = schedulerService;
        this.eventPublisher = eventPublisher;
    }

    public DeleteOperationResponse deleteTasksByIds(List<String> taskIds) throws MangleException {
        log.info("Deleting Tasks by ids : " + taskIds);

        DeleteOperationResponse deleteResponse = new DeleteOperationResponse();
        if (!CollectionUtils.isEmpty(taskIds)) {
            List<SchedulerSpec> activeSchedules = schedulerService.getActiveSchedulesForIds(taskIds);
            Map<String, List<String>> associations = new HashMap<>();

            for (SchedulerSpec spec : activeSchedules) {
                associations.put(spec.getId(), Arrays.asList(spec.getStatus().name()));
            }

            deleteResponse.setAssociations(associations);
            if (CollectionUtils.isEmpty(deleteResponse.getAssociations())) {
                List<Task<TaskSpec>> persistedTasks = taskRepository.findByIds(taskIds);
                List<String> persistedTaskIds = persistedTasks.stream().map(Task::getId).collect(Collectors.toList());
                taskIds.removeAll(persistedTaskIds);
                if (!taskIds.isEmpty()) {
                    throw new MangleException(ErrorCode.NO_RECORD_FOUND, ErrorConstants.TASK, taskIds.toString());
                }

                taskRepository.deleteByIdIn(persistedTaskIds);
            } else {
                deleteResponse.setResponseMessage(ErrorConstants.TASK_DELETION_PRECHECK_FAIL);
            }
            return deleteResponse;
        } else {
            log.warn(ErrorConstants.TASK_ID + ErrorConstants.FIELD_VALUE_EMPTY);
            throw new MangleException(ErrorCode.FIELD_VALUE_EMPTY, ErrorConstants.TASK_ID);
        }
    }


    public boolean deleteTaskById(String taskId) throws MangleException {
        log.info("Deleting Task by id : " + taskId);
        if (!StringUtils.isEmpty(taskId)) {
            Task<TaskSpec> task = taskRepository.findById(taskId).orElse(null);
            if (task != null && task.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                eventPublisher.publishAnEvent(new EntityOperationFailedEvent(taskId,
                        OperationType.DELETE.name(), task.getClass().getName()));
                throw new MangleException(String.format(ErrorConstants.INPROGRESS_TASK_DELETION_FAILURE, taskId),
                        ErrorCode.INPROGRESS_TASK_DELETION_FAILURE, taskId);
            }
            taskRepository.deleteByIdIn(Collections.singleton(taskId));
            return true;
        } else {
            log.warn(ErrorConstants.TASK_ID + ErrorConstants.FIELD_VALUE_EMPTY);
            throw new MangleException(ErrorCode.FIELD_VALUE_EMPTY, ErrorConstants.TASK_ID);
        }
    }
}
