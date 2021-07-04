package com.example.musicapplication.activities;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.MusicListSheet;
import com.example.musicapplication.MusicService;
import com.example.musicapplication.MyAdapter;
import com.example.musicapplication.R;
import com.example.musicapplication.SheetAdd;
import com.example.musicapplication.bean.Sheet;
import com.example.musicapplication.bean.Song;

import java.util.ArrayList;
import java.util.List;


import static com.example.musicapplication.MainActivity.sheet;
import static com.example.musicapplication.MainActivity.setSheet;
import static com.example.musicapplication.MainActivity.sheetAdapter;
public class MusicListActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView nextIv,playIv,lastIv,albumIv;
    private TextView singerTv,songTv;
    private SearchView musicSearch;
    private RecyclerView musicRv;
    public static List<Song> list;
    public static MyAdapter adapter;

    //与服务有关的部分
    private int musicDataSize;
    public int currentId=-2;
    private MusicService.MyBinder mMyBinder;
    private ServiceConnection serviceConnection;
    private Handler handler;
    private SeekBar seekBar;
    private Runnable runnable;
    private BroadcastReceiver myReceiver;
    private boolean isNetAvailable=false;

    private ImageView addImageView;
    private Sheet listAdded;
   // private MusicListSheet listSheet;

    Sheet listHistory,listRecommendation;
    private MusicListSheet listSheet;
    private int positionList;
    private Song song;
 //   private List<Sheet> setSheet, mainSheet;



    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_list);


        if(ContextCompat.checkSelfPermission(MusicListActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MusicListActivity.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else {
            initView();
        }



      /*  addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MusicListActivity.this, SheetAdd.class);
                startActivityForResult(addIntent,0);
            }
        });
*/


        listSheet=MusicListSheet.get(getApplicationContext());
        Intent serviceStart = getIntent();
        positionList=serviceStart.getIntExtra("CLICK_POSITION",0);//点历史歌单 position=1 ok
        sheet= listSheet.getSheet().get(positionList);// error null 历史

      //  list=sheet.getList();
//        song=list.get(serviceStart.getIntExtra("CLICK_POSITION",0));


        initData();
        //   ((MyAdapter) adapter).setEmptyView(getView(R.layout.activity_empty));
        //启动音乐服务
        // Intent serviceStart = new Intent(this, MusicService.class);
        serviceStart = new Intent(this, MusicService.class);
        startService(serviceStart);

        serviceStart.putExtra("POSITION",positionList);
        //绑定服务
        Intent mediaServiceIntent = new Intent(this, MusicService.class);
        serviceConn();
        bindService(mediaServiceIntent, serviceConnection, BIND_AUTO_CREATE);



        //设置广播接受者
        setReceiver();

        //设置搜索框
        setSearchList();

      /*  mainSheet =new ArrayList<>();
        setSheet = new ArrayList<>();
        mainSheet =listSheet.getSheet();
        setSheet.addAll(mainSheet);
        sheetAdapter = new SheetAdapter(R.layout.item_sheet, setSheet);
        recyclerView.setAdapter(sheetAdapter);
      /*  (( SheetAdapter)sheetAdapter ).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(MusicListActivity.this, MusicListActivity.class);
                intent.putExtra("CLICK_POSITION",position);
                startActivity(intent);
            }
        });
        */

        //设置点击事件
        setEventListener();
        adapter.notifyDataSetChanged();
        Toolbar listToolbar = findViewById(R.id.menu_bar);
        listToolbar.setTitle(sheet.getName());
        setSupportActionBar(listToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        listToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }
    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        adapter.notifyDataSetChanged();
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if(positionList!=0&&positionList!=1/*&&positionList!=2*/) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否删除此列表？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

               //setSheet.clear(); //setSheet.addAll(listSheet.getSheet());
                 //           sheetAdapter.notifyDataSetChanged();

                            listSheet.getSheet().remove(positionList);
                            setSheet.clear();
                            setSheet.addAll(listSheet.getSheet()); sheetAdapter.notifyDataSetChanged();
                            Toast.makeText(MusicListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                            finish();   sheetAdapter.notifyDataSetChanged();
                        }
                    });
                    builder.show();
                    adapter.notifyDataSetChanged();
                }/*else if(positionList==2){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否清空推荐歌单？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            listSheet=MusicListSheet.get(getApplicationContext());
                            listRecommendation=listSheet.getSheet().get(2);
                            listRecommendation.getList().clear();

                        }
                    });
                    builder.show();
                    adapter.notifyDataSetChanged();


                }*/else if(positionList==1){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否清空历史记录？")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                           list.clear();//实时更新数据源
                            listSheet=MusicListSheet.get(getApplicationContext());
                            listHistory=listSheet.getSheet().get(1);
                            listHistory.getList().clear();//
                            adapter.notifyDataSetChanged();//实时更新数据源
                            list.addAll(sheet.getList());//实时更新数据源
                            Toast.makeText(MusicListActivity.this, "清除成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                    adapter.notifyDataSetChanged();
                }else if(positionList==0){
                    Toast.makeText(this, "此列表不可删除", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_settings:
                if(positionList!=0&&positionList!=1) {
                    Intent intentDrag = new Intent(MusicListActivity.this, ItemDragActivity.class);
                    intentDrag.putExtra("POSITION",positionList);
                    startActivity(intentDrag);
                }else{
                    Toast.makeText(this, "此列表不可修改", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        //扫描本地音乐
        //data = new ArrayList<>();
        list = new ArrayList<>();
        //mListView = (ListView) findViewById(R.id.);
        //list = MusicUtils.getMusicData(this);
        //data.addAll(list);
        list.addAll(sheet.getList());//   ERROR data null   sheet null
        // 创建适配器对象
        adapter = new MyAdapter(this,list);
        musicRv.setAdapter(adapter);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        musicRv.setLayoutManager(layoutManager);

        if(list==null) {
            currentId=-2;
        } else{
            currentId=-1;
        }
        musicDataSize = list.size();

        //数据源变化，提示适配器更新
        adapter.notifyDataSetChanged();

    }

    public void initView() {

        //页面控件
        nextIv = findViewById(R.id.bottom_iv_next);
        playIv = findViewById(R.id.bottom_iv_play);
        lastIv = findViewById(R.id.bottom_iv_last);
        albumIv = findViewById(R.id.bottom_iv_icon);
        singerTv = findViewById(R.id.bottom_tv_singer);
        songTv = findViewById(R.id.bottom_tv_song);
        seekBar = findViewById(R.id.music_seekBar);
        musicSearch = findViewById(R.id.music_search);
        musicRv = findViewById(R.id.music_rv);
        addImageView= findViewById(R.id.image_view_player_add);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        //Notification_height = getResources().getDimensionPixelSize(resourceId);
        nextIv.setOnClickListener(this);
        lastIv.setOnClickListener(this);
        playIv.setOnClickListener(this);
        seekBar.setProgress(0);

    }
    private void serviceConn() {
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                mMyBinder = (MusicService.MyBinder) iBinder;

                //如果启动时正在播放
                if(mMyBinder.getMediaPlayState()==1) {
                    playIv.setImageResource(R.mipmap.icon_pause);
                    songTv.setText(mMyBinder.getMusicSong());
                    singerTv.setText(mMyBinder.getMusicSinger());
                    currentId=mMyBinder.getMusicId();
                    //设置进度条大小
                    seekBar.setMax(mMyBinder.getMusicDuration());
                    Log.d("currentId", Integer.toString(currentId));
                }

                //传递播放列表
                mMyBinder.setDatas(list);

                //初始化进度条
                seekBar.setProgress(0);
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        //响应用户点击设置进度条
                        if(b && currentId!=-1 &&currentId!=-2) {
                            mMyBinder.seekToPosition(seekBar.getProgress());
                            playIv.setImageResource(R.drawable.ripple_pause);
                        } else if(b){
                            Toast.makeText(MusicListActivity.this, "请选择播放音乐", Toast.LENGTH_SHORT).show();
                            seekBar.setProgress(0);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        //mMyBinder.seekToPosition(seekBar.getProgress());
                    }
                });

                //设置进度条控制线程
                handler = new Handler();
                runnable = new Runnable() {
                    private int pre=-1, pos;
                    @Override
                    public void run() {
                        pos = mMyBinder.getPlayPosition();
                        if(currentId!=-1 && currentId!=-2)
                            seekBar.setProgress(pos);
                        Log.d("RunnablePos", String.valueOf(pos));

                        if(pre!=pos) handler.postDelayed(runnable, 1000);
                        else handler.postDelayed(runnable, 2000);

//                        //修复最后一首时播放图标bug
//                        if(currentId==listSiz && pos!=pre) playIv.setImageResource(R.mipmap.icon_pause);
                        pre = pos;
                    }
                };
                handler.post(runnable);
            }
            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
    }

    private void setEventListener() {
        /* 设置点击事件*/
        adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, int position) {
                Song bean = list.get(position);
                mMyBinder.updateHistory(position);//更新历史记录
                //     mMyBinder.updateRecommendation(position);//更新推荐歌单
                song=bean;
                playMusicOnService(position);

            }
        });

        //设置单曲循环/列表循环
        albumIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMyBinder.setPlayModule();
                if(mMyBinder.getPlayModule()){
                    Toast.makeText(MusicListActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
                    albumIv.setImageResource(R.mipmap.icon_song2_loop_blue);
                } else {
                    Toast.makeText(MusicListActivity.this, "列表循环", Toast.LENGTH_SHORT).show();
                    albumIv.setImageResource(R.mipmap.icon_song2);
                }
            }
        });
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(MusicListActivity.this, SheetAdd.class);
                startActivityForResult(addIntent,0);
            }
        });



    }

    private void setReceiver() {
        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                songTv.setText(intent.getStringExtra("music_song"));
                singerTv.setText(intent.getStringExtra("music_singer"));
                currentId = intent.getIntExtra("music_id", -1);
                seekBar.setMax(intent.getIntExtra("music_duration", 0));
            }
        };
        IntentFilter intentFilter = new IntentFilter("UI_info");
        registerReceiver(myReceiver, intentFilter);
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
                    list.clear();
                    list.addAll(sheet.getList());
                    adapter.notifyDataSetChanged();
                } else {
                    //根据输入内容对RecycleView搜索
                    list.clear();
                    for (Song bean:sheet.getList()){
                        if(bean.getSong().contains( capitalize(s))|bean.getSong().contains( capitalize2(s))|bean.getSong().contains(s.toLowerCase()) | bean.getSong().contains(s.toUpperCase()) |bean.getSong().contains(s)) {
                            list.add(bean);
                        }
                    }

                    adapter.notifyDataSetChanged();
                }
                return true;
            }
        });

    }
    public void playMusicOnService(int Id) {
        /*根据传入ID播放音乐*/
        //设置服务信息
        mMyBinder.setMusic(Id);
        //切换按钮图片
        playIv.setImageResource(R.drawable.ripple_pause);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_iv_last:
                if(currentId==-2){
                    //没有播放音乐
                    Toast.makeText(this, "没有获取到音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(currentId==-1){
                    //没有播放音乐
                    Toast.makeText(this, "开始播放第一首", Toast.LENGTH_SHORT).show();
                    currentId=1;
                }
                if (currentId==0) {
                    Toast.makeText(this,"已经是第一首了~", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentId = currentId-1;
                playMusicOnService(currentId);

                break;
            case R.id.bottom_iv_next:
                if(currentId==-2){
                    //没有播放音乐
                    Toast.makeText(this, "没有获取到音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(currentId==-1){
                    //没有播放音乐
                    Toast.makeText(this, "开始播放最后一首~", Toast.LENGTH_SHORT).show();
                    currentId = musicDataSize-2;
                }
                if (currentId==musicDataSize-1) {
                    Toast.makeText(this,"没有下一首了~", Toast.LENGTH_SHORT).show();
                    return;
                }
                currentId=currentId+1;
                playMusicOnService(currentId);

                break;
            case R.id.bottom_iv_play:
                if(currentId==-1){
                    //没有播放音乐
                    Toast.makeText(this, "请选择播放音乐", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(currentId==-2){
                    Toast.makeText(this, "请打开软件存储权限", Toast.LENGTH_SHORT).show();
                    return;
                }
                int state = mMyBinder.getMediaPlayState();
                if(state==1) {
                    mMyBinder.pauseMusic();
                    playIv.setImageResource(R.drawable.ripple_play);
                } else if(state==0){
                    mMyBinder.playMusic();
                    playIv.setImageResource(R.drawable.ripple_pause);
                } else if(state==2){
                    Toast.makeText(this, "播放结束了~", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private View getView(int viewId) {
        return LayoutInflater.from(this).inflate(viewId, new RelativeLayout(this));
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
        handler.removeCallbacks(runnable);
        unbindService(serviceConnection);
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(song!=null) {
        if(resultCode==1){
            assert data != null;
            int addListPosition=data.getIntExtra("ADD_LIST",-1)+2;
            if(addListPosition==-1){
                return;
            }

                listAdded = listSheet.getSheet().get(addListPosition);
                if (!listAdded.contains(song)) {
                    listAdded.add(song);
                } else {
                    Toast.makeText(this, "已在列表中", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}