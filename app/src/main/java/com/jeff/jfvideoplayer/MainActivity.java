package com.jeff.jfvideoplayer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;


public class MainActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("诗词诗歌童话视频大全");
        findViewById(R.id.tonghua).setOnClickListener(this);
        findViewById(R.id.shige).setOnClickListener(this);
        findViewById(R.id.shici).setOnClickListener(this);
        findViewById(R.id.all).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.hasPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            creatFolder();
        } else {
            PermissionUtils.requestPermission(this, 1, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 1) return;
        if (PermissionUtils.checkPermissionResult(grantResults)) {
            creatFolder();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("权限管理")
                    .setMessage("请允许我访问您的内存，不然无法正常获取视频")
                    .setPositiveButton("好的，去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PermissionUtils.gotoAppDetialsPage(MainActivity.this);
                        }
                    }).create().show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tonghua:
                VideoListActivity.startActivity(MainActivity.this, 1);
                break;
            case R.id.shici:
                VideoListActivity.startActivity(MainActivity.this, 2);
                break;
            case R.id.shige:
                VideoListActivity.startActivity(MainActivity.this, 3);
                break;
            case R.id.all:
                VideoListActivity.startActivity(MainActivity.this, 0);
                break;
        }
    }

    private void creatFolder(){
        if (!VideoListActivity.basePath.exists()) {
            VideoListActivity.basePath.mkdir();
        }
        if (!VideoListActivity.shiciPath.exists()) {
            VideoListActivity.shiciPath.mkdir();
        }
        if (!VideoListActivity.shigePath.exists()) {
            VideoListActivity.shigePath.mkdir();
        }
        if (!VideoListActivity.tonghuaPath.exists()) {
            VideoListActivity.tonghuaPath.mkdir();
        }
    }

}
