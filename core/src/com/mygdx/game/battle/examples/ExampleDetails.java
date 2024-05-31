package com.mygdx.game.battle.examples;

public class ExampleDetails {
   private EXAMPLE_NUM exampleList;
   private String name;
   private int num;

   public ExampleDetails(EXAMPLE_NUM list, String name, int num){
      this.exampleList = list;
      this.name = name;
      this.num = num;
   }

   public EXAMPLE_NUM getExampleList() {
      return exampleList;
   }

   public void setExampleList(EXAMPLE_NUM exampleList) {
      this.exampleList = exampleList;
   }

   public String getName() {
      return name;
   }

   public int getNum() {
      return num;
   }
}
