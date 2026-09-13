package com.test.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 手表控制端完整 MainActivity（Android 7.1.1）
 * 输入被控端 IP，点击按钮发送 ALARM_ON / ALARM_OFF UDP 指令
 */
public class MainActivity extends Activity {

    EditText editIp;
    Button btnStart, btnStop;
    final int PORT = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editIp = findViewById(R.id.et_ip);
        btnStart = findViewById(R.id.btn_on);
        btnStop = findViewById(R.id.btn_off);

        btnStart.setOnClickListener(v -> sendUdp("ALARM_ON"));
        btnStop.setOnClickListener(v -> sendUdp("ALARM_OFF"));
    }

    private void sendUdp(String msg) {
        new Thread(() -> {
            try {
                String ipStr = editIp.getText().toString().trim();
                InetAddress ip = InetAddress.getByName(ipStr);
                DatagramSocket socket = new DatagramSocket();
                byte[] data = msg.getBytes();
                DatagramPacket pkt = new DatagramPacket(data, data.length, ip, PORT);
                socket.send(pkt);
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
