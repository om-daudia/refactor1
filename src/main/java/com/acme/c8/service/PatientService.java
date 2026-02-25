package com.acme.c8.service;

import com.acme.c8.client.PatientClient;
import com.acme.c8.dmn.DmnEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final DmnEvaluator dmnEvaluator;
    private final PatientClient patientClient;

    public Map<String, Object> findUser(String userId) {
        boolean isFound = dmnEvaluator.evaluateUserIsFound(userId);
        return Map.of("isFound", isFound);
    }

    public long filterPatients(int pageIndex) throws Exception {
        List<Map<String, Object>> patients = patientClient.loadPatients(pageIndex, 1000);

        long start = System.currentTimeMillis();
        dmnEvaluator.evaluateToJsonForList("PatientRule.dmn", "DeterminePatientRiskLevel", patients);
        long end = System.currentTimeMillis();

        return (end - start) / 1000;
    }
}
