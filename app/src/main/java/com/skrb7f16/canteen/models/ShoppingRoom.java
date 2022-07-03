package com.skrb7f16.canteen.models;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShoppingRoom {
    User creater;
    List<User> members;
    String code;
    List<Item> items;


    public ShoppingRoom(User creater) {
        this.creater = creater;
        items = new ArrayList<Item>();
        members = new ArrayList<User>();
    }

    public ShoppingRoom() {
        items = new ArrayList<Item>();
        members = new ArrayList<User>();
    }

    public ShoppingRoom(String code) {
        this.code = code;
        items = new ArrayList<Item>();
        members = new ArrayList<User>();
    }

    public User getCreater() {
        return creater;
    }

    public ShoppingRoom(User creater, String code) {
        this.creater = creater;
        this.code = code;
        items = new ArrayList<Item>();
        members = new ArrayList<User>();
    }

    public void setCreater(User creater) {
        this.creater = creater;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void addMembers(User user){
        members.add(user);
    }

    public void addItem(Item item){
        items.add(item);
    }

    public boolean memberExists(User user){
        for(User u:members){
            if(u.getUserId().equals(user.getUserId())==true){

                return true;
            }
        }
        return false;
    }
}
