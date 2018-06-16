package com.loposition.express.trash.util;

import com.loposition.express.trash.annotation.TrashField;
import com.loposition.express.trash.exception.VariableNotFoundException;
import com.loposition.express.trash.token.BaseTrashToken;
import com.loposition.express.trash.token.LongTrashToken;
import com.loposition.express.trash.token.StringTrashToken;
import com.loposition.express.trash.token.VariableTrashToken;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author huahua
 * @date create at 下午4:46 2018/4/5
 */
public class VariableUtil {

  public static BaseTrashToken getVariable(Map<String, Object> env,
      VariableTrashToken variableTrashToken)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    String content = variableTrashToken.getContent();
    String[] varNames = content.split(":");
    Object variable = env.get(varNames[0]);
    if (variable == null) {
      throw new VariableNotFoundException(varNames[0]);
    }
    variable = getVariable(varNames,variable);
    if (variable instanceof Long ||
        variable instanceof Integer) {
      return new LongTrashToken(Long.parseLong(variable.toString()));
    }
    return new StringTrashToken((String) variable);
  }


  private static Object getVariable(String[] names, Object object)
      throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
    Object ret = object;
    for (int i = 1; i < names.length; i++) {
      Field[] declaredFields = ret.getClass().getDeclaredFields();
      Object tempRet;
      //先从注解获取名字
      tempRet = getValueByAnnotation(declaredFields, names[i], ret);
      //再从方法获取名字
      if (tempRet == null) {
        try {
          tempRet = getValueByMethod(ret, names[i]);
        }catch (NoSuchMethodException e){
          throw new VariableNotFoundException(names[i]);
        }
      }
      if (tempRet == null) {
        throw new VariableNotFoundException(names[i]);
      } else {
        ret = tempRet;
      }
    }
    return ret;
  }

  private static Object getValueByAnnotation(Field[] fields, String name, Object object)
      throws IllegalAccessException {
    Integer fieldNum = 0;
    Object ret = null;
    for (Field field : fields) {
      TrashField annotation = field.getAnnotation(TrashField.class);
      if (annotation != null) {
        if (annotation.name().equals(name)) {
          fieldNum++;
          field.setAccessible(true);
          ret = field.get(object);
        }
      }
    }
    if (fieldNum > 1) {
      throw new VariableNotFoundException("duplicate TrashField " + name);
    }
    return ret;
  }


  private static Object getValueByMethod(Object object, String name)
      throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    Class<?> clazz = object.getClass();
    Method method = clazz.getMethod(getVarMethodName(name));
    if (method == null) {
      return null;
    }
    return method.invoke(object);

  }

  public static String getVarMethodName(String varName) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("get");
    String camelName = underline2Camel(varName, Boolean.FALSE);
    String firstLetter = camelName.charAt(0) + "";
    stringBuilder.append(
        camelName.replaceFirst(
            firstLetter,
            firstLetter.toUpperCase()
        )
    );
    return stringBuilder.toString();
  }


  public static String underline2Camel(String line, boolean smallCamel) {
    if (line == null || "".equals(line)) {
      return "";
    }
    StringBuffer sb = new StringBuffer();
    Pattern pattern = Pattern.compile("([A-Za-z\\d]+)(_)?");
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      String word = matcher.group();
      sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
          : Character.toUpperCase(word.charAt(0)));
      int index = word.lastIndexOf('_');
      if (index > 0) {
        sb.append(word.substring(1, index).toLowerCase());
      } else {
        sb.append(word.substring(1).toLowerCase());
      }
    }
    return sb.toString();
  }


  /**
   * 驼峰法转下划线
   *
   * @param line 源字符串
   * @return 转换后的字符串
   */
  public static String camel2Underline(String line) {
    if (line == null || "".equals(line)) {
      return "";
    }
    line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
    StringBuffer sb = new StringBuffer();
    Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
    Matcher matcher = pattern.matcher(line);
    while (matcher.find()) {
      String word = matcher.group();
      sb.append(word.toUpperCase());
      sb.append(matcher.end() == line.length() ? "" : "_");
    }
    return sb.toString();
  }


}
