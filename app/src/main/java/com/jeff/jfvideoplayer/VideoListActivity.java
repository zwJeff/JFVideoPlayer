package com.jeff.jfvideoplayer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.List;

public class VideoListActivity extends Activity {

    ListView listView;
    ImageView emptyView;
    MultiTypeAdapter mAdapter;

    public static final File basePath = new File(Environment.getExternalStorageDirectory(), "A诗词童话视频");
    public static final File shiciPath = new File(basePath, "诗词");
    public static final File shigePath = new File(basePath, "诗歌");
    public static final File tonghuaPath = new File(basePath, "童话");

    public static void startActivity(Activity activity, int type) {
        Intent intent = new Intent();
        intent.setClass(activity, VideoListActivity.class);
        intent.putExtra("type", type);
        activity.startActivity(intent);
    }

    private List<Video> videoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        int type = getIntent().getIntExtra("type", 0);
        listView = (ListView) findViewById(R.id.video_list);
        emptyView = (ImageView) findViewById(R.id.empty_view);
        initView(type);
    }

    private void initView(int type) {
        File folder = basePath;
        switch (type) {
            case 0:
                setTitle("全部视频");
                folder = basePath;
                break;
            case 1:
                setTitle("童话视频");
                folder = tonghuaPath;
                break;
            case 2:
                setTitle("诗词视频");
                folder = shiciPath;
                break;
            case 3:
                setTitle("诗歌视频");
                folder = shigePath;
                break;
        }

        videoList = FileManager.getVideos(folder);
        if (videoList.size() > 0) {
            listView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            mAdapter = new MultiTypeAdapter(this);
            mAdapter.setData(videoList);
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else {
            listView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }


}
