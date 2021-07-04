package com.example.musicapplication.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Sheet implements Serializable {
    private String name;
    private List<Song> list;

    public Sheet() {
        name="New List";
        list=new ArrayList<>();
    }
    public Sheet(String name){
        this.name=name;
        list=new ArrayList<>();
    }

    public  Sheet(String name, int type){
        this.name=name;
        switch(type){
            case 0:
                list=new ArrayList<>();
                break;
            case 1:
                list=new LinkedList<>();
                break;
            default:break;
        }
    }

    public String getName() {
        return name;
    }

    public List<Song> getList() {
        return list;
    }

    public void setList(java.util.List<Song> musicList){
        list=musicList;
    }

    public void add(Song music){
        list.add(music);
    }

    public void remove(Song music){
        list.remove(music);
    }

    public String getNumString(){
        if(list!=null) {
            return " " + list.size() + " 首音乐";
        }
        return " " + 0 + " 首音乐";
    }


    public boolean contains(Song song){

        return list.contains(song);
    }

    public void addFirst(Song music){
        if(list instanceof LinkedList) {
            ((LinkedList<Song>) list).addFirst(music);
        }
    }
}
