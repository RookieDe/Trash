package com.loposition.express.trash.expression;

import com.loposition.express.trash.lexer.ExpressionLexer;
import com.loposition.express.trash.token.BaseTrashToken;
import com.loposition.express.trash.token.VariableTrashToken;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author huahua
 * @date create at 下午8:33 2018/4/4
 * description: 表达式
 */
public class BaseTrashExpression {

  private ExpressionLexer expressionLexer;

  private List<BaseTrashToken> tokens;

  private Map<String,Object> env;



  public BaseTrashExpression(ExpressionLexer expressionLexer ,Map<String,Object>  env){
    this.expressionLexer = expressionLexer;
    this.env = env;
  }


  public void prase()
      throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    ArrayList<BaseTrashToken> baseTrashTokens = new ArrayList<BaseTrashToken>();
    BaseTrashToken baseTrashToken = null;
    do{
      baseTrashToken = expressionLexer.scan();
      if (baseTrashToken instanceof VariableTrashToken){
        baseTrashToken = VariableUtil.getVirable(env,(VariableTrashToken)baseTrashToken);
      }
      baseTrashTokens.add(baseTrashToken);
    }while (baseTrashToken != null);
    tokens = baseTrashTokens;
  }

  public List<BaseTrashToken> getTokens() {
    return tokens;
  }
}
