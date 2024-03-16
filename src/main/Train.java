/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package main;

/**
 *
 * @author Nguyen Trong Nguyen
 */
public class Train {
    
    private int id;
    private int capacity;
    private String description;
    
    public Train(){
        
    }
    public int getID(){
        return id;
    }
    public void setID(int id){
        this.id = id;
    }
    public int getCapacity(){
        return capacity;
    }
    public void setCapacity(int capacity){
        this.capacity = capacity;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }
}
