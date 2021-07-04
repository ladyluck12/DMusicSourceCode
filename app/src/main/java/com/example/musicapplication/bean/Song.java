package com.example.musicapplication.bean;

import java.io.Serializable;
import java.util.Objects;

//javabean数据库
public class Song implements Serializable {
    public String id;      //歌曲id
    public String song;    //歌曲名称
    public String singer;  //歌手名称
    public String album;   //专辑名称
    public long duration;  //歌曲时长
    public String path;    //歌曲路径
    public long size;//大小
    public long num;  //听歌次数
    public Song(String id, String song, String singer, String album, long duration, String path,long size) {
        this.id = id;
        this.song = song;
        this.singer = singer;
        this.album = album;
        this.duration = duration;
        this.path = path;
    }
    public Song() {
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public String getId() {
            return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song music = (Song) o;
        return  duration == music.duration &&
                Objects.equals(id, music.id) &&
                Objects.equals(song, music.song) &&
                Objects.equals(singer,music.singer) &&
                Objects.equals(album, music.album) ;
    }
}