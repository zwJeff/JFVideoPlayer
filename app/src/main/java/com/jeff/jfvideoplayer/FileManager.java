package com.jeff.jfvideoplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @创建人 chaychan
 * @创建时间 2016/7/23  14:34
 * @描述 文件管理者, 可以获取本机的各种文件
 */
public class FileManager {

    private static FileManager mInstance;
    private static Context mContext;
    public static ContentResolver mContentResolver;
    private static Object mLock = new Object();



    /**
     * 获取本机视频列表
     * @return
     */
    public static List<Video> getVideos(File folder) {

        List<Video> videos = new ArrayList<Video>();

            File[] files= folder.listFiles();
            for(File f:files){
                if(f.isDirectory()){
                    videos.addAll(getVideos(f));
                }else{
                    if(f.getName().endsWith(".mp4")||f.getName().endsWith(".MP4")||
                            f.getName().endsWith(".mP4")||f.getName().endsWith(".Mp4")){
                        Video video=new Video();
                        video.setName(f.getName());
                        video.setPath(f.getPath());
                        video.setUri(f.toURI());
                        videos.add(video);
                    }
                }
            }

        return videos;
    }

    // 获取视频缩略图

    public static void displayVideoThumbnail(final String filePath, final ImageView img) {

        new AsyncTask<ImageView, Object, Bitmap>() {
            @Override
            protected Bitmap doInBackground(ImageView[] objects) {
                Bitmap bitmap = null;
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                try {
                    retriever.setDataSource(filePath);
                    bitmap=retriever.getFrameAtTime();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                } finally {
                    retriever.release();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null)
                    img.setImageBitmap(bitmap);
            }
        }.execute();
    }
}
