package com.example.musicapplication.activities;


import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapplication.CustomVideoView;
import com.example.musicapplication.MainActivity;
import com.example.musicapplication.R;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_DISPLAY_LENGHT= 2100;    //延迟1.5秒
    private CustomVideoView videoview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        setContentView(R.layout.activity_splash);
        initView();
        final RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(granded -> {
                    if (granded) {
                        //获取权限后
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);	//第二个参数即为执行完跳转后的Activity
                                startActivity(intent);
                                SplashActivity.this.finish();   //关闭splashActivity，将其回收，否则按返回键会返回此界面
                            }
                        }, SPLASH_DISPLAY_LENGHT);
                        Log.d("rxRermision", "get");
                    } else {
                        Toast.makeText(this, "未获取到存储权限", Toast.LENGTH_LONG).show();
                        Log.d("rxRermision", "denied");
                    }
                });


    }


        private void initView() {
            //加载视频资源控件
            videoview = (CustomVideoView) findViewById(R.id.videoview);
            //设置播放加载路径
            //videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video));
            videoview.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.candycrush));
            //播放
            videoview.start();
            //循环播放
            videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    videoview.start();
                }
            });
        }

        //返回重启加载
        @Override
        protected void onRestart() {
            initView();
            super.onRestart();
        }

        //防止锁屏或者切出的时候，音乐在播放
        @Override
        protected void onStop() {
            videoview.stopPlayback();
            super.onStop();
        }

    }

