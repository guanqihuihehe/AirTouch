package com.demo.jump;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fruitbasket.audioplatform.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class App_Adapter extends ArrayAdapter<look_app_item> {
    private int resourId;
    public App_Adapter(Context context, int textViewResourceId, List<look_app_item> objects){
        super(context,textViewResourceId,objects);
        resourId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        look_app_item app_item1=getItem(position);
        View view;
        if(convertView==null){
            view=LayoutInflater.from(parent.getContext()).inflate(resourId,parent,false);
        }else{
            view=convertView;
        }
        CircleImageView imageView=(CircleImageView) view.findViewById(R.id.app_image);
        TextView app_name=(TextView)view.findViewById(R.id.app_name);
        TextView control_num=(TextView)view.findViewById(R.id.control_num);
        imageView.setImageDrawable(app_item1.get_image_resourece());
        app_name.setText(app_item1.get_app_name());
        control_num.setText(""+app_item1.getControl_num());
        return view;
    }
}
