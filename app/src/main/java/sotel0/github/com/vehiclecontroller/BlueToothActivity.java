package sotel0.github.com.vehiclecontroller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class BlueToothActivity extends AppCompatActivity {

    ListView deviceList;
    BluetoothAdapter myBluetooth = null;
    private AdapterView.OnItemClickListener myListClickListener;
    private String address = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);

        initializeVariables();

        findPairedDevices();
    }

    private void initializeVariables(){
        deviceList = (ListView) findViewById(R.id.deviceList);

        //on click listener to get the address of device
        myListClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
                // Get the device MAC address, the last 17 chars in the View
                String info = ((TextView) v).getText().toString();
                address = info.substring(info.length() - 17);

                //make intent to send address back to main activity
                Intent intent = new Intent();
                intent.putExtra("ADDRESS_VALUE", address);
                setResult(RESULT_OK, intent);
                finish();

            }
        };
    }

    private void findPairedDevices(){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = myBluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                list.add(device.getName() + "\n" + device.getAddress()); //Get the device's name and the address
            }
        }else {
            msg("No Paired Bluetooth Devices Found.");
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        if (!adapter.isEmpty()){
            deviceList.setAdapter(adapter);
        }


        deviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

}
