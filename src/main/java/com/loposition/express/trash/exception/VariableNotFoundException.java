package com.loposition.express.trash.exception;

/**
 * @author huahua
 * @date create at 下午4:56 2018/4/5
 */
public class VariableNotFoundException extends RuntimeException{

  private String variableName;

  public VariableNotFoundException(String variableName){
    this.variableName = variableName;
  }
}
