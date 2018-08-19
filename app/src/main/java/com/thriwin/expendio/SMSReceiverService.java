package com.thriwin.expendio;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.thriwin.expendio.SMSStateReceiver;

public class SMSReceiverService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        registerSMSStateReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void registerSMSStateReceiver() {

        SMSStateReceiver smsStateReceiver = new SMSStateReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsStateReceiver, filter);
    }
}