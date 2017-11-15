package com.vivo.hdp.sleeptest;

import android.app.Instrumentation;
import android.app.KeyguardManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.util.Log;

import com.vivo.hdp.sleep.ICommand;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    Context appContext = InstrumentationRegistry.getTargetContext();
    Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    UiDevice uiDevice = UiDevice.getInstance(instrumentation);
    private PowerManager.WakeLock wakeLock = null;
    ArrayList<String> pkgList = new ArrayList<>();
    StringBuilder command = new StringBuilder();
    StringBuilder sb = new StringBuilder();
    private ICommand iCommand;
    int brightTime = 0;
    int blackTime = 0;


    @Before
    public void before(){
        Log.e("hey",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");

        iCommand = AidlUtils.waitForAidl(appContext);
        String packageName = "";
        Uri uri = Uri.parse("content://com.vivo.hdp.provider");
        Cursor cursor = appContext.getContentResolver().query(uri, new String[]{"packageName","brightTime", "blackTime"}, null, null, null);

        //丛数据库读取勾选的应用
        if (cursor != null){

            if(cursor.moveToFirst()){
                brightTime = cursor.getInt(cursor.getColumnIndex("brightTime"));
                blackTime = cursor.getInt(cursor.getColumnIndex("blackTime"));
                Log.e(TAG,"  "+brightTime+"    "+blackTime);
            }
            while (cursor.moveToNext()){
                packageName = cursor.getString(cursor.getColumnIndex("packageName"));
                if (packageName != null){
                    Log.e(TAG, "hhhhh1: "+packageName);
                    pkgList.add(packageName);
                }
            }
            cursor.close();
        }
        Log.e(TAG, "testNoPasswardCase: "+brightTime+"rrrrr"+blackTime);
    }


    @Test
    public void testNoPasswardCase() throws Exception {
        uiDevice.sleep();
        SystemClock.sleep(blackTime*1000);
    }


    @Test
    public void brightTest() throws UiObjectNotFoundException, RemoteException {
        Log.e(TAG, "testNoPasswardCasetestNoPasswardCase: 3333");
        uiDevice.wakeUp();
        unlockScreen();
        if (!pkgList.isEmpty()) {
            for (String pkg : pkgList) {
                sb.append(" -p ").append(pkg);
            }
            Log.e(TAG, "testNoPasswardCasetestNoPasswardCase: enen" + sb);
            command.append("monkey").append(sb + " ").append(" --throttle 1000 ").append(Integer.MAX_VALUE - 1).
                    append(" --ignore-timeouts --ignore-security-exceptions --ignore-crashes --ignore-native-crashes").append(" --pct-anyevent 0 --pct-syskeys 20  --pct-touch 40 --pct-motion 40 --pct-trackball 0 --pct-nav 0 --pct-majornav 00 --pct-appswitch 0");

            Log.e(TAG, "testNoPasswardCasetestNoPasswardCase: " + command);

            iCommand.exces(command.toString());
            Log.e(TAG, "testNoPasswardCasetestNoPasswardCase: sleep");
        }

    }

    @Test
    public void unLockScreen() throws RemoteException, UiObjectNotFoundException {
        uiDevice.wakeUp();
        unlockScreen();
    }

    public  boolean isScreenLocked() {
        android.app.KeyguardManager mKeyguardManager = (KeyguardManager) appContext.getSystemService(appContext.KEYGUARD_SERVICE);
        return !mKeyguardManager.inKeyguardRestrictedInputMode();
    }


    private void unlockScreen() throws UiObjectNotFoundException {
        uiDevice.swipe(uiDevice.getDisplayWidth()/2,uiDevice.getDisplayHeight()*99/100,
                uiDevice.getDisplayWidth()/2,uiDevice.getDisplayHeight()/5,10);

        String numberId = "com.android.systemui:id/vivo_digit_text";
        String patterrId = "com.android.systemui:id/vivo_lock_pattern_view";
        UiObject object = new UiObject(new UiSelector().resourceId(numberId));
        UiObject object3 = new UiObject(new UiSelector().resourceId("com.android.systemui:id/VivoPinkey1"));
        UiObject object1 = new UiObject(new UiSelector().resourceId(patterrId));
        UiObject object2 = new UiObject(new UiSelector().resourceId("com.android.systemui:id/unlockPatternView"));
        SystemClock.sleep(1000);
        if (object.exists()||object3.exists()){
            if (object3.exists()){
                numberId = "com.android.systemui:id/VivoPinkey1";
            }
            inputScreenPassword(numberId);
        }else if (object1.exists() || object2.exists()) {
            if (object2.exists()) {
                patterrId = "com.android.systemui:id/unlockPatternView";
                Log.e(TAG, "unlockScreen: 555555555555555555555555555555");
            }
            drawPatternLock(patterrId);
        }
    }


    public void inputScreenPassword(String id) {
        UiObject2 object2 = uiDevice.findObject(By.res(id));

        for (int i = 0; i < 6; i++) {
            uiDevice.click(object2.getVisibleCenter().x, object2.getVisibleCenter().y);
        }
    }

    public void drawPatternLock(String id){
        UiObject2 object2 = uiDevice.findObject(By.res(id));
        Point point1 = new Point(object2.getVisibleBounds().left,object2.getVisibleBounds().height()/6+object2.getVisibleBounds().top);
        Point point2 = new Point(object2.getVisibleBounds().width()*5/6+object2.getVisibleBounds().left,object2.getVisibleBounds().height()/6+object2.getVisibleBounds().top);
        Point point3 = new Point(object2.getVisibleBounds().width()*5/6+object2.getVisibleBounds().left,object2.getVisibleBounds().bottom);
        Point[] swipe = {point1,point2,point3};
        uiDevice.swipe(swipe,40);
    }



    public void wakeUp(){
        // 键盘管理器
        KeyguardManager mKeyguardManager;
        // 键盘锁
        KeyguardManager.KeyguardLock mKeyguardLock;
        // 电源管理器
        PowerManager mPowerManager;
        // 唤醒锁
        PowerManager.WakeLock mWakeLock;
        mPowerManager = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "Tag");
        mWakeLock.acquire();
        mWakeLock.release();
        Log.e(TAG, "wakeUp: **********************");
    }

    private void acquireWakeLock1() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager)appContext.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
            if (null != wakeLock) {
                Log.i(TAG, "call acquireWakeLock");
                wakeLock.acquire();
            }
        }
    }

    private void releaseWakeLock1() {
        if (null != wakeLock && wakeLock.isHeld()) {
            Log.i(TAG, "call releaseWakeLock");
            wakeLock.release();
            wakeLock = null;
        }
    }


}
