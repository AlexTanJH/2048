package lab4_202_07.uwaterloo.ca.lab4_202_07;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

public class lab4_202_07 extends AppCompatActivity {
    public final int GAMEBOARDSIZE = 1200;

    private Timer timer;
    private GameLoopTask gameLoopTask;

    private RelativeLayout layout;
    public TextView lblDirection;

    private SensorManager sensorManager;

    private AccelSensorEventListener a;

    private Sensor accelSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_202_07);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.LinLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        layout = (RelativeLayout) findViewById(R.id.ReLayout);   // setting up the linear layout

        layout.getLayoutParams().width = GAMEBOARDSIZE;
        layout.getLayoutParams().height = GAMEBOARDSIZE;
        layout.setBackgroundResource(R.drawable.gameboard);

        lblDirection = new TextView(getApplicationContext());
        lblDirection.setTextColor(Color.BLACK);
        lblDirection.setTextSize(30);
        linearLayout.addView(lblDirection);
        lblDirection.setText("Hello");

        gameLoopTask = new GameLoopTask(this, getApplicationContext(), layout);
        timer = new Timer();
        timer.schedule(gameLoopTask,16,16);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);   // instantiating SensorManager object

        // setting the sensor type for each sensor
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // instantiating event handlers
        a = new AccelSensorEventListener(this, lblDirection,gameLoopTask);

        // registering event handlers
        sensorManager.registerListener(a, accelSensor, sensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(a);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(a, accelSensor, sensorManager.SENSOR_DELAY_GAME);
    }
    public void stopSensor(){
        sensorManager.unregisterListener(a);
    }
    public void resumeSensor(){
        sensorManager.registerListener(a, accelSensor, sensorManager.SENSOR_DELAY_GAME);
    }

}
