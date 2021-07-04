package com.example.musicapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.Toolbar;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.musicapplication.bean.Sheet;

import java.util.List;

public class SheetAdd extends AppCompatActivity {
    private MusicListSheet listSheet;
    private List<Sheet> sheet;
    private List<Sheet> subSheet;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Intent intent = getIntent();
        listSheet =MusicListSheet.get(getApplicationContext());
        sheet= listSheet.getSheet();
        subSheet=sheet.subList(2,sheet.size());
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view_add);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SheetAdapter(R.layout.item_sheet, subSheet);
       ((SheetAdapter) adapter).setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent=new Intent();
                intent.putExtra("ADD_LIST",position);
                setResult(1, intent);
                finish();
            }
        });
        recyclerView.setAdapter(adapter);
        Toolbar addToolbar = (Toolbar) findViewById(R.id.toolbar_add);
        setSupportActionBar(addToolbar);
    }

    private View getView(int viewId) {
        return LayoutInflater.from(this).inflate(viewId, new RelativeLayout(this));
    }

}
