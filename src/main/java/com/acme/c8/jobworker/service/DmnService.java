package com.acme.c8.jobworker.service;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DmnService {

    private static final String PATIENT_RULE_FILE = "PatientRule.dmn";
    private static final String PATIENT_RULE_DECISION_ID = "DeterminePatientRiskLevel";
    private static final String USER_FOUND_FILE = "UserIsFound.dmn";
    private static final String USER_FOUND_DECISION_ID = "UserIsFoundRule";

    private final DmnEngine dmnEngine;
    private final Map<String, DmnDecision> decisionCache = new ConcurrentHashMap<>();

    public DmnService() {
        DefaultDmnEngineConfiguration config =
                (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();
        this.dmnEngine = config.buildEngine();
    }

    public boolean evaluateUserIsFound(String userId) {
        DmnDecision decision = getOrLoadDecision(USER_FOUND_FILE, USER_FOUND_DECISION_ID);

        VariableMap variables = Variables.createVariables()
                .putValue("userId", userId);

        DmnDecisionResult result = dmnEngine.evaluateDecision(decision, variables);
        return result.getSingleResult().getEntry("isFound");
    }

    public List<Map<String, Object>> evaluatePatientRules(List<Map<String, Object>> patients) {
        DmnDecision decision = getOrLoadDecision(PATIENT_RULE_FILE, PATIENT_RULE_DECISION_ID);

        return patients.stream()
                .map(patient -> {
                    VariableMap variables = Variables.createVariables();
                    patient.forEach(variables::putValue);
                    DmnDecisionResult result = dmnEngine.evaluateDecision(decision, variables);
                    return result.getResultList();
                })
                .flatMap(List::stream)
                .toList();
    }

    public DmnDecisionResult evaluate(String dmnFile, String decisionId, Map<String, Object> inputVariables) {
        DmnDecision decision = getOrLoadDecision(dmnFile, decisionId);

        VariableMap variables = Variables.createVariables();
        inputVariables.forEach(variables::putValue);

        return dmnEngine.evaluateDecision(decision, variables);
    }

    private DmnDecision getOrLoadDecision(String dmnFile, String decisionId) {
        String cacheKey = dmnFile + "::" + decisionId;

        return decisionCache.computeIfAbsent(cacheKey, key -> {
            InputStream dmnStream = getClass().getClassLoader().getResourceAsStream(dmnFile);
            if (dmnStream == null) {
                throw new IllegalArgumentException("DMN file not found on classpath: " + dmnFile);
            }
            log.debug("Loading DMN decision: {} from {}", decisionId, dmnFile);
            return dmnEngine.parseDecision(decisionId, dmnStream);
        });
    }
}
