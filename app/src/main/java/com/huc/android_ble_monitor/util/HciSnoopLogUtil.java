package com.huc.android_ble_monitor.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.huc.android_ble_monitor.activities.ServicesOverviewActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class HciSnoopLogUtil implements IHciDecoder {
    private String mRawSnoopLog;
    private String mLogFilepath;
    private String TAG = this.getClass().getSimpleName();
    private String BTSTACK_CONFIG_FILE = "bt_stack.conf";
    private String BTSTACK_CONFIG_PATH = "/etc/bluetooth/" + BTSTACK_CONFIG_FILE;
    private String BTSNOOP_FALLBACK_FILE = "btsnoop_hci.log";
    private String BTSNOOP_FALLBACK_PATH = "/sdcard/" + BTSNOOP_FALLBACK_FILE;
    private IPacketReceptionCallback mCallback;
    private int mReadLastPacketCount = 1000;
    private int mHciSnoopLogPacketCount;

    static {
        System.loadLibrary("hciviewer");
    }

    public HciSnoopLogUtil(IPacketReceptionCallback cb, Context ctx){
        setPacketReceptionCb(cb);
        String filePath = getSnoopLogLocation();

        // Check if file exists
        if(!new File(filePath).exists()){
            new MaterialAlertDialogBuilder(ctx)
                    .setTitle("Error")
                    .setMessage("Could not find Bluetooth Snoop log file. Make sure to Activate the Option in the Android Developer Settings.")
                    .setNeutralButton("Ok", null)
                    .show();
        }else {
            startHciLogStream(filePath, mReadLastPacketCount);
        }
    }

    @Override
    public native void startHciLogStream(String filePath, int lastPacketCount);

    @Override
    public native void stopHciLogStream();

    /**
     * Returns the HCI Snoop log location from bt_stack.conf file
     * @return log filepath
     */
    @Nullable
    public String getSnoopLogLocation(){
        // check if conf file exists
        File file = new File(this.BTSTACK_CONFIG_PATH);
        if(!file.exists()) return BTSNOOP_FALLBACK_PATH;

        // read in conf file
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return BTSNOOP_FALLBACK_PATH;
        }
        mLogFilepath = prop.getProperty("btsnoopfilename");
        return mLogFilepath;
    }

    @Override
    public void setPacketReceptionCb(IPacketReceptionCallback cb) {
        mCallback = cb;
    }

    /**
     * callback called from native function when a HCI pakcet has been decoded
     *
     * @param snoopFrame snoop frame part
     * @param hciFrame   HCI packet part
     */
    public void onHciFrameReceived(final String snoopFrame, final String hciFrame) {
        if (mCallback != null) {
            mCallback.onHciFrameReceived(snoopFrame, hciFrame);
        }
    }

    public void onError(int errorCode, String errorMessage) {
        if (mCallback != null) {
            mCallback.onError(errorCode, errorMessage);
        }
    }

    /**
     * callback called from native function when packet count is finished
     *
     * @param packetCount total number of HCI packet available
     */
    public void onFinishedPacketCount(int packetCount) {
        mHciSnoopLogPacketCount = packetCount;
        if (mCallback != null) {
            mCallback.onFinishedPacketCount(packetCount);
        }
    }
}
