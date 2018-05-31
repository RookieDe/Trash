package com.loposition.express.trash.token;

/**
 * @author huahua
 * @date create at 上午8:01 2018/4/5
 * description: //TODO
 */
public enum  KeywordTrashType {
  IN("in"),
  INCLUDE("include"),
  NOT("not"),
  OR("or"),
  AND("and");


  private final String token;

  public String getToken() {
    return token;
  }

  private KeywordTrashType(String token){
    this.token = token;
  }


  public static KeywordTrashType of(String token){
    for (KeywordTrashType keywordTrashType : KeywordTrashType.values()){
      if (keywordTrashType.getToken().equals(token)){
        return keywordTrashType;
      }
    }
    return null;
  }
}
