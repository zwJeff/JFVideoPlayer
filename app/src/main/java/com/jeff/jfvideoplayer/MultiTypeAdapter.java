package com.jeff.jfvideoplayer;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * 说明：
 * 作者： 张武
 * 日期： 2017/5/10.
 * email:wuzhang4@creditease.cn
 */

public class MultiTypeAdapter extends BaseAdapter {

    public static final int HOT_NEWS_ADAPTER = 1;
    public static final int HOT_STORES_ADAPTER = 2;
    public static final int NEWS_LIST_ADAPTER = 3;
    public static final int AWARD_LIST_ADAPTER = 4;
    public static final int Activity_LIST_ADAPTER = 5;

    private Activity context;
    private List<Video> mData;

    public MultiTypeAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewsListItemHolder holder = null;
        final Video data = mData.get(position);
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.news_list_item, null);
            holder = new NewsListItemHolder();
            holder.img = (ImageView) convertView.findViewById(R.id.pic);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            convertView.setTag(holder);
        } else {
            holder = (NewsListItemHolder) convertView.getTag();
        }
        holder.title.setText(data.getName());
        holder.time.setText(data.getPath());
        FileManager.displayVideoThumbnail(data.getPath(), holder.img);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlayVideoActivity.startActivity(context,data.getPath());

//                Intent openVideo = new Intent(Intent.ACTION_VIEW);
//
//                Uri video = FileProvider.getUriForFile(context,
//                        context.getApplicationContext().getPackageName() + ".fileprovider",
//                        new File(data.getPath()));
//                openVideo.setDataAndType(video, "video/*");
//                context.startActivity(openVideo);
            }
        });
        return convertView;
    }

    public List<Video> getData() {
        return mData;
    }

    public MultiTypeAdapter setData(List<Video> mData) {
        this.mData = mData;
        return this;
    }


    class NewsListItemHolder {
        private ImageView img;
        private TextView title;
        private TextView time;
    }

}
