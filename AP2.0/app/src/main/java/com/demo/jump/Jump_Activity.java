package com.demo.jump;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fruitbasket.audioplatform.Constents;
import com.fruitbasket.audioplatform.R;
import com.fruitbasket.audioplatform.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.fruitbasket.audioplatform.Constents.applist;

public class Jump_Activity extends AppCompatActivity {
    static public Drawable draw;
    private List<look_app_item> Jump_list=new ArrayList<>();
    private List<app_item> applist=new ArrayList<>();
    private Button ensure;
    static public Recycler_Adapter adapter;
    static public Recycler_Adapter2 adapter3;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<look_app_item> main_templist= AppMainActivity.getJump_list();
        init_Jump();
        if(main_templist.size()==0)//第一次进入初始化链表
            initJump_list();
        else{//后面进入加载存储与MainActivity的链表
            for(int i=0;i<main_templist.size();i++){
                Jump_list.add(main_templist.get(i));
            }
        }
        initapplist();
        setContentView(R.layout.app_jump_);
        ensure=(Button)findViewById(R.id.ensure);
        recyclerView=(RecyclerView)findViewById(R.id.recycle_view);
        recyclerView3=(RecyclerView)findViewById(R.id.recycle_view3);
        recyclerView.setLayoutManager(new GridLayoutManager(this,5));//设置五列的RecycleView
        adapter=new Recycler_Adapter(Jump_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        recyclerView3.setLayoutManager(new GridLayoutManager(this,2));//设置两列的RecycleView
        adapter3=new Recycler_Adapter2(applist);
        recyclerView3.setAdapter(adapter3);
        ensure.setOnClickListener(new View.OnClickListener() {//确定键的交互
            @Override
            public void onClick(View v) {
                adapter.setClickposition(-1);
                adapter3.setClickposition(-1);
                AppMainActivity.setJump_list(Jump_list);
                AppMainActivity.setJump_list(Jump_list);
                List<look_app_item> temp_list=Constents.applist;
                SharedPreferences spt= PreferenceManager.getDefaultSharedPreferences(Jump_Activity.this);
                SharedPreferences.Editor editor=spt.edit();
                editor.clear();
                editor.commit();
                for(int i=0;i<temp_list.size();i++){
                    String pack=temp_list.get(i).getPackagename();
                    String name=temp_list.get(i).get_app_name();
                    int controlname=temp_list.get(i).getControl_num();
                    String dataKey1="used"+controlname;
                    String dataKey2="packageName"+controlname;
                    String dataKey3="name"+controlname;
                    editor.putBoolean(dataKey1,true);
                    editor.putString(dataKey2,pack);
                    editor.putString(dataKey3,name);
                }
                editor.apply();
                finish();
            }
        });
    }
    private void init_Jump(){
        Resources resources = getApplicationContext().getResources();
        Drawable drawable = resources.getDrawable(R.drawable.add);
        draw=drawable;
    }
    private void initJump_list(){//初始化
        for(int i=0;i<10;i++){
            Jump_list.add(new look_app_item("",draw,i));
        }
    }
    private void initapplist(){//初始化
        List<ApplicationInfo> applicationInfoList=queryFilterAppInfo();
        for(ApplicationInfo appInfo : applicationInfoList){
            String name = appInfo.loadLabel(getPackageManager()).toString();
            String package_name=appInfo.packageName;
            Drawable appIcon=appInfo.loadIcon(getPackageManager());
            app_item app_item=new app_item(String.format("%-24s",name),appIcon,package_name);//填充app名字，为了放置
            applist.add(app_item);
        }
    }
    private List<ApplicationInfo> queryFilterAppInfo() {//读取手机内的app信息
        PackageManager pm = this.getPackageManager();
        List<ApplicationInfo> appInfos = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);// GET_UNINSTALLED_PACKAGES代表已删除，但还有安装目录的
        List<ApplicationInfo> applicationInfos = new ArrayList<>();
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveinfoList = getPackageManager()
                .queryIntentActivities(resolveIntent, 0);
        Set<String> allowPackages = new HashSet();
        for (ResolveInfo resolveInfo : resolveinfoList) {
            allowPackages.add(resolveInfo.activityInfo.packageName);
        }
        for (ApplicationInfo app : appInfos) {
            if (allowPackages.contains(app.packageName)) {
                applicationInfos.add(app);
            }
        }
        return applicationInfos;
    }
    @Override
    public void onBackPressed(){//返回时存储jump_list
        adapter.setClickposition(-1);
        adapter3.setClickposition(-1);
        AppMainActivity.setJump_list(Jump_list);
        List<look_app_item> temp_list=Constents.applist;
        SharedPreferences spt= PreferenceManager.getDefaultSharedPreferences(Jump_Activity.this);
        SharedPreferences.Editor editor=spt.edit();
        editor.clear();
        editor.commit();
        for(int i=0;i<temp_list.size();i++){
            String pack=temp_list.get(i).getPackagename();
            String name=temp_list.get(i).get_app_name();
            int controlname=temp_list.get(i).getControl_num();
            String dataKey1="used"+controlname;
            String dataKey2="packageName"+controlname;
            String dataKey3="name"+controlname;
            editor.putBoolean(dataKey1,true);
            editor.putString(dataKey2,pack);
            editor.putString(dataKey3,name);
        }
        editor.apply();
        finish();
    }
    public static void changing(){
        int position1=adapter.getClickposition();
        int position2=adapter3.getClickposition();
        app_item temp=adapter3.getList().get(position2);
        look_app_item temp2=new look_app_item(temp.getName(),temp.getAppIcon(),adapter.getList().get(position1).getControl_num());
        adapter.getList().remove(position1);
        adapter.getList().add(position1,temp2);
        adapter.setClickposition(-1);//清空选中
        adapter3.setClickposition(-1);
        List<look_app_item> temp3=Constents.applist;
        int i=0;
        for(i=0;i<=temp3.size()-1;i++){
            if(temp3.get(i).getControl_num()>adapter.getList().get(position1).getControl_num()){//找到合适的插入位置
                break;
            }
        }
        look_app_item temp4=new look_app_item(temp.getName(),temp.getAppIcon(),adapter.getList().get(position1).getControl_num());
        temp4.setPackagename(adapter3.getList().get(position2).getPackagename());
        temp3.add(i,temp4);
        adapter.notifyDataSetChanged();//刷新界面
        adapter3.notifyDataSetChanged();
    }
}
