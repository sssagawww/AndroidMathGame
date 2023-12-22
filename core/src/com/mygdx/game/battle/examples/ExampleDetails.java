package com.mygdx.game.battle.examples;

public class ExampleDetails {
   private EXAMPLE_LIST exampleList;
   private String name;
   private int num;

   public ExampleDetails(EXAMPLE_LIST list, String name, int num){
      this.exampleList = list;
      this.name = name;
      this.num = num;
   }

   public EXAMPLE_LIST getExampleList() {
      return exampleList;
   }

   public void setExampleList(EXAMPLE_LIST exampleList) {
      this.exampleList = exampleList;
   }

   public String getName() {
      return name;
   }

   public int getNum() {
      return num;
   }
}
