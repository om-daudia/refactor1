package com.acme.c8.user;

import com.acme.c8.evaluator.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class UserService {

    public Map<String, Object> findUserImpl(  String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

    public static void main(String[] args) {
        System.out.println("DMN evaluator starting...");
    }
}
