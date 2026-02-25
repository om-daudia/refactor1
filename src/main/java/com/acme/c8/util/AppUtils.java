package com.acme.c8.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.camunda.feel.api.FeelEngineApi;
import org.camunda.feel.api.FeelEngineBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class AppUtils {

    private static final ObjectMapper PRETTY_MAPPER =
            new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final FeelEngineApi FEEL_ENGINE = FeelEngineBuilder.forJava().build();


    public static JsonNode evaluateFeel(String feel, Map<String, Object> variables) throws JsonProcessingException {
        var result = FEEL_ENGINE.evaluateExpression(feel, variables);
        if (result == null || result.isFailure()) return null;
        return MAPPER.readTree(MAPPER.writeValueAsString(result.result()));
    }
}
