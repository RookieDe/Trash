package com.loposition.express.trash.object;

/**
 * @author huahua
 * @date create at 下午3:36 2018/4/6
 */
public class TrashBoolean implements TrashObject {


  public boolean content;


  public boolean isContent() {
    return content;
  }


  public TrashBoolean(boolean content) {
    this.content = content;
  }

  public boolean greaterThan(TrashObject trashObject) {
    return false;
  }

  public boolean lessThan(TrashObject trashObject) {
    return false;
  }

  public boolean equal(TrashObject trashObject) {
    return false;
  }

  public Object getContent() {
    return isContent();
  }
}
