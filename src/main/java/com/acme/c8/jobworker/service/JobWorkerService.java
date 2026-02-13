package com.acme.c8.jobworker.service;

import com.acme.c8.jobworker.client.PatientClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobWorkerService {

    private static final int PATIENT_PAGE_SIZE = 1000;

    private final DmnService dmnService;
    private final PatientClient patientClient;

    public Map<String, Object> findUser(String userId) {
        boolean isFound = dmnService.evaluateUserIsFound(userId);
        return Map.of("isFound", isFound);
    }

    public Map<String, Object> filterPatients(int pageIndex) throws Exception {
        List<Map<String, Object>> patients = patientClient.loadPatients(pageIndex, PATIENT_PAGE_SIZE);
        log.info("Loaded {} patients for page {}", patients.size(), pageIndex);

        long start = System.currentTimeMillis();
        dmnService.evaluatePatientRules(patients);
        long durationSeconds = (System.currentTimeMillis() - start) / 1000;

        Map<String, Object> outputs = new HashMap<>();
        outputs.put("duration", durationSeconds);
        return outputs;
    }
}
