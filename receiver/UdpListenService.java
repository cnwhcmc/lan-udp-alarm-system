package com.test.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * 被控接收端 UDP 监听前台服务
 * 在 UdpListenService 的 onCreate() 里面增加 WiFi 状态检测，不要 root 相关代码
 * 全部删掉。
 */
public class UdpListenService extends Service {

    private static final int PORT = 8888;
    private static final String CHANNEL_ID = "udp_alarm_channel";

    private DatagramSocket socket;
    private Thread udpThread;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private AudioManager audioManager;
    private int originalVolume;

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 启动 UDP 监听线程
        udpThread = new Thread(() -> {
            try {
                socket = new DatagramSocket(PORT);
                byte[] buf = new byte[128];
                while (!Thread.currentThread().isInterrupted()) {
                    // WiFi 断开则跳转设置
                    if (!isWifiConnected()) {
                        gotoWifiSetting();
                        Thread.sleep(3000);
                        continue;
                    }
                    DatagramPacket pkt = new DatagramPacket(buf, buf.length);
                    socket.receive(pkt);
                    String cmd = new String(pkt.getData(), 0, pkt.getLength());
                    if ("ALARM_ON".equals(cmd)) {
                        runAlarm();
                    } else if ("ALARM_OFF".equals(cmd)) {
                        stopAlarm();
                    }
                }
            } catch (Exception ignored) {
            }
        });
        udpThread.start();
    }

    /**
     * 判断 WiFi 是否可用
     */
    private boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return info != null && info.isConnected();
    }

    /**
     * 跳转 WiFi 设置页面
     */
    private void gotoWifiSetting() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 启动前台服务（必须，否则高版本安卓直接杀死 Service）
     */
    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "UDP 警报监听", NotificationManager.IMPORTANCE_LOW);
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("UDP 警报监听中")
                    .setContentText("设备正在等待远程警报指令")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .build();
        }
        startForeground(1, notification);
    }

    /**
     * 执行警报：最大音量播放警报音 + 持续振动
     */
    private void runAlarm() {
        // 记录原音量并调到最大
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

        // 循环播放警报音
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alert);
            mediaPlayer.setLooping(true);
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        // 持续振动（循环 pattern）
        if (vibrator != null) {
            long[] pattern = {0, 1000, 500}; // 振动1秒，停0.5秒，循环
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createWaveform(pattern, 0));
            } else {
                vibrator.vibrate(pattern, 0);
            }
        }
    }

    /**
     * 停止警报
     */
    private void stopAlarm() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (vibrator != null) {
            vibrator.cancel();
        }
        // 恢复原音量
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (udpThread != null) {
            udpThread.interrupt();
        }
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        stopAlarm();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
