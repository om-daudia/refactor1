package com.acme.c8.jobworker.util;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;

import java.io.InputStream;
import java.util.Objects;

/**
 * Utility class for evaluating DMN decisions and FEEL expressions using Camunda engine.
 * This class provides static methods for executing DMN rules and arbitrary FEEL expressions.
 *
 * @since 1.0.0
 */
public final class DmnAndFeelEvaluator {

    private static final String USER_IS_FOUND_DMN_FILE = "UserIsFound.dmn";
    private static final String USER_IS_FOUND_RULE = "UserIsFoundRule";
    private static final String IS_FOUND_RESULT_KEY = "isFound";

    private static final DefaultDmnEngineConfiguration DMN_CONFIG =
            (DefaultDmnEngineConfiguration) DefaultDmnEngineConfiguration.createDefaultDmnEngineConfiguration();

    private static final DmnEngine DMN_ENGINE = DMN_CONFIG.buildEngine();


    /**
     * Private constructor to prevent instantiation of utility class.
     */
    private DmnAndFeelEvaluator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Evaluates the UserIsFound DMN decision rule.
     *
     * @param userId the user ID to validate
     * @return true if the user is found according to the DMN rule, false otherwise
     * @throws IllegalStateException if the DMN file is not found on the classpath
     * @throws NullPointerException if userId is null
     */
    public static boolean evaluateUserIsFound(String userId) {
        Objects.requireNonNull(userId, "userId must not be null");

        InputStream dmnStream = loadUserIsFoundDmn();
        DmnDecision decision = DMN_ENGINE.parseDecision(USER_IS_FOUND_RULE, dmnStream);
        VariableMap variables = Variables.createVariables().putValue("userId", userId);
        DmnDecisionResult result = DMN_ENGINE.evaluateDecision(decision, variables);

        return result.getSingleResult().getEntry(IS_FOUND_RESULT_KEY);
    }


    /**
     * Loads the UserIsFound DMN resource from the classpath.
     *
     * @return the input stream for the UserIsFound.dmn resource
     * @throws IllegalStateException if the resource is not found
     */
    private static InputStream loadUserIsFoundDmn() {
        InputStream dmnStream = DmnAndFeelEvaluator.class
                .getClassLoader()
                .getResourceAsStream(USER_IS_FOUND_DMN_FILE);

        if (dmnStream == null) {
            throw new IllegalStateException(
                    String.format("DMN file '%s' not found on classpath", USER_IS_FOUND_DMN_FILE)
            );
        }

        return dmnStream;
    }
}
