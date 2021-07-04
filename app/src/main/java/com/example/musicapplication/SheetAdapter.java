package com.example.musicapplication;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.musicapplication.bean.Sheet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SheetAdapter extends BaseQuickAdapter<Sheet, BaseViewHolder> {
   public  SheetAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }
    @Override
    protected void convert(BaseViewHolder viewHolder, Sheet item) {
        viewHolder.setText(R.id.name_text_view, item.getName());
        viewHolder.setText(R.id.number_text_view,item.getNumString());
    }
}
