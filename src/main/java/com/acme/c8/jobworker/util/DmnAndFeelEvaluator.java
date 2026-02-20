package com.acme.c8.jobworker.util;



import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.FeelEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;
import java.util.Map;

public class DmnAndFeelEvaluator {


    private static final DefaultDmnEngineConfiguration CONFIG = (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE =
            CONFIG.buildEngine();

    // ✅ FEEL is now a supported public API in 7.24
    private static final FeelEngine FEEL_ENGINE =
            CONFIG.getFeelEngine();

    /* -------------------------
       DMN
       ------------------------- */

    public static boolean evaluateUserIsFound(String userId) {

        InputStream dmnStream = DmnAndFeelEvaluator.class
                .getClassLoader()
                .getResourceAsStream("UserIsFound.dmn");

        if (dmnStream == null) {
            throw new IllegalStateException("UserIsFound.dmn not found on classpath");
        }

        DmnDecision decision =
                DMN_ENGINE.parseDecision("UserIsFoundRule", dmnStream);

        VariableMap variables = Variables.createVariables()
                .putValue("userId", userId);

        DmnDecisionResult result =
                DMN_ENGINE.evaluateDecision(decision, variables);

        return result
                .getSingleResult()
                .getEntry("isFound");
    }

    /* -------------------------
       FEEL (ARBITRARY EXPRESSIONS)
       ------------------------- */

    public static Object evaluateFeel(
            String expression,
            Map<String, Object> variables) {

        VariableMap variableMap = Variables.createVariables();
        variables.forEach(variableMap::putValue);
        return FEEL_ENGINE.evaluateSimpleExpression(expression, variableMap.asVariableContext());
    }
}
