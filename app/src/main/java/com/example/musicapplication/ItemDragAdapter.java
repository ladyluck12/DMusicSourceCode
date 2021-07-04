package com.example.musicapplication;

import android.view.View;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.musicapplication.bean.Song;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ItemDragAdapter extends BaseItemDraggableAdapter<Song, BaseViewHolder> {
    public ItemDragAdapter(List<Song> music) {
        super(R.layout.item_song, music);
    }

    @Override
    protected void convert(BaseViewHolder holder, Song item) {
if(item!=null) {
    holder.setText(R.id.music_num, item.getId());
    holder.setText(R.id.music_song, item.getSong());
    holder.setText(R.id.music_singer, item.getSinger());
    holder.setText(R.id.music_album, item.getAlbum());
    SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    String time = sdf.format(new Date(item.getDuration()));
    holder.setText(R.id.music_duration, time);
}
        // (String)item.getDuration()
    }

}