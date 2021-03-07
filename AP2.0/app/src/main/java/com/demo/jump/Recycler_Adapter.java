package com.demo.jump;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.fruitbasket.audioplatform.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import static com.fruitbasket.audioplatform.Constents.applist;
//该适配器为jump_activity 宝座RecycleView适配器
public class Recycler_Adapter extends RecyclerView.Adapter<Recycler_Adapter.ViewHolder> {
    private List<look_app_item> list;
    static private int clickposition=-1;
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView control_num;
        private CircleImageView app_image;
        private int defItem = -1;
        private AdapterView.OnItemClickListener onItemListener;
        public ViewHolder(View view){
            super(view);
            app_image=view.findViewById(R.id.app_icon);
            control_num=view.findViewById(R.id.snum);
        }
    }
    Recycler_Adapter(List<look_app_item> list){
        this.list=list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.app_choice,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition()!=-1&&Recycler_Adapter2.getClickposition()!=-1){
                    clickposition=holder.getAdapterPosition();//获取当前点击的位置
                    List<look_app_item> main_templist=applist;//获取MainActivity的链表对其进行修改
                    look_app_item tempitem=list.get(clickposition);
                    for(int i=0;i<main_templist.size();i++){
                        if(tempitem.getControl_num()==main_templist.get(i).getControl_num()){
                            main_templist.remove(i);
                            break;
                        }
                    }
                    Jump_Activity.changing();
                    return;
                }
                if((int)holder.getAdapterPosition()==clickposition&&list.get(clickposition).get_app_name().length()==0){//点击已被选中的空宝座则取消选中
                    clickposition=-1;
                    notifyDataSetChanged();
                    return;
                }
                else if(list.get((int)holder.getAdapterPosition()).get_app_name().length()!=0){//点击已有app的宝座则将其移出宝座
                    int tempposition=holder.getAdapterPosition();
                    clickposition=-1;
                    look_app_item tempitem=list.get(tempposition);
                    look_app_item temp=new look_app_item("",Jump_Activity.draw,tempposition);
                    List<look_app_item> main_templist=applist;//获取MainActivity的链表对其进行修改
                    for(int i=0;i<main_templist.size();i++){
                        if(tempitem.getControl_num()==main_templist.get(i).getControl_num()){
                            main_templist.remove(i);
                            break;
                        }
                    }
                    list.remove(tempposition);
                    list.add(tempposition,temp);
                    notifyDataSetChanged();
                    return;
                }
                clickposition=holder.getAdapterPosition();
                holder.itemView.setBackgroundColor(Color.rgb(220,220 ,220));//将其背景设置为灰色
                notifyDataSetChanged();
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        look_app_item app_item1=list.get(position);
        holder.control_num.setText(""+app_item1.getControl_num());
        holder.app_image.setImageDrawable(app_item1.get_image_resourece());
        if(position==clickposition)//如果当前item为被选中的item，设置为灰色背景
            holder.itemView.setBackgroundColor(Color.rgb(220,220 ,220));
        else
            holder.itemView.setBackgroundColor(Color.rgb(255,255,255));
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
    public List<look_app_item> getList() {
        return list;
    }
    public static int getClickposition() {
        return clickposition;
    }
    public static void setClickposition(int clickposition) {
        Recycler_Adapter.clickposition = clickposition;
    }
}
