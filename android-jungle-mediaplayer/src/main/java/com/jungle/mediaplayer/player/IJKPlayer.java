package com.jungle.mediaplayer.player;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;

import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.render.MediaRender;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 说明：
 * 作者： 张武
 * 日期： 2017/12/14.
 */

public class IJKPlayer extends BaseMediaPlayer {


    protected IjkMediaPlayer mMediaPlayer;

    public IJKPlayer(Context context) {
        super(context);
    }

    public IJKPlayer(Context context, MediaRender render) {
        super(context, render);
    }

    private void init(){
        mMediaPlayer = new IjkMediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompletionListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
    }

    @Override
    public void play(VideoInfo videoInfo) {
        if (mMediaPlayer != null) {
            destroy();
        }

        init();
        super.play(videoInfo);

        // Clear old display and Reset.
        Log.e(TAG, "Reset MediaPlayer!");
        mMediaPlayer.setDisplay(null);
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(videoInfo.getStreamUrl()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prepare For Play.
        Log.e(TAG, "Prepare MediaPlayer!");
        try {
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(-1, false, "Video PrepareAsync FAILED! Video might be damaged!!");
            return;
        }

        notifyStartPlay();
        notifyLoading();
    }

    @Override
    public void pause() {
        if (mMediaPlayer == null || !mMediaPlayerIsPrepared) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mIsPaused = true;
            notifyPaused();
        }
    }

    @Override
    public void resume() {
        if (mMediaPlayer == null || !mMediaPlayerIsPrepared || !mMediaRender.isRenderValid()) {
            return;
        }

        mIsPaused = false;
        mMediaPlayer.start();
        notifyResumed();
    }

    @Override
    public void stop() {
        if (mMediaPlayer == null) {
            return;
        }

        mIsPaused = false;
        mMediaPlayerIsPrepared = false;
        mMediaPlayer.stop();

        notifyStopped();
    }

    @Override
    public void seekTo(int millSeconds) {
        if (mMediaPlayer != null) {
            if (millSeconds < 0) {
                millSeconds = 0;
            }

            mIsPaused = false;
            mMediaPlayer.seekTo(millSeconds);
            mMainHandler.postDelayed(mSeekRunnable, 300);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? (int) mMediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer != null ? (int) mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void destroy() {
        super.destroy();

        clearLoadingFailed();
        if (mMediaPlayer != null) {
            stop();

            mMediaPlayer.setDisplay(null);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return mIsPaused;
    }


    @Override
    public boolean hasVideoPlay() {
        return mMediaPlayer != null;
    }

    @Override
    protected void playWithMediaRender() {
//        if (!mMediaPlayerIsPrepared) {
//            return;
//        }
//
//        try {
//            mMediaRender.prepareMediaRender(mMediaPlayer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mMediaPlayer.setScreenOnWhilePlaying(true);
//        mMediaPlayer.start();
//        mMediaPlayer.seekTo(0);
//
//        trySeekToStartPosition();
    }

    @Override
    protected void surfaceHolderChanged() {
        if (mMediaPlayer != null) {
//            mMediaRender.mediaRenderChanged(mMediaPlayer);
        }
    }

    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            mIsLoading = true;
            notifyStartSeek();
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener=new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            String errorWhat;
            switch (i) {
                case IjkMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    errorWhat = "MEDIA_ERROR_UNKNOWN";
                    break;
                case IjkMediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    errorWhat = "MEDIA_ERROR_SERVER_DIED";
                    break;
                default:
                    errorWhat = "!";
            }

            String errorExtra;
            switch (i1) {
                case IjkMediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    errorExtra = "MEDIA_ERROR_UNSUPPORTED";
                    break;
                case IjkMediaPlayer.MEDIA_ERROR_MALFORMED:
                    errorExtra = "MEDIA_ERROR_MALFORMED";
                    break;
                case IjkMediaPlayer.MEDIA_ERROR_IO:
                    errorExtra = "MEDIA_ERROR_IO";
                    break;
                case IjkMediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    errorExtra = "MEDIA_ERROR_TIMED_OUT";
                    break;
                default:
                    errorExtra = "!";
            }

            String msg = String.format("what = %d (%s), extra = %d (%s)",
                    i, errorWhat, i1, errorExtra);

            Log.e(TAG, msg);
            notifyError(i, msg);
            return true;
        }
    };
    private IMediaPlayer.OnPreparedListener mOnPreparedListener=new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            Log.e(TAG, "**SUCCESS** Video Prepared Complete!");

            mAutoPlayWhenHolderCreated = false;
            mMediaPlayerIsPrepared = true;
            mIsLoading = false;

            // Start Play.
            if (mMediaRender.isRenderCreating() || !mMediaRender.isRenderValid()) {
                mAutoPlayWhenHolderCreated = true;
            } else {
                playWithMediaRender();
            }

            clearLoadingFailed();
            notifyFinishLoading();
        }
    };
    private IMediaPlayer.OnCompletionListener mOnCompletionListener=new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            mMainHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMainHandler.removeCallbacks(mSeekRunnable);
                    mIsLoading = false;
                    notifySeekComplete();
                }
            }, 100);
        }
    };
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompletionListener=new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            Log.e(TAG, "Video Play Complete!");

            iMediaPlayer.seekTo(0);
            notifyPlayComplete();
        }
    };
    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener=new IMediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            mBufferPercent = i;
        }
    };
    private IMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener=new IMediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            mVideoWidth = iMediaPlayer.getVideoWidth();
            mVideoHeight = iMediaPlayer.getVideoHeight();

            updateMediaRenderSize();

            mVideoSizeInitialized = true;
            trySeekToStartPosition();
        }
    };

    private void trySeekToStartPosition() {
        if (mVideoInfo == null) {
            return;
        }

        int seekToPosition = mVideoInfo.getCurrentPosition();
        if (mMediaPlayer != null && mMediaPlayerIsPrepared && mVideoSizeInitialized && seekToPosition > 0) {
            mMediaPlayer.seekTo(seekToPosition);
        }
    }
    private void clearLoadingFailed() {
        mMainHandler.removeCallbacks(mLoadingFailedRunnable);
    }
}
