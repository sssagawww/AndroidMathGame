package com.mygdx.game.battle.examples;

public class ExampleDetails {
   private EXAMPLE_NUM exampleList;
   private String name;

   public ExampleDetails(EXAMPLE_NUM list, String name){
      this.exampleList = list;
      this.name = name;
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
}
