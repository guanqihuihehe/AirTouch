package com.demo.mymusic;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class Constents {
    public static MediaPlayer mplayer;
    public static int song_index;
    public static ArrayList<Song> list;
    public static int playmode = 1;
    public static int listsize;
    public static Context maincontext;
    public static int jump_index;
    public static int pageindex;
    public static int maxpagenum;
    //    public static int jump_index;
    public static SongSheetActivity.MyHandler ConstentsHandler = null;
    public static String cut_song_name(String name) {

        if (name.length() >= 11
                && ((name.substring(name.length() - 11, name.length()).equals(
                "[mqms2].mp3"))|| name.substring(name.length()-11,name.length()).equals("[mqms2].m4a") )){
            return name.substring(0, name.length() - 11);
        }
        else if (name.length() >= 11
                && ((name.substring(name.length() - 10, name.length()).equals(
                "[mqms].mp3"))|| name.substring(name.length()-10,name.length()).equals("[mqms].m4a") )){
            return name.substring(0, name.length() - 10);
        }
        else if (name.length() >= 5
                && (name.substring(name.length() - 4, name.length()).equals(
                ".mp3"))|| name.substring(name.length()-4,name.length()).equals(".m4a") ){
            return name.substring(0, name.length() - 4);
        }
        return name;
    }
}
