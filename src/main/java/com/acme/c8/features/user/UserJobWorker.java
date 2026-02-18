package com.acme.c8.features.user;

import com.acme.c8.jobworker.JobWorkerJobWorkerService;

import io.camunda.zeebe.spring.common.exception.ZeebeBpmnError;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;


public class UserJobWorker {

        private final UserService service;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable  String userId) {
    final String  METHOD_NAME= "JobWorker.findUser";
    Map<String,Object> inputVarMap = job.getVariablesAsMap();
        log.trace(METHOD_NAME+" started...");

        try {
            Map<String, Object> outputs = service.findUserImpl(userId);

            log.trace(METHOD_NAME+" Finished.");
            return outputs;
        } catch (Exception e) {
            log.trace(METHOD_NAME+" Error.");
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(),inputVarMap);
        }
    }

}
