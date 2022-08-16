package si.ipiramida.bixolonprintertest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bixolon.labelprinter.BixolonLabelPrinter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    ActivityResultLauncher<String[]> permissionResultLauncher;
    private boolean isBTPermissionsGranted = false;
    private boolean isBTAdminPermissionGranted = false;
    private boolean isBTScanPermissionGranted = false;
    private boolean isBTAdvertisePermissionGratned = false;
    private boolean isBTConnectPermissionsGranted = false;
    private boolean isAccessFineLocationGranted = false;
    private boolean isAccessCoarseLocationGranted = false;

    private TextView statusTiskalnika = null;
    private Button bTiskanje = null;
    private EditText vnosnoOkno = null;

    static BixolonLabelPrinter mBixolonLabelPrinter;

    private String mConnectedDeviceName = null;
    private boolean mIsConnected;

    private static final String CHARACTER_ENCODING_CP1250 = "CP1250";
    private static final int PRINTER_WIDTH_BIXOLON_SPP_L310 = 575;
    private static final char LF = '\r';
    private static final char CR = '\n';
    private static final String LFCR = "\r\n";
    private static final String _newline = "\r";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBixolonLabelPrinter = new BixolonLabelPrinter(this, mHandler, Looper.getMainLooper());

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> result) {
                if (result.get(Manifest.permission.BLUETOOTH) != null) {
                    isBTPermissionsGranted = result.get(Manifest.permission.BLUETOOTH);
                }
                if (result.get(Manifest.permission.BLUETOOTH_ADMIN) != null) {
                    isBTAdminPermissionGranted = result.get(Manifest.permission.BLUETOOTH_ADMIN);
                }
                if (result.get(Manifest.permission.BLUETOOTH_ADVERTISE) != null) {
                    isBTAdvertisePermissionGratned = result.get(Manifest.permission.BLUETOOTH_ADVERTISE);
                }
                if (result.get(Manifest.permission.BLUETOOTH_CONNECT) != null) {
                    isBTConnectPermissionsGranted = result.get(Manifest.permission.BLUETOOTH_CONNECT);
                }
                if (result.get(Manifest.permission.BLUETOOTH_SCAN) != null) {
                    isBTScanPermissionGranted = result.get(Manifest.permission.BLUETOOTH_SCAN);
                }
                if (result.get(Manifest.permission.ACCESS_FINE_LOCATION) != null) {
                    isAccessFineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (result.get(Manifest.permission.ACCESS_COARSE_LOCATION) != null) {
                    isAccessCoarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                }
            }
        });

        requestPermissions();

        statusTiskalnika = findViewById(R.id.statusTiskalnika);
        vnosnoOkno = findViewById(R.id.vnosnoOkno);

        bTiskanje = findViewById(R.id.bTiskanje);
        bTiskanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeDirectIO();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.item1:
                mBixolonLabelPrinter.findBluetoothPrinters();
                break;
        }

        return false;
    }

   private void requestPermissions() {
        isBTPermissionsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED;
        isBTAdminPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED;
        isBTScanPermissionGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
        isBTAdvertisePermissionGratned = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED;
        isBTConnectPermissionsGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        isAccessFineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        isAccessCoarseLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

       List<String> permissionRequest = new ArrayList<>();

       if (!isBTPermissionsGranted) {
           permissionRequest.add(Manifest.permission.BLUETOOTH);
       }

       if (!isBTAdminPermissionGranted) {
           permissionRequest.add(Manifest.permission.BLUETOOTH_ADMIN);
       }

       if (!isBTScanPermissionGranted) {
           permissionRequest.add(Manifest.permission.BLUETOOTH_SCAN);
       }

       if (!isBTAdvertisePermissionGratned) {
           permissionRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE);
       }

       if (!isBTConnectPermissionsGranted) {
           permissionRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
       }

       if (!isAccessFineLocationGranted) {
           permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
       }

       if (!isAccessCoarseLocationGranted) {
           permissionRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
       }

       if (!permissionRequest.isEmpty()) {
           permissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
       }
   }

    private final void setStatus(CharSequence subtitle)
    {
        /*final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(subtitle);*/
        statusTiskalnika.setText(subtitle);
    }

    private final void setStatus(int resId)
    {
        /*final ActionBar actionBar = getActionBar();
        actionBar.setSubtitle(resId);*/
        statusTiskalnika.setText(resId);
    }


    @SuppressLint("HandlerLeak")
    private void dispatchMessage(Message msg)
    {
        switch (msg.arg1)
        {
            case BixolonLabelPrinter.PROCESS_GET_STATUS:
                byte[] report = (byte[]) msg.obj;
                StringBuffer buffer = new StringBuffer();
                if((report[0] & BixolonLabelPrinter.STATUS_1ST_BYTE_PAPER_EMPTY) == BixolonLabelPrinter.STATUS_1ST_BYTE_PAPER_EMPTY)
                {
                    buffer.append("Paper Empty.\n");
                }
                if((report[0] & BixolonLabelPrinter.STATUS_1ST_BYTE_COVER_OPEN) == BixolonLabelPrinter.STATUS_1ST_BYTE_COVER_OPEN)
                {
                    buffer.append("Cover open.\n");
                }
                if((report[0] & BixolonLabelPrinter.STATUS_1ST_BYTE_CUTTER_JAMMED) == BixolonLabelPrinter.STATUS_1ST_BYTE_CUTTER_JAMMED)
                {
                    buffer.append("Cutter jammed.\n");
                }
                if((report[0] & BixolonLabelPrinter.STATUS_1ST_BYTE_TPH_OVERHEAT) == BixolonLabelPrinter.STATUS_1ST_BYTE_TPH_OVERHEAT)
                {
                    buffer.append("TPH(thermal head) overheat.\n");
                }
                if((report[0] & BixolonLabelPrinter.STATUS_1ST_BYTE_AUTO_SENSING_FAILURE) == BixolonLabelPrinter.STATUS_1ST_BYTE_AUTO_SENSING_FAILURE)
                {
                    buffer.append("Gap detection error. (Auto-sensing failure)\n");
                }
                if((report[0] & BixolonLabelPrinter.STATUS_1ST_BYTE_RIBBON_END_ERROR) == BixolonLabelPrinter.STATUS_1ST_BYTE_RIBBON_END_ERROR)
                {
                    buffer.append("Ribbon end error.\n");
                }

                if(report.length == 2)
                {
                    if((report[1] & BixolonLabelPrinter.STATUS_2ND_BYTE_BUILDING_IN_IMAGE_BUFFER) == BixolonLabelPrinter.STATUS_2ND_BYTE_BUILDING_IN_IMAGE_BUFFER)
                    {
                        buffer.append("On building label to be printed in image buffer.\n");
                    }
                    if((report[1] & BixolonLabelPrinter.STATUS_2ND_BYTE_PRINTING_IN_IMAGE_BUFFER) == BixolonLabelPrinter.STATUS_2ND_BYTE_PRINTING_IN_IMAGE_BUFFER)
                    {
                        buffer.append("On printing label in image buffer.\n");
                    }
                    if((report[1] & BixolonLabelPrinter.STATUS_2ND_BYTE_PAUSED_IN_PEELER_UNIT) == BixolonLabelPrinter.STATUS_2ND_BYTE_PAUSED_IN_PEELER_UNIT)
                    {
                        buffer.append("Issued label is paused in peeler unit.\n");
                    }
                }
                if(buffer.length() == 0)
                {
                    buffer.append("No error");
                }
                Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_SHORT).show();
                break;
            case BixolonLabelPrinter.PROCESS_GET_INFORMATION_MODEL_NAME:
            case BixolonLabelPrinter.PROCESS_GET_INFORMATION_FIRMWARE_VERSION:
            case BixolonLabelPrinter.PROCESS_EXECUTE_DIRECT_IO:
                Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
            case BixolonLabelPrinter.PROCESS_OUTPUT_COMPLETE:
                Toast.makeText(getApplicationContext(), "Output Complete", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case BixolonLabelPrinter.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case BixolonLabelPrinter.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            mIsConnected = true;
                            invalidateOptionsMenu();
                            break;

                        case BixolonLabelPrinter.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;

                        case BixolonLabelPrinter.STATE_NONE:
                            Log.e("NONE", msg.toString());
                            setStatus(R.string.title_not_connected);
                            //mListView.setEnabled(false);
                            mIsConnected = false;
                            invalidateOptionsMenu();
                            break;
                    }
                    break;

                case BixolonLabelPrinter.MESSAGE_READ:
                    MainActivity.this.dispatchMessage(msg);
                    break;

                case BixolonLabelPrinter.MESSAGE_DEVICE_NAME:
                    mConnectedDeviceName = msg.getData().getString(BixolonLabelPrinter.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), mConnectedDeviceName, Toast.LENGTH_LONG).show();
                    break;

                case BixolonLabelPrinter.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(BixolonLabelPrinter.TOAST), Toast.LENGTH_SHORT).show();
                    break;

                case BixolonLabelPrinter.MESSAGE_LOG:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(BixolonLabelPrinter.LOG), Toast.LENGTH_SHORT).show();
                    break;

                case BixolonLabelPrinter.MESSAGE_BLUETOOTH_DEVICE_SET:
                    if(msg.obj == null)
                    {
                        Toast.makeText(getApplicationContext(), "No paired device", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        DialogManager.showBluetoothDialog(MainActivity.this, (Set<BluetoothDevice>) msg.obj);
                    }
                    break;

            }
        }
    };

    private void executeDirectIO() {
        String command = vnosnoOkno.getText().toString();

        if (command.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please input command", Toast.LENGTH_SHORT).show();
            return;
        }

        mBixolonLabelPrinter.executeDirectIo(command, false, 0);
    }


}
