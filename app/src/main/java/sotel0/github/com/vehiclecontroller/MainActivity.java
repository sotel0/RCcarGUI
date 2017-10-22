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
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    // variables to hold widgets
    SeekBar moveBar;
    SeekBar turnBar;
    TextView moveText;
    TextView turnText;
    Button btnPairing;

    // for bluetooth configuration
    private BluetoothAdapter bluetoothDevice = null;
    private String address = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private ProgressDialog progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize bluetooth setup
        setupBluetooth();

        //instantiate the widgets and variables
        initializeVariable();

        //create all the onclick listeners
        setupClickListeners();

        //set the default value of seekBars to midpoint
        moveBar.setProgress(moveBar.getMax()/2);
        turnBar.setProgress(turnBar.getMax()/2);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("result code");
        System.out.println(resultCode);
        System.out.println("request code");
        System.out.println(requestCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                address = data.getStringExtra("ADDRESS_VALUE");

                new ConnectBT().execute();
            }
            if(resultCode == 2){
                if(btSocket!= null && btSocket.isConnected()){
                    try {
                        btSocket.close();
                        isBtConnected = false;
                        msg("Disconnected",0);
                    } catch (IOException e){
                        msg("Disconnection Fail", 0);
                    }
                } else {
                    msg("No Connection", 0);
                }
            }

        }
    }

    private void setupBluetooth(){
        bluetoothDevice = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothDevice == null){
            //Show message that the device has no bluetooth adapter
            msg("Bluetooth Device Not Available",1);
        }
        else{
            if (!bluetoothDevice.isEnabled()) {
                int REQUEST_ENABLE_BT = 64;
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

            }

        }
    }

    private void initializeVariable() {
        moveBar = (SeekBar) findViewById(R.id.moveBar);
        turnBar = (SeekBar) findViewById(R.id.turnBar);
        moveText = (TextView) findViewById(R.id.moveText);
        turnText = (TextView) findViewById(R.id.turnText);
        btnPairing = (Button) findViewById(R.id.bluetoothBtn);

    }

    private void startBTActivity(){

        if(bluetoothDevice == null){
            //Show message that the device has no bluetooth adapter
            msg("Bluetooth Device Not Available",1);
        }
        else if(bluetoothDevice.isEnabled()){
            Intent intent = new Intent(this, BlueToothActivity.class);
            startActivityForResult(intent,1);
        }
    }

    private void setupClickListeners(){
        //deviceList.setOnItemClickListener();

        btnPairing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                    startBTActivity();

            }
        });

        moveBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //calculate the middle value
            int midpoint = moveBar.getMax()/2;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // the value being sent to the arduino
                int barValue = i-midpoint;

                //make the range display from negative to 0 to positive values
                moveText.setText(String.valueOf(barValue));

                //prepare value to be sent
                if(barValue < 0){
                    barValue = Math.abs(barValue);
                    //distinguish reverse direction
                    barValue = barValue + 1000;
                }else {
                    barValue = Math.abs(barValue);
                }

                System.out.println(barValue + " move");


                if (btSocket!=null) //check for connection
                {
                    try {
                        //send the value as bytes
                        byte[] output = {0,0};
                        output[0] = (byte) (barValue & 0xFF);
                        output[1] = (byte) ((barValue >> 8) & 0xFF);
                        //byte[] output = ByteBuffer.allocate(2).putInt(barValue).array();
                        //byte[] output = BigInteger.valueOf(barValue).toByteArray();
                        //byte[] output = Integer.toString(barValue).getBytes();

//                        for (byte item: output) {
//                            btSocket.getOutputStream().write(item);
//                        }

                        btSocket.getOutputStream().write(output);
                    }catch (IOException e){
                        msg("no outputStream to write",0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(midpoint);
            }
        });

        turnBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //calculate the middle value
            int midpoint = turnBar.getMax()/2;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //if you need to reduce the amount of degrees from 90 that the servo can turn:
                // 1) degree = 90 - degree
                // 2) change the value to barValue below: i + degree
                // 3) change the max value in the seekbar: 180-(degree*2)
                int barValue = i + 30;

                //make the range display from negative to 0 to positive values
                turnText.setText(String.valueOf(barValue));

                //prepare value to be sent
                barValue = barValue + 2000;

                System.out.println(barValue + " turn");

                if (btSocket!=null) //check for connection
                {
                    try {

                        //send the value as bytes
                        //byte[] output = Integer.toString(barValue).getBytes();
                        byte[] output = {0,0};
                        output[0] = (byte) (barValue & 0xFF);
                        output[1] = (byte) ((barValue >> 8) & 0xFF);

                        btSocket.getOutputStream().write(output);
                    }catch (IOException e){
                        msg("No outputStream to write",0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(midpoint);
            }
        });
    }

    private void msg(String s, int size) {
        // 0 short message
        // 1 long message
        if (size == 0) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    bluetoothDevice = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice device = bluetoothDevice.getRemoteDevice(address);//connects to the device's address and checks if it's available

                    //btSocket = device.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    btSocket = device.createRfcommSocketToServiceRecord(myUUID);

                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Try again.",1);
            }
            else
            {
                msg("Connected.",1);
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }

}
