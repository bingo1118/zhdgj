package com.smart.cloud.fire.mvp.playBack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.p2p.core.BasePlayBackActivity;
import com.p2p.core.P2PHandler;
import com.p2p.core.P2PView;
import com.smart.cloud.fire.global.ConstantValues;
import com.smart.cloud.fire.utils.PhoneWatcher;
import com.smart.cloud.fire.yoosee.P2PConnect;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import fire.cloud.smart.com.smartcloudfire.R;

public class PlayBackActivity extends BasePlayBackActivity implements SeekBar.OnSeekBarChangeListener {
    @Bind(R.id.nowTime)
    TextView nowTime;
    @Bind(R.id.seek_bar)
    SeekBar seekbar;
    @Bind(R.id.totalTime)
    TextView totalTime;
    @Bind(R.id.pause)
    ImageView pause;
    @Bind(R.id.close_voice)
    ImageView stopVoice;
    @Bind(R.id.control_bottom)
    RelativeLayout control_bottom;
    private int mCurrentVolume, mMaxVolume;
    private AudioManager mAudioManager = null;
    private boolean isControlShow = true;
    private boolean mIsCloseVoice = false;
    Context mContext;
    boolean isPause = false;
    boolean isRegFilter = false;
    boolean isScroll = false;
    boolean isReject = false;
    PhoneWatcher mPhoneWatcher;
    ArrayList<String> list = new ArrayList<>();
    private int currentFile = 0;
    private int currentFileTemp = currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        P2PConnect.setPlaying(true);
        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_play_back);
        ButterKnife.bind(this);
        mContext = this;
        if (savedInstanceState == null) {
            list = getIntent().getStringArrayListExtra("playbacklist");
            currentFile = getIntent().getIntExtra("currentFile", 0);
        } else {
            list = savedInstanceState.getStringArrayList("playbacklist");
            currentFile = savedInstanceState.getInt("currentFile", 0);
        }
        currentFileTemp = currentFile;
        initComponent();
        regFilter();
        startWatcher();
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        }
        mCurrentVolume = mAudioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        mMaxVolume = mAudioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onHomePressed() {
        // TODO Auto-generated method stub
        super.onHomePressed();
        reject();
    }

    private void upDataCurrentFile() {
        currentFile = currentFileTemp;
    }

    private void startWatcher() {
        mPhoneWatcher = new PhoneWatcher(mContext);
        mPhoneWatcher.setOnCommingCallListener(new PhoneWatcher.OnCommingCallListener() {
            @Override
            public void onCommingCall() {
                // TODO Auto-generated method stub
                reject();
            }
        });
        mPhoneWatcher.startWatcher();
    }

    private void initComponent() {
        pView = (P2PView) findViewById(R.id.pView);
        this.initP2PView(P2PConnect.getCurrentDeviceType());
        seekbar.setOnSeekBarChangeListener(this);
    }

    private void regFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantValues.P2P.P2P_REJECT);
        filter.addAction(ConstantValues.P2P.PLAYBACK_CHANGE_SEEK);
        filter.addAction(ConstantValues.P2P.PLAYBACK_CHANGE_STATE);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        isRegFilter = true;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantValues.P2P.P2P_REJECT)) {
                reject();
            } else if (intent.getAction().equals(
                    ConstantValues.P2P.PLAYBACK_CHANGE_SEEK)) {
                if (!isScroll) {
                    int max = intent.getIntExtra("max", 0);
                    int current = intent.getIntExtra("current", 0);
                    seekbar.setMax(max);
                    seekbar.setProgress(current);
                    nowTime.setText(convertTime(current));
                    totalTime.setText(convertTime(max));
                }
            } else if (intent.getAction().equals(
                    ConstantValues.P2P.PLAYBACK_CHANGE_STATE)) {
                int state = intent.getIntExtra("state", 0);
                switch (state) {
                    case 0:
                        isPause = true;
                        pause.setImageResource(R.drawable.playing_start);
                        break;
                    case 1:
                        isPause = true;
                        pause.setImageResource(R.drawable.playing_start);
                        break;
                    case 2:
                        isPause = false;
                        pause.setImageResource(R.drawable.playing_pause);
                        break;
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                reject();
            }

        }
    };

    public void reject() {
        if (!isReject) {
            isReject = true;
            P2PHandler.getInstance().reject();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        reject();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            mCurrentVolume++;
            if (mCurrentVolume > mMaxVolume) {
                mCurrentVolume = mMaxVolume;
            }

            if (mCurrentVolume != 0) {
                mIsCloseVoice = false;
                stopVoice.setImageResource(R.drawable.btn_playback_voice);
            }
            return false;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            mCurrentVolume--;
            if (mCurrentVolume < 0) {
                mCurrentVolume = 0;
            }

            if (mCurrentVolume == 0) {
                mIsCloseVoice = true;
                stopVoice.setImageResource(R.drawable.btn_playback_voice_s);
            }

            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onDestroy() {
        Log.e("myyy", "onDestroy");
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    mCurrentVolume, 0);
        }
        if (isRegFilter) {
            mContext.unregisterReceiver(mReceiver);
            isRegFilter = false;
        }
        if (null != mPhoneWatcher) {
            mPhoneWatcher.stopWatcher();
        }
        P2PConnect.setPlaying(false);
        super.onDestroy();
    }

    @OnTouch(R.id.control_bottom)
    public boolean onTouch(){
        return true;
    }

    @OnClick({R.id.close_voice_rela,R.id.pause_rela,R.id.next_rela,R.id.previous_rela})
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.close_voice_rela:
                if (mIsCloseVoice) {
                    mIsCloseVoice = false;
                    stopVoice.setImageResource(R.drawable.btn_playback_voice);
                    if (mCurrentVolume == 0) {
                        mCurrentVolume = 1;
                    }
                    if (mAudioManager != null) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                mCurrentVolume, 0);
                    }
                } else {
                    mIsCloseVoice = true;
                    stopVoice.setImageResource(R.drawable.btn_playback_voice_s);
                    if (mAudioManager != null) {
                        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0,
                                0);
                    }
                }
                break;
            case R.id.pause_rela:
                if (isPause) {
                    this.startPlayBack();
                } else {
                    this.pausePlayBack();
                }
                break;
            case R.id.next_rela:
                currentFileTemp++;
                if (null != list && list.size() > 0) {
                    if (currentFileTemp >= list.size()) {
                        Toast.makeText(mContext, "无下一个文件", Toast.LENGTH_SHORT).show();
                        currentFileTemp--;
                    } else {
                        if (this.next(list.get(currentFileTemp))) {
                            upDataCurrentFile();
                        } else {
                            // 播放错误
                            currentFileTemp--;
                        }
                    }
                }
                break;
            case R.id.previous_rela:
                currentFileTemp--;
                if (currentFileTemp <= -1) {
                    Toast.makeText(mContext, "无上一个文件", Toast.LENGTH_SHORT).show();
                    currentFileTemp++;
                } else {
                    if (currentFileTemp < list.size()
                            && this.previous(list.get(currentFileTemp))) {
                        upDataCurrentFile();
                    } else {
                        currentFileTemp++;
                    }
                }
                break;
        }

    }

    public void changeControl() {
        if (isControlShow) {
            isControlShow = false;
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    R.anim.slide_out_top);
            anim2.setDuration(300);
            control_bottom.startAnimation(anim2);
            control_bottom.setVisibility(View.GONE);

        } else {
            isControlShow = true;
            control_bottom.setVisibility(View.VISIBLE);
            Animation anim2 = AnimationUtils.loadAnimation(this,
                    R.anim.slide_in_bottom);
            anim2.setDuration(300);
            control_bottom.startAnimation(anim2);

        }
    }

    @Override
    public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
        // TODO Auto-generated method stub

        Log.e("playback", "onProgressChanged arg1:" + arg1 + " arg2:" + arg2);
        nowTime.setText(convertTime(arg1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        isScroll = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0) {
        // TODO Auto-generated method stub
        this.jump(arg0.getProgress());
        isScroll = false;
    }

    public String convertTime(int time) {
        int hour = time / (60 * 60);
        int minute = time / (60) - hour * 60;
        int second = time - hour * 60 * 60 - minute * 60;

        String hour_str = hour + "";
        String minute_str = minute + "";
        String second_str = second + "";
        if (minute < 10) {
            minute_str = "0" + minute;
        }
        if (second < 10) {
            second_str = "0" + second;
        }

        return hour_str + ":" + minute_str + ":" + second_str;
    }

    @Override
    public int getActivityInfo() {
        // TODO Auto-generated method stub
        return ConstantValues.ActivityInfo.ACTIVITY_PLAYBACKACTIVITY;
    }

    @Override
    protected void onP2PViewSingleTap() {
        // TODO Auto-generated method stub
        changeControl();
    }

    @Override
    protected void onGoBack() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onGoFront() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onExit() {
        // TODO Auto-generated method stub
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                // Toast.makeText(getApplicationContext(),R.string.Press_again_exit,
                // Toast.LENGTH_SHORT).show();
                Toast.makeText(mContext, "再按一次退出播放", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                reject();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

