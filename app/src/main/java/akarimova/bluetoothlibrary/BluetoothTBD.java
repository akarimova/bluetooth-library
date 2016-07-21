package akarimova.bluetoothlibrary;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by akarimova on 21.07.16.
 */
public class BluetoothTBD extends BroadcastReceiver {
    @NonNull
    private Context context;

    @Nullable
    private AudioManager audioManager;

    @Nullable
    private BluetoothAdapter bluetoothAdapter;

    @Nullable
    private BluetoothHeadset headsetProxy;

    public BluetoothTBD(@NonNull Context context) {
        this.context = context;
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private boolean checkBluetoothHeadsetProxy() {

        return audioManager != null && bluetoothAdapter != null && bluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                headsetProxy = (BluetoothHeadset) proxy;
                List<BluetoothDevice> devices = headsetProxy.getConnectedDevices();
                if (devices.size() > 0) {
                    if (!audioManager.isBluetoothScoOn()) {
                        audioManager.startBluetoothSco();
                    }
                }
                context.registerReceiver(BluetoothTBD.this,
                        new IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED));

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                context.registerReceiver(BluetoothTBD.this, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED));
//                } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                    context.registerReceiver(receiver, new IntentFilter(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));
//                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                if (headsetProxy != null) {
                    bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, headsetProxy);
                    headsetProxy = null;
                }
            }

        }, BluetoothProfile.HEADSET);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
