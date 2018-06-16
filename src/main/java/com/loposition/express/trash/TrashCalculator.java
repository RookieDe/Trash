package com.loposition.express.trash;

import com.loposition.express.trash.exception.ArrayParseException;
import com.loposition.express.trash.exception.IncludeCannotUseException;
import com.loposition.express.trash.object.TrashBoolean;
import com.loposition.express.trash.object.TrashDouble;
import com.loposition.express.trash.object.TrashList;
import com.loposition.express.trash.object.TrashLong;
import com.loposition.express.trash.object.TrashObject;
import com.loposition.express.trash.object.TrashString;
import com.loposition.express.trash.token.BaseTrashToken;
import com.loposition.express.trash.token.DoubleTrashToken;
import com.loposition.express.trash.token.KeyWordTrashToken;
import com.loposition.express.trash.token.KeywordTrashType;
import com.loposition.express.trash.token.LongTrashToken;
import com.loposition.express.trash.token.OperatorTrashToken;
import com.loposition.express.trash.token.OperatorTrashType;
import com.loposition.express.trash.token.StringTrashToken;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * @author huahua
 * @date create at 上午7:58 2018/4/6
 */
public class TrashCalculator {


  private Stack<TrashObject> tokens;


  private Iterator<BaseTrashToken> tokenIterator;

  private boolean notFlag;

  private BaseTrashToken nowToken;


  public TrashCalculator(List<BaseTrashToken> tokens) {
    this.tokenIterator = tokens.iterator();
    this.tokens = new Stack<TrashObject>();
  }

  public Boolean calculator(Stack<TrashObject> tokens) {

    while (tokenIterator.hasNext()) {
      BaseTrashToken token = nextToken();
      if (token == null) {
        continue;
      }
      //push string
      if (token instanceof StringTrashToken) {
        StringTrashToken stringTrashToken = (StringTrashToken) token;
        if (stringTrashToken.getContent().startsWith("$")) {
          tokens.push(new TrashList(stringTrashToken.getContent()));
        } else {
          tokens.push(new TrashString(stringTrashToken.getContent()));
        }

      }
      //push long
      if (token instanceof LongTrashToken) {
        tokens.push(new TrashLong(
            ((LongTrashToken) token).getContent()));
      }
      if (token instanceof DoubleTrashToken) {
        tokens.push(new TrashDouble(
            ((DoubleTrashToken) token).getContent()));
      }

      if (token instanceof OperatorTrashToken) {
        //push list
        OperatorTrashToken operatorTrashToken = (OperatorTrashToken) token;
        //array
        if (operatorTrashToken.isOperate(OperatorTrashType.ARRAY_LEFT)) {
          pushList();
        }
        //()
        else if (operatorTrashToken.isOperate(OperatorTrashType.RIGHT)) {
          bracket();
        } else if (operatorTrashToken.isOperate(OperatorTrashType.LEFT)) {
          bracket();
        }
        //math operate
        mathOperate(operatorTrashToken);
      }

      //push keyword
      if (token instanceof KeyWordTrashToken) {
        KeyWordTrashToken keyWordTrashToken = (KeyWordTrashToken) token;
        //include in
        if (keyWordTrashToken.is(KeywordTrashType.INCLUDE)) {
          checkIncludeIn();
          BaseTrashToken expectToken = nextToken();
          if (expectToken instanceof StringTrashToken) {
            includeInString((StringTrashToken) expectToken);
          } else {
            throw new IncludeCannotUseException("must be String");
          }
        }
        // in
        else if (keyWordTrashToken.is(KeywordTrashType.IN)) {
          BaseTrashToken expectToken = nextToken();
          inString((StringTrashToken) expectToken);
        }
        // or
        else if (keyWordTrashToken.is(KeywordTrashType.OR)) {
          //之前有真的直接返回
          if (orOperate()) {
            return Boolean.TRUE;
          }
        }
        //and
        else if (keyWordTrashToken.is(KeywordTrashType.AND)) {
          //之前有 false 直接返回 不然就把之前的 pop
          if (andOperate()) {
            return Boolean.FALSE;
          }
        } else if (keyWordTrashToken.is(KeywordTrashType.NOT)) {
          notFlag = true;
        }
      }
    }
    return null;
  }

  private boolean andOperate() {
    TrashObject peek = tokens.peek();
    if (peek instanceof TrashBoolean) {
      TrashBoolean trashBoolean = (TrashBoolean) tokens.pop();
      return !((Boolean) trashBoolean.getContent());
    }
    return true;
  }

  /**
   * 看之前是否有为真的返回 直接出栈
   * ps or之前是 true 的话就是 true ,or之前为 false 的话可以直接忽略
   *
   * @return 是否需要执行下注
   */
  private boolean orOperate() {
    TrashObject peek = tokens.peek();
    if (peek instanceof TrashBoolean) {
      TrashBoolean trashBoolean = (TrashBoolean) tokens.pop();
      return ((Boolean) trashBoolean.getContent());
    }
    return false;
  }

  public Boolean calculator() {
    Boolean calculator = calculator(tokens);
    if (calculator != null) {
      return calculator;
    } else {
      return (Boolean) tokens.peek().getContent();
    }
  }

  /**
   * bracket
   * 括号不支持
   */
  private void bracket() {

  }


