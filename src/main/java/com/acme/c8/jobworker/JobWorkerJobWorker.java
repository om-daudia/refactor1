package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class JobWorkerJobWorker {

    private static final String FIND_USER_METHOD_NAME = "JobWorker.findUser";
    private static final String SIFT_METHOD_NAME= "JobWorker.findUser";

    JobWorkerJobWorkerService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable @NotNull(message="userId cannot be null") final String userId) throws ZeebeBpmnError {
    Map<String,Object> inputVarMap = job.getVariablesAsMap();
        if (log.isTraceEnabled()) {
            log.trace("{} {} started...", FIND_USER_METHOD_NAME, job.getKey());
        }
        try {
            Map<String, Object> outputs = service.findUser(userId);
            if (log.isTraceEnabled()) {
                log.trace("{} {} Finished.", FIND_USER_METHOD_NAME, job.getKey());
            }
            return outputs;
        } catch (Exception e) {
            log.error("{} {} Error.", FIND_USER_METHOD_NAME, job.getKey(), e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> sift(final ActivatedJob job, @Variable @NotNull(message="index cannot be null") @Positive(message="index must be positive") Integer index) throws ZeebeBpmnError {
        Map<String,Object> inputVarMap = job.getVariablesAsMap();
        if (log.isTraceEnabled()) {
            log.trace("{} {} started...", SIFT_METHOD_NAME, job.getKey());
        }
        try {
           long duration= DmnEvaluator.go(index);
           Map<String, Object> outputs = new HashMap<>();
           outputs.put("duration", duration);
           if (log.isTraceEnabled()) {
               log.trace("{} {} finished... duration={}", SIFT_METHOD_NAME, job.getKey(), duration);
           }
           return outputs;//n outputs;
        } catch (Exception e) {
            log.error("{} {} Error.", SIFT_METHOD_NAME, job.getKey(), e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(),inputVarMap);
        }
    }
}


