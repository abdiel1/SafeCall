package fall_detection;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseMatrix;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class FallDetectionManager implements FallDetector, SensorEventListener{

    private List<FallDetectionListener> fallDetectionListeners = new ArrayList<FallDetectionListener>();

    private float[] previousValues = new float[3];

    private long previous_time;

    //private final double[][] FALL_INVERSE_COVARIANCE_VALUES = {{382.3,-258.2,15.8},{-258.2,756.1,67.9},{15.8,67.9,578.3}};

    private final Real[][] FALL_INVERSE_COVARIANCE_VALUES = {{Real.valueOf(382.3), Real.valueOf(-258.2),Real.valueOf(15.8)},
            {Real.valueOf(-258.2),Real.valueOf(756.1),Real.valueOf(67.9)},{Real.valueOf(15.8),Real.valueOf(67.9),Real.valueOf(578.3)}};

    private final DenseMatrix<Real> FALL_INVERSE_COVARIANCE_MATRIX = DenseMatrix.valueOf(FALL_INVERSE_COVARIANCE_VALUES);

    //private final RealMatrix FALL_INVERSE_COVARIANCE_MATRIX = new Array2DRowRealMatrix(FALL_INVERSE_COVARIANCE_VALUES);

//    private final double[][] NON_FALL_INVERSE_COVARIANCE_VALUES = {{0.0132,-0.0036,-0.0047},{-0.0036,0.0054,-0.0001},
//            {-0.0047,-0.0001,0.0108}};

    private final Real[][] NON_FALL_INVERSE_COVARIANCE_VALUES = {{Real.valueOf(0.0132), Real.valueOf(-0.0036),Real.valueOf(-0.0047)},
            {Real.valueOf(-0.0036),Real.valueOf(0.0054),Real.valueOf(-0.0001)},{Real.valueOf(-0.0047),Real.valueOf(-0.0001),Real.valueOf(0.0108)}};

    private final DenseMatrix<Real> NON_FALL_INVERSE_COVARIANCE_MATRIX = DenseMatrix.valueOf(NON_FALL_INVERSE_COVARIANCE_VALUES);

   // private final RealMatrix NON_FALL_INVERSE_COVARIANCE_MATRIX = new Array2DRowRealMatrix(NON_FALL_INVERSE_COVARIANCE_VALUES);

    //private final double[] FALL_MEANS = {78.5251,52.283,61.5768};

    private final Real[] FALL_MEANS = {Real.valueOf(78.5251),Real.valueOf(52.283),Real.valueOf(61.5768)};

    //private final RealMatrix FALL_MEANS_MATRIX = new Array2DRowRealMatrix(FALL_MEANS);

    private final double[] NON_FALL_MEANS = {10.0271,11.8262,10.1568};

    private final RealMatrix NON_FALL_MEANS_MATRIX = new Array2DRowRealMatrix(NON_FALL_MEANS);

    private boolean active;

    private static FallDetectionManager instance;

    private FallDetectionManager(boolean active){
        this.active = active;
    }

    public static FallDetectionManager getInstance(boolean active){
        if(instance == null){
            instance = new FallDetectionManager(active);
        }

        return instance;

    }

    @Override
    public void registerFallDetectionListener(FallDetectionListener fallDetectionListener) {
        if(!fallDetectionListeners.contains(fallDetectionListener)){
            fallDetectionListeners.add(fallDetectionListener);
        }
    }

    @Override
    public boolean removeFallDetectionListener(FallDetectionListener fallDetectionListener) {

        return fallDetectionListeners.remove(fallDetectionListener);
    }

    @Override
    public void notifyFallDetectionListeners() {

        for(FallDetectionListener f : fallDetectionListeners){
            f.onFallDetected();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(active){
            if(previousValues[0] != 0){

                double[] slopes = calculateSlope(event.values);
                if(slopes == null){
                    return;
                }
                double fallMahalanobisDistance = calculateMahalanobisDistance(slopes,FALL_INVERSE_COVARIANCE_MATRIX,
                        FALL_MEANS_MATRIX);
                double nonFallMahalanobisDistance = calculateMahalanobisDistance(slopes,NON_FALL_INVERSE_COVARIANCE_MATRIX,
                        NON_FALL_MEANS_MATRIX);


                //Log.d("Fall",new Double(fallMahalanobisDistance).toString());
                //Log.d("NoFall", new Double(nonFallMahalanobisDistance).toString());
                //Log.d("Fallen",new Boolean(fallMahalanobisDistance < nonFallMahalanobisDistance).toString());

                if(fallMahalanobisDistance < nonFallMahalanobisDistance){
                    Log.d("Fall","Fall was detected");
                    //notifyFallDetectionListeners();
                }
            } else{
                previous_time = System.currentTimeMillis();
                previousValues[0] = event.values[0];
                previousValues[1] = event.values[1];
                previousValues[2] = event.values[2];

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double[] calculateSlope(float[] values){
        double[] slopes = new double[3];

        Real time = Real.valueOf(System.currentTimeMillis());
        Real timeDifference = time.minus(Real.valueOf(previous_time));
        Real multiplyFactor = Real.valueOf(100);

        if(timeDifference.doubleValue() == 0){
            return null;
        }


        Real accelerationDifference = Real.valueOf(values[0]).minus(Real.valueOf(previousValues[0]));
        Real slope = accelerationDifference.divide(timeDifference).times(multiplyFactor);
        slopes[0] = slope.doubleValue();

        accelerationDifference = Real.valueOf(values[1]).minus(Real.valueOf(previousValues[1]));
        slope = accelerationDifference.divide(timeDifference).times(multiplyFactor);
        slopes[1] = slope.doubleValue();


        accelerationDifference = Real.valueOf(values[2]).minus(Real.valueOf(previousValues[2]));
        slope = accelerationDifference.divide(timeDifference).times(multiplyFactor);
        slopes[2] = slope.doubleValue();

        //String s = String.format("%s %s %s",values[0],values[1],values[2]);

        //Log.d("Slope ",accelerationDifference.toString() +" " + timeDifference.toString() + " " + slope.toString());

        previous_time = time.longValue();
        previousValues[0] = values[0];
        previousValues[1] = values[1];
        previousValues[2] = values[2];


        return slopes;
    }

    private double calculateMahalanobisDistance(double[] sample, RealMatrix inverseCovariance, RealMatrix mean){

        RealMatrix sampleMatrix = new Array2DRowRealMatrix(sample);

        RealMatrix temp =  sampleMatrix.subtract(mean);

        RealMatrix temp2 = temp.transpose();

        temp2 = temp2.multiply(inverseCovariance);

        temp2 = temp2.multiply(temp);

        return temp2.getEntry(0,0);

    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean value){
        active = value;
    }


}