  private void mathOperate(OperatorTrashToken operatorTrashToken) {
    Boolean ret = null;
    //>
    if (operatorTrashToken.isOperate(OperatorTrashType.GT)) {
      TrashObject trashObject = buildBasicTrashObjectByToken(tokenIterator.next());
      ret = tokens.pop().greaterThan(trashObject);
    }
    //>=
    else if (operatorTrashToken.isOperate(OperatorTrashType.GTE)) {
      TrashObject trashObject = buildBasicTrashObjectByToken(tokenIterator.next());
      ret = !tokens.pop().lessThan(trashObject);
    }
    //<
    else if (operatorTrashToken.isOperate(OperatorTrashType.LT)) {
      TrashObject trashObject = buildBasicTrashObjectByToken(tokenIterator.next());
      ret = tokens.pop().lessThan(trashObject);
    }
    //<=
    else if (operatorTrashToken.isOperate(OperatorTrashType.LTE)) {
      TrashObject trashObject = buildBasicTrashObjectByToken(tokenIterator.next());
      ret = !tokens.pop().greaterThan(trashObject);
    }
    //==
    else if (operatorTrashToken.isOperate(OperatorTrashType.EQUAL)) {
      TrashObject trashObject = buildBasicTrashObjectByToken(tokenIterator.next());
      ret = tokens.pop().equal(trashObject);
    }
    //!=
    else if (operatorTrashToken.isOperate(OperatorTrashType.UNEQUAL)) {
      TrashObject trashObject = buildBasicTrashObjectByToken(tokenIterator.next());
      ret = !tokens.pop().equal(trashObject);
    }
    if (ret != null) {
      tokens.push(new TrashBoolean(ret));
    }
  }

  private void inString(StringTrashToken expectToken) {
    TrashObject peek = tokens.peek();

    if (peek instanceof TrashList) {
      includeInString(expectToken);
    } else {
      tokens.pop();
      tokens.push(new TrashBoolean(
          expectToken.getContent().contains(
              String.valueOf(
                  peek.getContent()
              )
          )));
    }
  }

  /**
   * 压栈数组
   */
  private void pushList() {
    ArrayList<TrashObject> trashObjects = new ArrayList<TrashObject>();
    while (tokenIterator.hasNext()) {
      BaseTrashToken token = tokenIterator.next();
      if (token instanceof OperatorTrashToken) {
        OperatorTrashToken operatorTrashToken = (OperatorTrashToken) token;
        operatorTrashToken.isOperate(OperatorTrashType.ARRAY_RIGHT);
        tokens.push(new TrashList<TrashObject>(trashObjects));
        return;
      } else {
        tryAdd(trashObjects, token);
      }
    }

    throw new ArrayParseException();

  }

  private boolean checkIncludeIn() {
    TrashObject peek = tokens.peek();
    if (!(peek instanceof TrashList)) {
      throw new IncludeCannotUseException(
          "the object before include must be list");
    }
    BaseTrashToken next = tokenIterator.next();
    if (!(next instanceof KeyWordTrashToken)) {
      throw new IncludeCannotUseException(
          "the token after include must be 'in'");
    }
    if (!((KeyWordTrashToken) next).is(KeywordTrashType.IN)) {
      throw new IncludeCannotUseException(
          "the token after include must be 'in'");
    }
    return true;
  }


  /**
   * include in String
   */
  private void includeInString(StringTrashToken stringTrashToken) {
    TrashList<TrashObject> pop = (TrashList<TrashObject>) tokens.pop();
    List<TrashObject> trashObjects = pop.getContent();
    boolean flag = false;
    for (TrashObject trashObject : trashObjects) {
      if (stringTrashToken.getContent().contains(
          String.valueOf(trashObject.getContent())
      )) {
        flag = true;
        break;
      }
    }
    tokens.push(new TrashBoolean(flag));

  }


  private static void tryAdd(List<TrashObject> objects, BaseTrashToken baseTrashToken) {
    if (canAdd(objects, baseTrashToken)) {
      if (baseTrashToken instanceof StringTrashToken) {
        objects.add(new TrashString(((StringTrashToken) baseTrashToken).getContent()));
      }
      if (baseTrashToken instanceof LongTrashToken) {
        objects.add(new TrashLong(
            ((LongTrashToken) baseTrashToken).getContent()
        ));
      }
    } else {
      throw new ArrayParseException();
    }
  }


  private static boolean canAdd(List<TrashObject> objects, BaseTrashToken baseTrashToken) {
    if (objects.size() == 0) {
      return Boolean.TRUE;
    }
    TrashObject trashObject = objects.get(objects.size() - 1);
    if (baseTrashToken instanceof StringTrashToken
        && trashObject instanceof TrashString) {
      return Boolean.TRUE;
    }
    if (baseTrashToken instanceof LongTrashToken
        && trashObject instanceof TrashLong) {
      return Boolean.TRUE;
    }
    return Boolean.FALSE;
  }

  private static TrashObject buildBasicTrashObjectByToken(BaseTrashToken baseTrashToken) {
    if (baseTrashToken instanceof LongTrashToken) {
      return new TrashLong(((LongTrashToken) baseTrashToken).getContent());
    } else if (baseTrashToken instanceof StringTrashToken) {
      return new TrashString(((StringTrashToken) baseTrashToken).getContent());
    } else if (baseTrashToken instanceof DoubleTrashToken) {
      return new TrashDouble(((DoubleTrashToken) baseTrashToken).getContent());
    }
    return null;
  }


  BaseTrashToken nextToken() {
    nowToken = tokenIterator.next();
    return nowToken;
  }
}
