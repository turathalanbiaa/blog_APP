package com.example.blog.model;

public class Categories {
    private int id;
   private String name;

   public Categories(int id, String name){
       this.id=id;
       this.name=name;
   }

   public Categories(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
