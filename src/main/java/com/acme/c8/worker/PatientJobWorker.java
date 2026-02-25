package com.acme.c8.worker;

import com.acme.c8.service.PatientService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.spring.client.annotation.JobWorker;
import io.camunda.spring.client.annotation.Variable;
import io.camunda.spring.client.exception.ZeebeBpmnError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PatientJobWorker {

    private final PatientService patientService;

    @JobWorker(type = "com.capbpm.c8.JobWorker.FindUser:v.1.1", fetchVariables = {"userId"})
    public Map<String, Object> findUser(final ActivatedJob job, @Variable String userId) {
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        try {
            return patientService.findUser(userId);
        } catch (Exception e) {
            log.error("findUser failed for userId={}", userId, e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }

    @JobWorker(type = "com.capbpm.c8.JobWorker.filterPatients:v.1.1", fetchVariables = {"index"})
    public Map<String, Object> filterPatients(final ActivatedJob job, @Variable Integer index) {
        Map<String, Object> inputVarMap = job.getVariablesAsMap();
        try {
            long duration = patientService.filterPatients(index);
            return Map.of("duration", duration);
        } catch (Exception e) {
            log.error("filterPatients failed for index={}", index, e);
            throw new ZeebeBpmnError("ERR_CODE", e.getMessage(), inputVarMap);
        }
    }
}
