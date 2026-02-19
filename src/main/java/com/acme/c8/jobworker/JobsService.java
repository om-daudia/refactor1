package com.acme.c8.jobworker;

import com.acme.c8.evaluator.DmnEvaluator;
import com.acme.c8.user.UserService;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.annotation.Variable;
import io.camunda.client.exception.BpmnError;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobsService {

    private final UserService userService;

    private final DmnEvaluator dmnEvaluator;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        final String METHOD_NAME = "JobWorker.findUser";
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        log.trace(METHOD_NAME + " started...");

        try {
            Map<String, Object> outputs = userService.findUserImpl(userId);

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
            long duration = dmnEvaluator.go(index);

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


