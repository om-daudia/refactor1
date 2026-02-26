package com.acme.c8.jobworker.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for evaluating DMN decisions using Camunda engine.
 * Provides methods for executing DMN rules and converting results to JSON format.
 * Implements caching for parsed DMN decisions to improve performance.
 *
 * @since 1.0.0
 */
public final class DmnEvaluator {

    private static final String CACHE_KEY_SEPARATOR = "::";
    private static final String DMN_FILE_NOT_FOUND_ERROR = "DMN file not found on classpath: ";

    private static final DefaultDmnEngineConfiguration DMN_CONFIG =
            (DefaultDmnEngineConfiguration)
                    DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = DMN_CONFIG.buildEngine();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * Cache of parsed DMN decisions.
     * Key format: dmnFile + "::" + decisionId
     * Uses ConcurrentHashMap for thread-safe access.
     */
    private static final Map<String, DmnDecision> DECISION_CACHE = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DmnEvaluator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Evaluates a single DMN decision and returns the result as JSON.
     *
     * @param dmnFile the DMN file name on the classpath
     * @param decisionId the ID of the decision to evaluate
     * @param inputVariables a map of input variables for the decision
     * @return the DMN result as a JSON string
     * @throws IllegalArgumentException if the DMN file is not found
     * @throws RuntimeException if JSON serialization fails
     * @throws NullPointerException if any parameter is null
     */
    public static String evaluateToJson(
            String dmnFile,
            String decisionId,
            Map<String, Object> inputVariables) {
        Objects.requireNonNull(dmnFile, "dmnFile must not be null");
        Objects.requireNonNull(decisionId, "decisionId must not be null");
        Objects.requireNonNull(inputVariables, "inputVariables must not be null");

        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);
        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);
        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);

        return toJson(result);
    }

    /**
     * Evaluates a DMN decision for a list of input variable maps and returns results as JSON.
     * Each item in the list is evaluated against the same decision.
     *
     * @param dmnFile the DMN file name on the classpath
     * @param decisionId the ID of the decision to evaluate
     * @param patientList a list of variable maps to evaluate
     * @return the DMN results as a JSON string
     * @throws IllegalArgumentException if the DMN file is not found
     * @throws RuntimeException if JSON serialization fails
     * @throws NullPointerException if any parameter is null
     */
    public static String evaluateToJsonForList(
            String dmnFile,
            String decisionId,
            List<Map<String, Object>> patientList) {
        Objects.requireNonNull(dmnFile, "dmnFile must not be null");
        Objects.requireNonNull(decisionId, "decisionId must not be null");
        Objects.requireNonNull(patientList, "patientList must not be null");

        List<DmnDecisionResult> results = new ArrayList<>();
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        for (Map<String, Object> variables : patientList) {
            VariableMap variableMap = Variables.createVariables();
            variableMap.putAll(variables);
            DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variableMap);
            results.add(result);
        }

        return toJsonFromList(results);
    }


    /**
     * Gets a DMN decision from cache or loads and caches it.
     * Uses a computed cache key combining dmnFile and decisionId.
     *
     * @param dmnFile the DMN file name on the classpath
     * @param decisionId the ID of the decision to load
     * @return the parsed DMN decision
     * @throws IllegalArgumentException if the DMN file is not found on the classpath
     */
    private static DmnDecision getOrLoadDecision(String dmnFile, String decisionId) {
        String cacheKey = dmnFile + CACHE_KEY_SEPARATOR + decisionId;

        return DECISION_CACHE.computeIfAbsent(cacheKey, key -> {
            InputStream dmnStream = DmnEvaluator.class
                    .getClassLoader()
                    .getResourceAsStream(dmnFile);

            if (dmnStream == null) {
                throw new IllegalArgumentException(DMN_FILE_NOT_FOUND_ERROR + dmnFile);
            }

            return DMN_ENGINE.parseDecision(decisionId, dmnStream);
        });
    }

    /**
     * Converts a DMN decision result to JSON format.
     *
     * @param result the DMN decision result to convert
     * @return the result as a JSON string
     * @throws RuntimeException if JSON serialization fails
     */
    private static String toJson(DmnDecisionResult result) {
        try {
            return OBJECT_MAPPER.writeValueAsString(result.getResultList());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN result to JSON", e);
        }
    }

    /**
     * Converts a list of DMN decision results to JSON format.
     *
     * @param results a list of DMN decision results to convert
     * @return the results as a JSON string
     * @throws RuntimeException if JSON serialization fails
     */
    private static String toJsonFromList(List<DmnDecisionResult> results) {
        try {
            return OBJECT_MAPPER.writeValueAsString(
                    results.stream()
                            .flatMap(r -> r.getResultList().stream())
                            .toList()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize DMN results to JSON", e);
        }
    }
}

