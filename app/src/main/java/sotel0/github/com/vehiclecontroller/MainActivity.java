package sotel0.github.com.vehiclecontroller;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    SeekBar moveBar;
    SeekBar turnBar;
    TextView moveText;
    TextView turnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate the widgets and variables
        initializeVariable();

        //create all the onclick listeners
        setupClickListeners();

        //set the default value to midpoint
        moveBar.setProgress(moveBar.getMax()/2);
        turnBar.setProgress(turnBar.getMax()/2);
    }
    private void initializeVariable(){
        moveBar = (SeekBar) findViewById(R.id.moveBar);
        turnBar = (SeekBar) findViewById(R.id.turnBar);
        moveText = (TextView) findViewById(R.id.moveText);
        turnText = (TextView) findViewById(R.id.turnText);

    }

    private void setupClickListeners(){


        moveBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            //calculate the middle value
            int midpoint = moveBar.getMax()/2;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //make the range display from negative to 0 to positive values
                moveText.setText(String.valueOf(i-midpoint));

                //maybe if change is 0 dont send data to bluetooth
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
                //make the range display from negative to 0 to positive values
                turnText.setText(String.valueOf(i-midpoint));

                //maybe if change is 0 dont send data to bluetooth
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
}
