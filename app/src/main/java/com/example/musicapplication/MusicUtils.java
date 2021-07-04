package com.example.musicapplication;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;


import com.example.musicapplication.bean.Song;

import java.util.ArrayList;
import java.util.List;
//音乐工具类
public class MusicUtils {
    // 扫描系统里面的音频文件，返回一个list集合
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<Song>();
        //加载本地存储当中的音乐mp3文件到集合当中
        // 获取ContentResolver对象
       // ContentResolver resolver = getContentResolver();
        // 获取本地音乐存储的Uri地址
       // Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // 开始查询地址
        //Cursor cursor = resolver.query(uri, null, null, null, null);
        // 遍历Cursor
        // 查询曲库
        //Cursor 提供遍历查询结果的方法//getcontentresolver访问数据//
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
        //Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, null,
                null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        /*query参数：
        uri	要检索的内容
        projection	要返回的列的列表  传递null将返回所有列
        selection	声明要返回的行 格式为SQL WHERE子句（不包括WHERE本身） ，传递null将返回给定URI的所有行
        selectionArgs	可能包含在selection中，将被selectionArgs的值按其在所选内容中出现的顺序替换，这些值将被绑定为字符串。
        sortOrder	如何对行排序，格式为SQL order BY子句（不包括order BY本身） ，传递null将使用默认顺序，可能无序
        */
        int id=-1;
            if (cursor != null) {//查询结果非空 {
                while (cursor.moveToNext()) {//循环查询数据库
                    Song song = new Song();
                    song.duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                    song.size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                    if (song.duration>30*1000&&song.size > 1000 * 800) {
                    song.song = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                    //获取列，getColumnIndexOrThrow如果没有找到该列名,会抛出IllegalArgumentException异常
                    song.singer = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                    song.path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                     song.album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));//获取相应的的属性值
                        if (song.song.contains(".mp3")) {  //分离后缀
                            String[] str = song.song.split(".mp3");
                            song.song = str[0];
                        }
                        if (song.song.contains(".ogg")) {
                            String[] str = song.song.split(".ogg");
                            song.song = str[0];
                        }
                        if (song.song.contains(".flav")) {
                            String[] str = song.song.split(".flav");
                            song.song = str[0];
                        }  //分离后缀
                        if (song.song.contains("-")) {//用-分离歌名和歌手
                            String[] str = song.song.split("-");
                            song.singer = str[0];
                            song.song = str[1];
                        } // if(album.equals("Sounds")) continue;
                        ++id;
                        String sid = String.valueOf(id+1);
                        song.id=sid;
                        list.add(song);//添加到表中
                    }
                }
                cursor.close();// 释放资源
            }
            return list;
        }

        // 定义一个方法用来格式化获取到的时间
         public static String formatTime ( long time) {//ms
             if (time / 1000 % 60 < 10) {
                 return time / 1000 / 60 + ":0" + time / 1000 % 60;
             } else {
                 return time / 1000 / 60 + ":" + time / 1000 % 60;
             }
         }

    }
