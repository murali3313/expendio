package com.thriwin.expendio;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static android.net.wifi.p2p.WifiP2pDevice.CONNECTED;
import static com.thriwin.expendio.Utils.isNull;

interface MessageConstants {
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_SUCCESS = 2;
    public static final int CONNECTED = 3;
    public static final int MESSAGE_ERROR = 4;
    public static final int MESSAGE_CONNECT_ERROR = 5;
}