package com.acme.c8.jobworker.handler;

import com.acme.c8.jobworker.service.JobWorkerService;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.exception.BpmnError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskHandler {

    private final JobWorkerService jobWorkerService;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        log.info("findUser started for userId={}", userId);
        try {
            Map<String, Object> outputs = jobWorkerService.findUser(userId);
            log.info("findUser completed for userId={}", userId);
            return outputs;
        } catch (Exception e) {
            log.error("findUser failed for userId={}", userId, e);
            throw new BpmnError("ERR_CODE", e.getMessage());
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> filterPatients(final ActivatedJob job, @Variable Integer index) {
        log.info("filterPatients started for index={}", index);
        try {
            Map<String, Object> outputs = jobWorkerService.filterPatients(index);
            log.info("filterPatients completed, duration={}s", outputs.get("duration"));
            return outputs;
        } catch (Exception e) {
            log.error("filterPatients failed for index={}", index, e);
            throw new BpmnError("ERR_CODE", e.getMessage());
        }
    }
}
