package com.loposition.express.trash;

import com.loposition.express.trash.annotation.TrashObject;
import com.loposition.express.trash.expression.BaseTrashExpression;
import com.loposition.express.trash.lexer.ExpressionLexer;
import com.loposition.express.trash.token.StringTrashToken;
import com.loposition.express.trash.util.ObjectNameUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huahua
 * @date create at 下午5:52 2018/4/4
 * description: 总执行人
 */
public class TrashExecutor {

  public static Boolean pass(String express, Map<String, Object> env) {

    ExpressionLexer expressionLexer = new ExpressionLexer(express);

    BaseTrashExpression baseTrashExpression = new BaseTrashExpression(expressionLexer, env);

    try {
      baseTrashExpression.prase();
      TrashCalculator trashCalculator = new TrashCalculator(baseTrashExpression.getTokens());
      return trashCalculator.calculator();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Boolean.FALSE;

  }


  public static Boolean pass(String expression) {
    HashMap<String, Object> stringObjectHashMap = new HashMap<>(0);
    return pass(expression, stringObjectHashMap);
  }

  public static Boolean pass(String expression, Object object, Map<String, Object> env) {
    //决定在 map中存放的 name
    String defaultName = ObjectNameUtil.getDefaultName(object);
    env.put(defaultName, object);
    TrashObject trashAnnotation =
        object.getClass().getAnnotation(TrashObject.class);
    if (trashAnnotation != null && trashAnnotation.value().length > 0) {
      for (String aliasName : trashAnnotation.value()) {
        if (!"".equals(aliasName)) {
          env.put(aliasName, object);
        }
      }
    }

    return pass(expression, env);
  }

  public static Boolean pass(String expression, Object object) {
    HashMap<String, Object> evn = new HashMap<>(1);
    return pass(expression, object, evn);
  }

}
