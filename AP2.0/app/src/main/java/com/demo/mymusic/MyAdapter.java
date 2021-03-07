package com.demo.mymusic;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fruitbasket.audioplatform.R;

import java.util.List;

import static com.demo.mymusic.Constents.maincontext;
import static com.demo.mymusic.Constents.pageindex;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<Song> list;
    private int position_flag = 0;


    public MyAdapter(Context context, List<Song> list) {

        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            // 引入布局
            view = View.inflate(context, R.layout.list_item, null);
            // 实例化对象
            holder.song = (TextView) view.findViewById(R.id.item_mymusic_song);
            holder.singer = (TextView) view
                    .findViewById(R.id.item_mymusic_singer);
            holder.jump=view.findViewById(R.id.jump);
//            holder.duration = (TextView) view
//                    .findViewById(R.id.item_mymusic_duration);
            holder.position = (TextView) view
                    .findViewById(R.id.item_mymusic_postion);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // 给控件赋值
        String string_song = list.get(i).getSong();
        if (string_song.length() >= 11
                && (string_song.substring(string_song.length() - 11,
                string_song.length()).equals("[mqms2].mp3")||string_song.substring(string_song.length() - 11,
                string_song.length()).equals("[mqms2].m4a"))) {
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 11).trim());
        }
        else if (string_song.length() >= 11
                && (string_song.substring(string_song.length() - 10,
                string_song.length()).equals("[mqms].mp3")||string_song.substring(string_song.length() - 10,
                string_song.length()).equals("[mqms].m4a"))) {
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 10).trim());
        }
        else if (string_song.length() >= 5
                && (string_song.substring(string_song.length() - 4,
                string_song.length()).equals(".mp3")||string_song.substring(string_song.length() - 4,
                string_song.length()).equals(".m4a"))) {
            holder.song.setText(string_song.substring(0,
                    string_song.length() - 4).trim());
        }
        else {
            holder.song.setText(string_song.trim());
        }

        holder.singer.setText(list.get(i).getSinger().toString().trim());
        // 时间转换为时分秒
        int duration = list.get(i).getDuration();
        String time = MusicUtils.formatTime(duration);

        final  int index=i;
        holder.jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Message message=new Message();
//                message.what=2;
//                message.obj=index;
//                ConstentsHandler.sendMessage(message);
                int res=index+pageindex*8;
                Intent intent=new Intent(maincontext,BroadcastActivity.class);
                intent.putExtra("jump",res);
                context.startActivity(intent);
            }
        });

//        holder.duration.setText(time);

//        //给控件赋值
//        holder.song.setText(list.get(i).getSong().toString());
//        holder.singer.setText(list.get(i).getSinger().toString());
//        //时间需要转换一下
//        int duration = list.get(i).getDuration();
//        String time = MusicUtils.formatTime(duration);
//        holder.duration.setText(time);
        holder.position.setText(i+1+"");

        return view;
    }

    class ViewHolder {
        TextView song;// 歌曲名
        TextView singer;// 歌手
        ImageView jump;
        TextView position;// 序号
    }

}