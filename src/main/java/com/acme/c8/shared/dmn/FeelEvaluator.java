package com.acme.c8.shared.dmn;

import org.camunda.bpm.engine.variable.context.VariableContext;

public class FeelEvaluator {


    /* -------------------------
       FEEL (ARBITRARY EXPRESSIONS)
       ------------------------- */

    public static Object evaluateFeel(
            String expression,
            Map<String, Object> variables) {

      //  org.camunda.feel.context.Context context = org.camunda.feel.context.Contex of(variables);
      //  VariableContext context = VariableContext.fromMap(variables);


        VariableContext context=null;
        return FEEL_ENGINE.evaluateSimpleExpression(expression, context);
     //   return FEEL_ENGINE.evaluateSimpleExpression(expression, variables);

    }

}
