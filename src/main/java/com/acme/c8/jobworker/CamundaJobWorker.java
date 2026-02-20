package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnEvaluator;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.exception.BpmnError;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@AllArgsConstructor
public class CamundaJobWorker {

    private final CamundaJobWorkerService service;
    private final DmnEvaluator dmnEvaluator;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.info("findUser started, userId={}", userId);

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);
            log.info("findUser finished, userId={}", userId);
            return outputs;
        } catch (Exception e) {
            log.error("findUser failed, userId={}", userId, e);
            throw new BpmnError("ERR_CODE", e.getMessage(), inputVarMap, e);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> shift(final ActivatedJob job, @Variable Integer index) {
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.info("shift started, index={}", index);

        try {
            long duration = dmnEvaluator.go(index);

            Map<String, Object> outputs = new HashMap<>();
            outputs.put("duration", duration);
            log.info("shift finished, index={}, duration={}s", index, duration);
            return outputs;
        } catch (Exception e) {
            log.error("shift failed, index={}", index, e);
            throw new BpmnError("ERR_CODE", e.getMessage(), inputVarMap, e);
        }
    }
}
