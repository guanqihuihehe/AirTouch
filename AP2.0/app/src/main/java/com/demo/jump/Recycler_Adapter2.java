package com.demo.jump;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fruitbasket.audioplatform.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
//该适配器为显示所有app的RecycleView的适配器
public class Recycler_Adapter2 extends RecyclerView.Adapter<Recycler_Adapter2.ViewHolder>{
    private List<app_item> list;
    static private int clickposition=-1;//用来判断点击的区域
    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView app_name;
        private CircleImageView app_image;
        public ViewHolder(View view){
            super(view);
            app_image=view.findViewById(R.id.look_imageicon);
            app_name=view.findViewById(R.id.app_name3);
        }
    }
    Recycler_Adapter2(List<app_item> list){
        this.list=list;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.app_look,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {//设置交互
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition()!=-1&&Recycler_Adapter.getClickposition()!=-1){
                    clickposition=holder.getAdapterPosition();//获取当前点击的位置
                    Jump_Activity.changing();
                    return;
                }
                if((int)holder.getAdapterPosition()==clickposition){
                    clickposition=-1;
                    notifyDataSetChanged();//刷新界面
                    return;
                }
                clickposition=holder.getAdapterPosition();//获取当前点击的位置
                holder.itemView.setBackgroundColor(Color.rgb(220,220 ,220));//调整背景颜色使其显示为被选中
                notifyDataSetChanged();//刷新界面
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
        final app_item app_item1=list.get(position);
        holder.app_image.setImageDrawable(app_item1.getAppIcon());
        holder.app_name.setText(app_item1.getName());
        if(position==clickposition)//如果该区域是当前被点击的则置灰色
            holder.itemView.setBackgroundColor(Color.rgb(220,220 ,220));
        else//反之置白色
            holder.itemView.setBackgroundColor(Color.rgb(255,255,255));
    }
    @Override
    public int getItemCount(){
        return list.size();
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public static int getClickposition() {
        return clickposition;
    }
    public List<app_item> getList() {
        return list;
    }
    public static void setClickposition(int clickposition) {
        Recycler_Adapter2.clickposition = clickposition;
    }
}
