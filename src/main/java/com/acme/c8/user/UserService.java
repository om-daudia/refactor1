package com.acme.c8.user;

import com.acme.c8.evaluator.DmnAndFeelEvaluator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final DmnAndFeelEvaluator dmnAndFeelEvaluator;

    public Map<String, Object> findUserImpl(  String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = dmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

    public static void main(String[] args) {
        System.out.println("DMN evaluator starting...");
    }
}
