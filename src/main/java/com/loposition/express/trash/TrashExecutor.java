package com.loposition.express.trash;

import com.loposition.express.trash.expression.BaseTrashExpression;
import com.loposition.express.trash.lexer.ExpressionLexer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author huahua
 * @date create at 下午5:52 2018/4/4
 * description: 总执行人
 */
public class TrashExecutor {

  public static Boolean pass(String express, Map<String,Object> env){

    ExpressionLexer expressionLexer = new ExpressionLexer(express);

    BaseTrashExpression baseTrashExpression = new BaseTrashExpression(expressionLexer, env);

    try {
      baseTrashExpression.prase();
      TrashCalculator trashCalculator = new TrashCalculator(baseTrashExpression.getTokens());
      return trashCalculator.calculator();
    }catch (Exception e){
      e.printStackTrace();
    }
    return Boolean.FALSE;

  }


  public static Boolean pass(String expression){
    HashMap<String, Object> stringObjectHashMap = new HashMap<String, Object>();
    return pass(expression,stringObjectHashMap);
  }

}
