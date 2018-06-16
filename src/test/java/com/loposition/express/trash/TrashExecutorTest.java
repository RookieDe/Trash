package com.loposition.express.trash;

import static org.junit.Assert.*;

import com.loposition.express.trash.context.VarContextHolder;
import com.loposition.express.trash.item.SubItem;
import com.loposition.express.trash.item.TestItem;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * @author huahua
 * @date create at 下午3:54 2018/4/5
 */
public class TrashExecutorTest {

  @Test
  public void testExecute(){

    HashMap<String, Object> stringObjectHashMap = new HashMap<String, Object>();
    stringObjectHashMap.put("test",3244);
    TestItem hello = new TestItem("hello",20L);
    stringObjectHashMap.put("item",hello);
    ArrayList<String> strings = new ArrayList<String>();
    strings.add("hello");
    strings.add("hello1");
    strings.add("hello2");
    VarContextHolder.add("go",strings);
    String expression = " '$go'  in item:content ";

    Boolean pass = TrashExecutor.pass(expression, stringObjectHashMap);

    System.out.println(pass);
  }

  @Test
  public void testAnnotation(){
    SubItem what = new SubItem("hell3");
    TestItem hello = new TestItem("hello",20L,what);
    ArrayList<String> strings = new ArrayList<String>();
    strings.add("hello");
    strings.add("hello1");
    strings.add("hello2");
    VarContextHolder.add("go",strings);
    String expression = " '$go' in testItem:item:name ";

    Boolean pass = TrashExecutor.pass(expression, hello);
    System.out.println(pass);
  }


}