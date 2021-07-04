package com.example.musicapplication;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.musicapplication.bean.Sheet;
import com.example.musicapplication.bean.Song;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.example.musicapplication.MainActivity.sheet;

public class MusicService extends Service {
    //private long music_num;

    private MediaPlayer mediaPlayer;
    private String path;
    private int pausePosition;
    private MyBinder myBinder;
    private String music_singer, music_song;
    private int music_Id, music_duration;
    private List<Song> mDatas;
    private int musicDataSize;
    private boolean playState, playModule;
    //**********************************************
    Sheet listHistory,listRecommendation;
    Song song,song1,song2;
    MusicListSheet listSheet;
    List<Song> list;
    int musicNumber;
    //**********************************************
    public MusicService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        listSheet=MusicListSheet.get(getApplicationContext());
        listHistory=listSheet.getSheet().get(1);
//        listRecommendation=listSheet.getSheet().get(2);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if(mediaPlayer!=null){
                    myBinder.play_next();
                    //playState=true;
                }
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                return true;
            }
        });
        //  listSheet=MusicListSheet.get(getApplicationContext());
        //listHistory=listSheet.getSheet().get(1);
        //初始化信息
        //music_num=0;
        music_Id=-1;
        music_singer="";
        music_song="";
        pausePosition = 0;
        playState=false;
        playModule=false;
        myBinder = new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // positionList=intent.getIntExtra("POSITION_LIST",-1);

        /*if(song!=null){
            mediaPlayer.start();
        }*/

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        return myBinder;
    }


    public class MyBinder extends Binder {

        public boolean setDatas(List<Song> musicBeans){
            Log.d("MusicService----0----", "setDatas");
            mDatas = musicBeans;
            musicDataSize = mDatas.size();
            //music_Id=-1;   网络变化造成列表重新set时，将出现bug
            return true;
        }
        public void updateRecommendation(int position){
            if(position!=-1) {
                song1= mDatas.get(position);
                song2= mDatas.get(position+1);
            }

            if(song!=null){
                song.num++;//播放次数+1
            }

            if (listRecommendation.contains(song)) {
                listRecommendation.remove(song);
            }
            listRecommendation.addFirst(song);// 在列表首部添加元素

        }
        public void updateHistory(int position){
            if(position!=-1) {
                song= mDatas.get(position);
            }

            if(song!=null){

                if(listHistory.contains(song)) {
                    listHistory.remove(song);
                }
            }

            listHistory.addFirst(song);// 在列表首部添加元素

        }
        public void setMusic(int Id){
            Log.d("MusicService----1----", "setMusic");
            //重置播放
            mediaPlayer.reset();
            //进度记录清零
            pausePosition=0;
            //设置新的音乐
            music_Id=Id;
            Song musicBean = mDatas.get(music_Id);

            try {
                //设置信息
                path = musicBean.getPath();
                mediaPlayer.setDataSource(path);
                music_song = musicBean.getSong();
                music_singer = musicBean.getSinger();
                music_duration = (int) musicBean.getDuration();
                // music_num=musicBean.getNum();
                //调用意图服务，更新activity内容
                Intent intentInfo = new Intent("com.example.easymusic.intentService");
                intentInfo.setPackage(getPackageName());
                intentInfo.putExtra("music_id", music_Id);
                intentInfo.putExtra("music_song", music_song);
                intentInfo.putExtra("music_singer", music_singer);
                intentInfo.putExtra("music_duration", music_duration);
                // intentInfo.putExtra("music_duration", music_num);
                startService(intentInfo);
                myBinder.playMusic();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void playMusic() {
            /* 播放音乐的函数*/
            Log.d("MusicService----2----", "playMusic");
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                if (pausePosition == 0) {
                    try {
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        playState=false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    //从暂停到播放
                    mediaPlayer.seekTo(pausePosition);
                    mediaPlayer.start();
                    playState=false;
                }
            }


        }

        public void pauseMusic() {
            /* 暂停音乐的函数*/
            Log.d("MusicService----3----", "pauseMusic");
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                pausePosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();
            }
        }

        public void play_next() {
            //判断是否为单曲循环
            if(!playModule) {
                //判断是否是最后一首
                if (music_Id >= musicDataSize - 1) {
                    //从第一首开始播放
                    music_Id = -1;
                }
                music_Id = music_Id + 1;
            }
            setMusic(music_Id);
            playMusic();
            playState=false;

        }

        public void play_last() {
            music_Id = music_Id-1;
            if(music_Id==-1) return;
            setMusic(music_Id);
            playMusic();

        }

        public void stopMusic() {
            /* 停止音乐的函数*/
            Log.d("MusicService----4----", "stopMusic");
            if (mediaPlayer != null) {
                pausePosition = 0;
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                mediaPlayer.stop();
            }
        }

        public int getPlayPosition() {
            /*返回播放进度*/
            Log.d("MusicService----5----", "getPlayPosition");
            if(mediaPlayer!=null) {
                return mediaPlayer.getCurrentPosition();
            }
            return 0;
        }

        public void seekToPosition(int msec) {
            /*设置播放进度*/
            Log.d("MusicService----6----", "seekToPosition");
            if(mediaPlayer!=null) {
                if(!mediaPlayer.isPlaying()) myBinder.playMusic();
                playState=false;
                mediaPlayer.seekTo(msec);
            }
        }

        public int getMediaPlayState() {
            /*返回播放状态*/
            Log.d("MusicService----7----", "getMediaPlayState");
            if(mediaPlayer!=null) {
//                if(mediaPlayer.getCurrentPosition()+500>=mediaPlayer.getDuration())
//                    return 2;
//                else if(mediaPlayer.isPlaying()) return 1;
                if(mediaPlayer.isPlaying()) return 1;
                else if(playState) return 2;
                else return 0;
            }
            else return 0;
        }

        public String getMusicSinger(){
            Log.d("MusicService----8----", "getMusicSinger");
            if(mediaPlayer!=null) return music_singer;
            else return "";
        }
     /*   public String getMusicNum(){
            Log.d("MusicService----8----", "getMusicSinger");
            if(mediaPlayer!=null) return music_num;
            else return "";
        }*/

        public String getMusicSong() {
            Log.d("MusicService----9----", "getMusicSong");
            if(mediaPlayer!=null) return music_song;
            else return "";
        }

        public int getMusicId(){
            Log.d("MusicService----10----", "getMusicId");
            if(mediaPlayer!=null) return music_Id;
            else return -1;
        }

        public int getMusicDuration(){
            Log.d("MusicService----11----", "getMusicDuration");
            if(mediaPlayer!=null) return mediaPlayer.getDuration();
            else return -1;
        }

        public void setPlayModule(){
            /*设置播放模式*/
            if(playModule) playModule=false;
            else playModule=true;
        }

        public boolean getPlayModule(){
            return playModule;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            pausePosition = 0;
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            mediaPlayer.stop();
        }
    }
}
