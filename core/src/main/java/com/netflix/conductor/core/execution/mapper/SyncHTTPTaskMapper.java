package com.netflix.conductor.core.execution.mapper;

import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskDef;
import com.netflix.conductor.common.metadata.tasks.TaskType;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.exception.TerminateWorkflowException;
import com.netflix.conductor.core.utils.ParametersUtils;
import com.netflix.conductor.dao.MetadataDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 描述
 *
 * @author shichunqiang
 * @version 1.0
 * @date 2022/1/7 17:25
 */
@Component
public class SyncHTTPTaskMapper implements TaskMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncHTTPTaskMapper.class);

    private final ParametersUtils parametersUtils;
    private final MetadataDAO metadataDAO;

    @Autowired
    public SyncHTTPTaskMapper(ParametersUtils parametersUtils, MetadataDAO metadataDAO) {
        this.parametersUtils = parametersUtils;
        this.metadataDAO = metadataDAO;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SYNC_HTTP;
    }

    /**
     * This method maps a {@link WorkflowTask} of type {@link TaskType#HTTP} to a {@link Task} in a {@link
     * Task.Status#SCHEDULED} state
     *
     * @param taskMapperContext: A wrapper class containing the {@link WorkflowTask}, {@link WorkflowDef}, {@link
     *                           Workflow} and a string representation of the TaskId
     * @return a List with just one HTTP task
     * @throws TerminateWorkflowException In case if the task definition does not exist
     */
    @Override
    public List<Task> getMappedTasks(TaskMapperContext taskMapperContext) throws TerminateWorkflowException {

        LOGGER.debug("TaskMapperContext {} in HTTPTaskMapper", taskMapperContext);

        WorkflowTask taskToSchedule = taskMapperContext.getTaskToSchedule();
        taskToSchedule.getInputParameters().put("asyncComplete", taskToSchedule.isAsyncComplete());
        Workflow workflowInstance = taskMapperContext.getWorkflowInstance();
        String taskId = taskMapperContext.getTaskId();
        int retryCount = taskMapperContext.getRetryCount();

        TaskDef taskDefinition = Optional.ofNullable(taskMapperContext.getTaskDefinition())
                .orElseGet(() -> Optional.ofNullable(metadataDAO.getTaskDef(taskToSchedule.getName()))
                        .orElse(null));

        Map<String, Object> input = parametersUtils
                .getTaskInputV2(taskToSchedule.getInputParameters(), workflowInstance, taskId, taskDefinition);
        Boolean asynComplete = (Boolean) input.get("asyncComplete");

        Task httpTask = new Task();
        httpTask.setTaskType(taskToSchedule.getType());
        httpTask.setTaskDefName(taskToSchedule.getName());
        httpTask.setReferenceTaskName(taskToSchedule.getTaskReferenceName());
        httpTask.setWorkflowInstanceId(workflowInstance.getWorkflowId());
        httpTask.setWorkflowType(workflowInstance.getWorkflowName());
        httpTask.setCorrelationId(workflowInstance.getCorrelationId());
        httpTask.setScheduledTime(System.currentTimeMillis());
        httpTask.setTaskId(taskId);
        httpTask.setInputData(input);
        httpTask.getInputData().put("asyncComplete", asynComplete);
        httpTask.setStatus(Task.Status.SCHEDULED);
        httpTask.setRetryCount(retryCount);
        httpTask.setCallbackAfterSeconds(taskToSchedule.getStartDelay());
        httpTask.setWorkflowTask(taskToSchedule);
        httpTask.setWorkflowPriority(workflowInstance.getPriority());
        if (Objects.nonNull(taskDefinition)) {
            httpTask.setRateLimitPerFrequency(taskDefinition.getRateLimitPerFrequency());
            httpTask.setRateLimitFrequencyInSeconds(taskDefinition.getRateLimitFrequencyInSeconds());
            httpTask.setIsolationGroupId(taskDefinition.getIsolationGroupId());
            httpTask.setExecutionNameSpace(taskDefinition.getExecutionNameSpace());
        }
        return Collections.singletonList(httpTask);
    }
}
