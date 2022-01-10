package com.netflix.conductor.contribs.tasks.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 同步调用http任务
 *
 * @author shichunqiang
 * @version 1.0
 * @date 2022/1/7 14:32
 */
@Component(SyncHttpTask.NAME)
public class SyncHttpTask extends HttpTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncHttpTask.class);
    private final String requestParameter;

    public static final String NAME = "SYNC_HTTP";
    @Autowired
    public SyncHttpTask(RestTemplateProvider restTemplateProvider, ObjectMapper objectMapper) {
        this(NAME, restTemplateProvider, objectMapper);
    }


    public SyncHttpTask(String name,
                    RestTemplateProvider restTemplateProvider,
                    ObjectMapper objectMapper) {
       super(name, restTemplateProvider, objectMapper);
        this.restTemplateProvider = restTemplateProvider;
        this.objectMapper = objectMapper;
        this.requestParameter = REQUEST_PARAMETER_NAME;
        LOGGER.info("{} initialized...", getTaskType());
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public void start(Workflow workflow, Task task, WorkflowExecutor executor) {
        super.start(workflow, task, executor);
    }
}
