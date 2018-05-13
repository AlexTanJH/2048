package lab4_202_07.uwaterloo.ca.lab4_202_07;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.TextView;


//event handler for the acceleration function
class AccelSensorEventListener implements SensorEventListener {
    //other variables
    private GameLoopTask gameLoopTask;
    private TextView lblDirection;
    private Activity activity;
    private GestureFSM xAxis;
    private GestureFSM yAxis;

    //array that holds the maximum values for accelaration
    private double[] maxAccelSensorValue = {0, 0, 0};

    //making the threshold values
    private float[][] CSVValues = new float[100][3];
    public final float[] bxTHRES = {-0.001f, -0.005f, 0.003f}; // left
    public final float[] axTHRES = {0.001f, 0.005f, -0.003f};  //right
    //    public final float[] byTHRES = {-0.0013f, -0.007f, 0.003f}; //back
//    public final float[] ayTHRES = {0.0013f, 0.007f, -0.003f}; // forward
    public final float[] byTHRES = {-0.001f, -0.005f, 0.003f}; //back
    public final float[] ayTHRES = {0.001f, 0.005f, -0.003f}; // forward

    //filter constant that helps the low pass filter
    private static final float FILTERCONSTANT = 2000.0f;

    //don't care abput this, but needed to "implement" this because SensorEventListener is a interface
    public void onAccuracyChanged(Sensor s, int i) {
    }

    //constructor
    AccelSensorEventListener(Activity a, TextView lblDirection, GameLoopTask gameLoopTask) {
        this.activity = a;
        this.gameLoopTask = gameLoopTask;
        this.lblDirection = lblDirection;
        xAxis = new GestureFSM(axTHRES, bxTHRES);
        yAxis = new GestureFSM(ayTHRES, byTHRES);
    }


    //activates when sensor detects movement
    public void onSensorChanged(SensorEvent se) {
        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {                                 //checks to see the right sensor is activated
            float[] accelSensorValue = new float[3];                                                //array thay holds current acceleration values
            for (int x = 0; x < accelSensorValue.length; x++) {

                accelSensorValue[x] += (se.values[x] - accelSensorValue[x]) / FILTERCONSTANT;           //applies the low pass filter
                if (Math.abs(accelSensorValue[x]) > Math.abs(maxAccelSensorValue[x])) {              //compares the magnitude of the acceration to find max acceleration
                    maxAccelSensorValue[x] = accelSensorValue[x];
                }
            }

            //removes last element and puts in new value up front
            System.arraycopy(CSVValues, 0, CSVValues, 1, CSVValues.length - 1);                          //

            //holds a 100 of the historical values
            CSVValues[0] = accelSensorValue;

            //feeds in the values to the FSM
            xAxis.FSM(accelSensorValue[0]);
            yAxis.FSM(accelSensorValue[1]);

            //holds the current type of X and Y axis: A, B, X, UNKNOWN
            String Xstates = xAxis.getStateStr();
            String Ystates = yAxis.getStateStr();

            //Decides on the state between the X and Y axis
            if (xAxis.getState()== GestureFSM.STATES.DETERMINED || yAxis.getState() == GestureFSM.STATES.DETERMINED) {
                if (Ystates.equals("X")) {
                    if (Xstates.equals("A")) {
                        lblDirection.setText("Right");
                        gameLoopTask.setDirection(GameLoopTask.GameDirection.RIGHT);
                    } else if (Xstates.equals("B")) {
                        lblDirection.setText("Left");
                        gameLoopTask.setDirection(GameLoopTask.GameDirection.LEFT);
                    } else {
                        //lblDirection.setText("Unknown");
                        //gameLoopTask.setDirection(GameLoopTask.GameDirection.UNKNOWN);
                    }
                } else if (Xstates.equals("X")) {
                    if (Ystates.equals("A")) {
                        lblDirection.setText("Up");
                        gameLoopTask.setDirection(GameLoopTask.GameDirection.UP);
                    } else if (Ystates.equals("B")) {
                        lblDirection.setText("Down");
                        gameLoopTask.setDirection(GameLoopTask.GameDirection.DOWN);
                    } else {
                        //lblDirection.setText("Unknown");
                        //gameLoopTask.setDirection(GameLoopTask.GameDirection.UNKNOWN);
                    }
                } else {
                    lblDirection.setText("Unknown");
                    //gameLoopTask.setDirection(GameLoopTask.GameDirection.UNKNOWN);
                }
            }

        }
    }

    public float[][] getCSVValues() {
        return this.CSVValues;
    }
}
