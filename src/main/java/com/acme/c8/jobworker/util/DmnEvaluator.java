package com.acme.c8.jobworker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import com.acme.c8.jobworker.PatientClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class DmnEvaluator {

    private final PatientClient patientClient;

    private static final DefaultDmnEngineConfiguration CONFIG =
            (DefaultDmnEngineConfiguration)
                    DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE =
            CONFIG.buildEngine();

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    /**
     * Cache of parsed DMN decisions.
     * Key = dmnFile + "::" + decisionId
     */
    private static final Map<String, DmnDecision> DECISION_CACHE =
            new ConcurrentHashMap<>();

    /**
     * Evaluate a DMN decision and return the result as JSON.
     */
    public static String evaluateToJson(
            String dmnFile,
            String decisionId,
            Map<String, Object> inputVariables) {

        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);

        DmnDecisionResult result =
                DMN_ENGINE.evaluateDecision(decision, variables);

        return toJson(result);
    }

    public static String evaluateToJsonForList(
            String dmnFile,
            String decisionId,
            List<Map<String, Object>> pList) {

        List<DmnDecisionResult> resList = new ArrayList<DmnDecisionResult>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        for (Map<String, Object> inputVariables : pList) {
            inputVariables.forEach(variables::putValue);

            DmnDecisionResult result =
                    DMN_ENGINE.evaluateDecision(decision, variables);

            resList.add(result);
        }
        return toJsonFromList(resList);
    }

    /**
     * Load and cache a DMN decision if not already cached.
     */
    private static DmnDecision getOrLoadDecision(
            String dmnFile,
            String decisionId) {

        String cacheKey = dmnFile + "::" + decisionId;

        return DECISION_CACHE.computeIfAbsent(cacheKey, key -> {

            InputStream dmnStream = DmnEvaluator.class
                    .getClassLoader()
                    .getResourceAsStream(dmnFile);

            if (dmnStream == null) {
                throw new IllegalArgumentException(
                        "DMN file not found on classpath: " + dmnFile
                );
            }

            return DMN_ENGINE.parseDecision(decisionId, dmnStream);
        });
    }

    /**
     * Convert a DMN decision result into JSON.
     */
    private static String toJson(DmnDecisionResult result) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    result.getResultList()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to serialize DMN result to JSON", e
            );
        }
    }

    private static String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    results.stream()
                            .flatMap(r -> r.getResultList().stream())
                            .toList()
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to serialize DMN results to JSON", e
            );
        }
    }

    public long go(int pageIndex) throws Exception {
        List<Map<String, Object>> patientList = patientClient.loadPatients(pageIndex, 1000);
        long start = System.currentTimeMillis();
        evaluateToJsonForList("PatientRule.dmn", "DeterminePatientRiskLevel", patientList);
        long end = System.currentTimeMillis();
        return (end - start) / 1000;
    }
}
