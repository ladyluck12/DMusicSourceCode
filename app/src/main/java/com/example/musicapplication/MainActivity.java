package com.example.musicapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
//import android.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.musicapplication.activities.MusicListActivity;
import com.example.musicapplication.bean.Sheet;
import com.google.android.material.navigation.NavigationView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView menuIv;
    //侧滑栏部分
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private Fragment fragment_about, fragment_introduction, fragment_message;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int Notification_height;
    private SearchView musicSearch;
    public static Sheet sheet;

    public static List<Sheet> setSheet, mainSheet;

    private static final String TAG = "MainActivity";
    private static final int MY_REQUEST_CODE = 1713;
    private MusicListSheet listSheet;
    private RecyclerView recyclerView;
    public static RecyclerView.Adapter sheetAdapter;
    private RecyclerView.LayoutManager layoutManager;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initView();

        setSearchList();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_lists);


        mainSheet =new ArrayList<>();
        setSheet = new ArrayList<>();

        load();
        listSheet=MusicListSheet.get(getApplicationContext());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mainSheet =listSheet.getSheet();
        setSheet.addAll(mainSheet);

        sheetAdapter = new SheetAdapter(R.layout.item_sheet, setSheet);
       // adapter = new SheetAdapter(R.layout.item_sheet, listSheet.getSheet());

        (( SheetAdapter) sheetAdapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MainActivity.this, MusicListActivity.class);
                intent.putExtra("CLICK_POSITION",position);
                startActivity(intent);
            }
        });

        //设置侧滑栏//设置侧滑栏
        setDrawer();
        //设置点击事件
        setEventListener();
        //设置菜单fragment页面
        setFragment();


        recyclerView.setAdapter(sheetAdapter);
        Toolbar mainToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mainToolbar.setTitle("");
        setSupportActionBar(mainToolbar);

    }
    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onResume() {
        save();
        super.onResume();
        sheetAdapter.notifyDataSetChanged();
        Log.d(TAG, "onResume: "+ listSheet.size());

    }

    protected void onPause() {
        save();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        save();
        MusicIntentService musicIntent=MusicIntentService.get(getApplicationContext());
        stopService(musicIntent.getIntent());
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {//nameNewList 弹窗输入
            final EditText inputServer = new EditText(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("输入歌单名称")
                    .setView(inputServer)
                    .setNegativeButton("取消", null);
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    String nameNewList = inputServer.getText().toString();
                    listSheet.add(new Sheet(nameNewList));
                    //实时更新数据源
                    setSheet.clear();
                    setSheet.addAll(listSheet.getSheet());
                    //
                    sheetAdapter.notifyDataSetChanged();
                }
            });
            builder.show();
            sheetAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        sheetAdapter.notifyDataSetChanged();
    }

    private void save(){
        try {
            FileOutputStream fos=openFileOutput("save_list", MODE_PRIVATE);
            ObjectOutputStream oos=new ObjectOutputStream(fos);
            oos.writeObject(listSheet);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean load(){
        try {
            FileInputStream fis=openFileInput("save_list");
            ObjectInputStream ois=new ObjectInputStream(fis);
            boolean flag=MusicListSheet.set(ois.readObject());
            ois.close();
            fis.close();
            return flag;
        } catch (IOException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }





    private void setDrawer() {
        navigationView.setItemIconTintList(null);
        navigationView.getChildAt(0).setVerticalScrollBarEnabled(false);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.item_0:
                        Log.d("ItemSelectedListener", "item0");
                        //fragmentManager.popBackStackImmediate(null, 1);
                        transaction = fragmentManager.beginTransaction();
                        transaction.remove(fragment_about).commit();
                        transaction = fragmentManager.beginTransaction();
                        transaction.remove(fragment_introduction).commit();
                        transaction = fragmentManager.beginTransaction();
                        transaction.remove(fragment_message).commit();
//                        Toast.makeText(MainActivity.this, "!!!!", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.item_2:
                        //弹出功能介绍
                        transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fragment, fragment_introduction).commit();
                        Log.d("ItemSelectedListener", "item2");
//                        Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_3:
                        //退出

                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("确定要退出吗？")
                                .setIcon(android.R.drawable.ic_dialog_info)
                                .setNegativeButton("取消", null);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "退出成功", Toast.LENGTH_SHORT).show();
                                finish();   sheetAdapter.notifyDataSetChanged();
                            }
                        });
                        builder.show();
                        break;
                }
                sheetAdapter.notifyDataSetChanged();
                drawerLayout.closeDrawer(GravityCompat.START);
                transaction.addToBackStack(null);
                return true;
            }
        });
        //设置滑动主activity跟随
        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //获取高度宽度
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                Display display = MainActivity.this.getWindowManager().getDefaultDisplay();
                display.getMetrics(metrics);
                //设置activity高度，注意要加上状态栏高度
                RelativeLayout relativeLayout = findViewById(R.id.main_activity);
                relativeLayout.layout(drawerView.getRight(), Notification_height, drawerView.getRight()+metrics.widthPixels, metrics.heightPixels);

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }
    private void setEventListener() {

        menuIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }
    private void setFragment() {
        fragmentManager = getSupportFragmentManager();
        fragment_message = new Frag_message();
        fragment_introduction = new Frag_introduction();
        fragment_about = new Frag_quit();
        transaction = fragmentManager.beginTransaction();
    }
    private void initView() {
        /*初始化控件的函数*/
        musicSearch = findViewById(R.id.sheet_search);
        //侧滑栏控件
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.nav_view);
        menuIv = findViewById(R.id.menu_icon);
        //获取状态栏高度
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        Notification_height = getResources().getDimensionPixelSize(resourceId);


    }
    public String capitalize(String string) {//搜索 首字母大写
        String finalstr="";
        String[] a=   string.split(" ");
        for(int i=0;i<a.length;i++){
            a[i]=a[i].substring(0, 1).toUpperCase()+a[i].substring(1);
            finalstr=finalstr+a[i]+" ";
        }
        finalstr=finalstr.substring(0, finalstr.length()-1);
        return finalstr;
    }
    public String capitalize2(String string) {//搜索 和上面反过来
        String finalstr="";
        String[] a=   string.split(" ");
        for(int i=0;i<a.length;i++){
            a[i]=a[i].substring(0, 1)+a[i].substring(1).toLowerCase();
            finalstr=finalstr+a[i]+" ";
        }
        finalstr=finalstr.substring(0, finalstr.length()-1);
        return finalstr;
    }
    private void setSearchList() {

        //设置SearchView默认是否自动缩小为图标
        musicSearch.setIconifiedByDefault(true);
        musicSearch.setFocusable(false);

        //设置搜索框监听器
        musicSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //点击搜索按钮时激发
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //输入时激发
                if(TextUtils.isEmpty(s)){
                    //没有过滤条件内容
                    setSheet.clear();
                    setSheet.addAll(listSheet.getSheet());
                    sheetAdapter.notifyDataSetChanged();
                } else {
                    //根据输入内容对RecycleView搜索
                    setSheet.clear();
                    for (Sheet bean: listSheet.getSheet()){
                        if(bean.getName().contains( capitalize(s))|bean.getName().contains( capitalize2(s))|bean.getName().contains(s.toLowerCase()) | bean.getName().contains(s.toUpperCase()) |bean.getName().contains(s)  ) {
                            setSheet.add(bean);
                        }
                    }

                    sheetAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }
}


