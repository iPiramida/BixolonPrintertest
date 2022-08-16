package si.ipiramida.bixolonprintertest;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Set;

public class DialogManager {
    @SuppressLint("MissingPermission")
    static void showBluetoothDialog(Context context, final Set<BluetoothDevice> pairedDevices) {
        final String[] items = new String[pairedDevices.size()];
        int index = 0;
        for (BluetoothDevice device : pairedDevices) {
            items[index++] = device.getName() + "\n" + device.getAddress();
        }

        new AlertDialog.Builder(context).setTitle("Paired Bluetooth printers")
                .setItems(items, new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.S)
                    public void onClick(DialogInterface dialog, int which) {

                        String strSelectList = items[which];
                        String temp;
                        int indexSpace = 0;
                        for(int i = 5; i<strSelectList.length(); i++){
                            temp = strSelectList.substring(i-5, i);
                            if((temp.equals("00:10"))||(temp.equals("74:F0"))||(temp.equals("00:15")) || (temp.equals("DD:C5")) || (temp.equals("40:19"))){
                                indexSpace = i;
                                i = 100;
                            }
                        }
                        String strDeviceInfo = null;
                        strDeviceInfo = strSelectList.substring(indexSpace-5, strSelectList.length());

                        MainActivity.mBixolonLabelPrinter.connect(strDeviceInfo);

                    }
                }).show();
    }
}
