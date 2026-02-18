package com.acme.c8.features.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;


@Service
public class UserService {

    public Map<String, Object> findUserImpl(  String userId) {
        Map<String, Object> outputs = new HashMap<>();
        Boolean tmp = UserDmnEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", tmp);
        return outputs;
    }

}
