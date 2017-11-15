package com.vivo.hdp.sleeptest;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.vivo.hdp.sleep.ICommand;

/**
 * Created by 10957084 on 2017/11/9.
 */

public class AidlUtils  {

    private static ICommand iCommand;

    public  AidlUtils(){
    }

    public static ICommand waitForAidl(Context context) {
        Intent userIntent = new Intent();
//        userIntent.setAction("com.vivo.hdp.sleep.ICommand");
        userIntent.setClassName("com.vivo.hdp.sleep","com.vivo.hdp.sleep.MyService");
//        userIntent.setPackage("com.vivo.hdp.sleep");
        context.bindService(userIntent, conn, Service.BIND_AUTO_CREATE);
        Log.e("hey","start bind");
        while (iCommand == null) {
            SystemClock.sleep(100);
        }
        Log.e("hey","bind succeed!!");
        return iCommand;
    }

    private static ServiceConnection conn = new ServiceConnection() {
        @Override
        synchronized public void onServiceConnected(ComponentName name, IBinder service) {
            iCommand = ICommand.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iCommand = null;
        }
    };
}
