package com.acme.c8.jobworker.util;



import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.impl.DefaultDmnEngineConfiguration;
import org.camunda.bpm.dmn.feel.impl.FeelEngine;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.context.VariableContext;

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

 



    /* -------------------------
       DEMO
       ------------------------- */

    public static void main(String[] args) {

        System.out.println("DMN: " + evaluateUserIsFound("007"));

        System.out.println("FEEL 1: " +
                evaluateFeel(
                        "userId = \"007\"",
                        Map.of("userId", "007")
                )
        );

//        System.out.println("FEEL 2: " +
//                evaluateFeel(
//                        "if score >= 90 then \"A\" else \"B\"",
//                        Map.of("score", 95)
//                )
//        );
//
//        System.out.println("FEEL 3: " +
//                evaluateFeel(
//                        "sum(items)",
//                        Map.of("items", java.util.List.of(10, 20, 30))
//                )
//        );
    }
}
