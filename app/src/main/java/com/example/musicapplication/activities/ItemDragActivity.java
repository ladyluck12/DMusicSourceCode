package com.example.musicapplication.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.example.musicapplication.ItemDragAdapter;
import com.example.musicapplication.MusicListSheet;
import com.example.musicapplication.R;

import static com.example.musicapplication.MainActivity.sheet;
import static com.example.musicapplication.activities.MusicListActivity.adapter;
import static com.example.musicapplication.activities.MusicListActivity.list;

//import android.widget.Toolbar;

public class ItemDragActivity  extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int positionList;
    private MusicListSheet listSheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draggable);

        Intent intent=getIntent();
        positionList=intent.getIntExtra("POSITION",0);
        listSheet =MusicListSheet.get(getApplicationContext());
        sheet= listSheet.getSheet().get(positionList);

        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_musics_drag);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ItemDragAdapter dragAdapter = new ItemDragAdapter(sheet.getList());
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(dragAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        dragAdapter.enableDragItem(itemTouchHelper, R.id.name_music_text_view_draggable, true);
        dragAdapter.setOnItemDragListener(onItemDragListener);
        dragAdapter.enableSwipeItem();
        dragAdapter.setOnItemSwipeListener(onItemSwipeListener);

        recyclerView.setAdapter(dragAdapter);

        Toolbar dragToolbar = (Toolbar) findViewById(R.id.toolbar_drag);
        dragToolbar.setTitle("修改歌单");
        setSupportActionBar(dragToolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);
        dragToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.clear();//实时更新数据源
                list.addAll(sheet.getList());//实时更新数据源
                adapter.notifyDataSetChanged();//实时更新数据源
                finish();
                list.clear();//实时更新数据源
                list.addAll(sheet.getList());//实时更新数据源
                adapter.notifyDataSetChanged();
            }
        });
        adapter.notifyDataSetChanged();
    }

    OnItemDragListener onItemDragListener = new OnItemDragListener() {

        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){   list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}
        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {   list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}
        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {   list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}
    };

    OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {

        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {  list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}
        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {   list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}
        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {   list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {   list.clear();//实时更新数据源
            list.addAll(sheet.getList());//实时更新数据源
            adapter.notifyDataSetChanged();}
    };

}
