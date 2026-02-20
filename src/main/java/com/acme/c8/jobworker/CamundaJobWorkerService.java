package com.acme.c8.jobworker;
import java.util.*;

import com.acme.c8.jobworker.util.DmnAndFeelEvaluator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CamundaJobWorkerService {

    public Map<String, Object> findUserImpl(String userId) {
        log.debug("Evaluating DMN for userId={}", userId);
        Map<String, Object> outputs = new HashMap<>();
        Boolean isFound = DmnAndFeelEvaluator.evaluateUserIsFound(userId);
        outputs.put("isFound", isFound);
        log.debug("DMN result for userId={}: isFound={}", userId, isFound);
        return outputs;
    }
}
