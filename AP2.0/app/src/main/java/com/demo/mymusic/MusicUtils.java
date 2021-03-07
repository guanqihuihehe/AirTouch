package com.demo.mymusic;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

public class MusicUtils {
    /**
     * 扫描系统里面的音频文件，返回一个list集合
     */
    public static List<Song> getMusicData(Context context) {
        List<Song> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            System.out.println("find right:"+cursor.getCount());
            while (cursor.moveToNext()) {
                Song song = new Song();
                song.setSong( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                song.setSinger( cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                song.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                song.setDuration( cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                song.setSize( cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                if (song.getSize() > 1000 * 800&& song.getDuration() > 30000) {//过滤掉短音频 n
                    // 分离出歌曲名和歌手
                    if (song.getSong().contains("-")) {
                        String[] str = song.getSong().split("-");
                        song.setSinger( str[0]);
                        song.setSong( str[1]);
                    }
                    list.add(song);
                }
//                System.out.println("listsize:"+list.size());
            }
//            System.out.println("listsize:"+list.size());
            // 释放资源
            cursor.close();
        }
        else
        {
            System.out.println("find error");
        }
        //change 10.20
//        List<Song> list2 = new ArrayList<>();
//        for(int k=15;k<20;k++)
//        {
//            list2.add(list.get(k));
//        }
        return list;
    }

    //格式化时间
    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" + time / 1000 % 60;
        }
    }
}