package com.loposition.express.trash.item;

/**
 * @author huahua
 * @date create at 下午7:20 2018/4/5
 */
public class TestItem {
  private String content;
  private Long id;


  public TestItem(String content ,Long id) {
    this.content = content;
    this.id = id;
  }

  public String getContent() {
    return content;
  }

  public Long getId(){return id;}
}
