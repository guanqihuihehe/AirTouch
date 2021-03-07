package com.demo.dialing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fruitbasket.audioplatform.R;

import java.util.List;

public class ContactAdapter extends ArrayAdapter<Contacts_member> {//自定义适配器
    private int resoureceId;
    public ContactAdapter(Context context, int textViewResourceId, List<Contacts_member> objects){
        super(context,textViewResourceId,objects);
        resoureceId=textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){//获取视图
        Contacts_member contacts_member=getItem(position);
        View view;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resoureceId, parent, false);
        }
        else{
            view=convertView;
        }
        TextView PhoneNumber=(TextView)view.findViewById(R.id.Text_PhoneNumber);
        TextView Name=(TextView)view.findViewById(R.id.Text_Name);
        PhoneNumber.setText(contacts_member.get_PhoneNumber());
        Name.setText(contacts_member.get_Name());
        return view;
    }
}
