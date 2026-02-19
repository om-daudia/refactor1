package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.exception.BpmnError;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
public class JobWorkerJobWorker {

    private final JobWorkerJobWorkerService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        final String METHOD_NAME = "JobWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace(METHOD_NAME + " started...");

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);

            log.trace(METHOD_NAME + " Finished.");
            return outputs;
        } catch (Exception e) {
            log.trace(METHOD_NAME + " Error.");
            throw new BpmnError("ERR_CODE", e.getMessage(), inputVarMap, e);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> sift(final ActivatedJob job, @Variable Integer index) {
        final String METHOD_NAME = "JobWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace(METHOD_NAME + " started...");

        try {
            long duration = DmnEvaluator.go(index);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            System.out.println("duration=" + duration);

            // log.trace(METHOD_NAME+" Finished.");
            return outputs;//n outputs;
        } catch (Exception e) {
            log.trace(METHOD_NAME + " Error.");
            throw new BpmnError("ERR_CODE", e.getMessage(), inputVarMap, e);
        }
    }
}


