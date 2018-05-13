package lab4_202_07.uwaterloo.ca.lab4_202_07;

/**
 * Created by Alex on 2017-06-30.
 */

// FSM class
public class GestureFSM {
    // the STATE enums
    enum STATES {WAIT, RISE_A, FALL_A, RISE_B, FALL_B, DETERMINED, UNKNOWN}

    private float previousValue;    // the previous reading
    private float THRES_B[] = new float[3];     // Threshold for the horizontal direction
    private float THRES_A[] = new float[3];     // Threshold for the forward and backward direction
    private int counter;                        // counter for resetting
    private String stateStr;                    // the current type of gesture
    private STATES state = STATES.WAIT;         // the current state

    GestureFSM(float[] THRES_A, float[] THRES_B){   // constructor for the class
        previousValue = 0;                          // initialize variable
        this.THRES_A = THRES_A;                     // setting values
        this.THRES_B = THRES_B;
        state = STATES.WAIT;                        // setting state to wait as default
        stateStr = "X";
    }

    public void FSM(float value){
        float delta_A = value - previousValue;      // the change in acceleration
        switch (state){
            case WAIT:                              // wait state
                counter = 30;                       // always set counter to 30 while on standby
                if (delta_A > THRES_A[0]){
                    state = STATES.RISE_A;
                }
                if (delta_A < THRES_B[0]){
                    state = STATES.FALL_B;
                }
                break;
            case RISE_A:
                if (delta_A <=0){                       // negative delta_A or 0 for local max
                    if (previousValue < THRES_A[1]){
                        state = STATES.UNKNOWN;         // unknown state when the previous value is not large enough
                    }
                    if (previousValue >= THRES_A[1]){
                        state = STATES.FALL_A;          // enters fall state when previous value is large enough
                    }
                }
                break;
            case FALL_A:
                if (delta_A >= 0){                      // check for positive delta_A or 0 for local min
                    if (previousValue <= THRES_A[2]){
                        stateStr = "A";                 // A type gesture
                        state = STATES.DETERMINED;      // Determined state
                    }
                    if (previousValue > THRES_A[2]){
                        state = STATES.UNKNOWN;         // otherwise go in unknown
                    }
                }
                break;
            case FALL_B:
                if (delta_A >=0){                       // check for positive delta_A or 0 for local min
                    if (previousValue > THRES_B[1]){
                        state = STATES.UNKNOWN;         // unknown if magnitude of acceleration is not large enough
                    }
                    if (previousValue <= THRES_B[1]){
                        state = STATES.RISE_B;          // go in to rise_b if it is large enough
                    }
                }
                break;
            case RISE_B:
                if (delta_A <= 0){                      // check negative delta_A or 0 for local max
                    if(previousValue >= THRES_B[2]){
                        state = STATES.DETERMINED;      // enter determined state if magnitude is large enough
                        stateStr = "B";
                    }
                    if(previousValue < THRES_B[2]){
                        state = STATES.UNKNOWN;         // unknown if not large enough
                    }
                }
                break;
            case DETERMINED:
                stateStr = "X";
                state = STATES.WAIT;            // return to wait state
                break;
            case UNKNOWN:
                stateStr = "X";                     // the "X" state
                state = STATES.DETERMINED;          // the determined that it is unknown
                break;
            default:
                state = STATES.WAIT;                // go back to wait state for all other cases
                break;
        }
        counter--;                                  // decrement the counter
        previousValue = value;                      // set value as the previous to be used in the next reading.
    }
    public String getStateStr(){
        return this.stateStr;
    }

    public STATES getState(){return  this.state;}


}
