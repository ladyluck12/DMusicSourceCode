package com.example.musicapplication;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.bean.Song;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.LocalMusicViewHolder>{
    Context context;
    List<Song>mDatas;
    OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void OnItemClick(View view,int position);
    }

    public MyAdapter(Context context, List<Song> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @NonNull
    @Override
    public LocalMusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_song,parent,false);
        LocalMusicViewHolder holder = new LocalMusicViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocalMusicViewHolder holder, final int position) {
        Song musicBean = mDatas.get(position);
        if (musicBean != null) {
            holder.idTv.setText(musicBean.getId());
            holder.songTv.setText(musicBean.getSong());
            holder.singerTv.setText(musicBean.getSinger());
            holder.albumTv.setText(musicBean.getAlbum());
            //转换时间格式
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String time = sdf.format(new Date(musicBean.getDuration()));
            holder.timeTv.setText(time);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.OnItemClick(v, position);
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class LocalMusicViewHolder extends RecyclerView.ViewHolder{
        TextView idTv,songTv,singerTv,albumTv,timeTv;
        public LocalMusicViewHolder(View itemView) {
            super(itemView);
            idTv = itemView.findViewById(R.id.music_num);
            songTv = itemView.findViewById(R.id.music_song);
            singerTv = itemView.findViewById(R.id.music_singer);
            albumTv = itemView.findViewById(R.id.music_album);
            timeTv = itemView.findViewById(R.id.music_duration);
        }
    }

}

