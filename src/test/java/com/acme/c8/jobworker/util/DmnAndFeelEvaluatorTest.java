package com.acme.c8.jobworker.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DmnAndFeelEvaluatorTest {

    @Test
    void evaluateUserIsFound() {
        boolean result = DmnAndFeelEvaluator.evaluateUserIsFound("007");
        assertTrue(result);
    }

    @Test
    void evaluateFeelEquality() {
        Object result = DmnAndFeelEvaluator.evaluateFeel(
                "userId = \"007\"",
                Map.of("userId", "007")
        );
        assertEquals(true, result);
    }
}
