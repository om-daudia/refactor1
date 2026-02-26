package com.acme.c8.jobworker;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;
import com.acme.c8.jobworker.util.DmnEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service component for job worker operations.
 * Provides business logic for user and patient operations.
 *
 * @since 1.0.0
 */
@Component
@Slf4j
public class JobWorkerJobWorkerService {

    private static final String IS_FOUND_KEY = "isFound";
    private static final String PATIENT_RULE_FILE = "PatientRule.dmn";
    private static final String PATIENT_RISK_DECISION_ID = "DeterminePatientRiskLevel";

    /**
     * Finds a user and returns the result as a map.
     *
     * @param userId the ID of the user to find
     * @return a map containing the isFound result
     * @throws NullPointerException if userId is null
     */
    public Map<String, Object> findUserImpl(String userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        log.debug("Executing user lookup for ID: {}", userId);

        Map<String, Object> outputs = new HashMap<>();
        Boolean isFound = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put(IS_FOUND_KEY, isFound);

        log.debug("User lookup result - isFound: {} for ID: {}", isFound, userId);
        return outputs;
    }

    /**
     * Filters patients based on the specified index.
     * Evaluates patient data through the patient risk level DMN decision.
     *
     * @param index the page index for filtering patients
     * @return the duration of the filtering operation in milliseconds
     * @throws NullPointerException if index is null
     */
    public long filterPatientsImpl(Integer index) {
        Objects.requireNonNull(index, "index must not be null");

        log.debug("Starting patient filtering with index: {}", index);
        long startTime = System.currentTimeMillis();

        try {
            List<Map<String, Object>> patientList = PatientClient.loadPatients(index, 1000);
            log.debug("Loaded {} patients for evaluation", patientList.size());

            DmnEvaluator.evaluateToJsonForList(PATIENT_RULE_FILE, PATIENT_RISK_DECISION_ID, patientList);
            log.debug("Patient risk evaluation completed");

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Patient filtering completed in {} milliseconds", duration);
            return duration;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("Error during patient filtering after {} milliseconds", duration, e);
            throw new RuntimeException("Failed to filter patients", e);
        }
    }
}
