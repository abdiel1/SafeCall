package fall_detection;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class FallDetectionManager implements FallDetector, SensorEventListener {

    private List<FallDetectionListener> fallDetectionListeners = new ArrayList<FallDetectionListener>();

    private float[] previousValues = new float[3];

    private long previous_time;

    //private final double[][] FALL_INVERSE_COVARIANCE_VALUES = {{382.3,-258.2,15.8},{-258.2,756.1,67.9},{15.8,67.9,578.3}};

    private final Real[][] FALL_INVERSE_COVARIANCE_VALUES = {{Real.valueOf(0.0003823), Real.valueOf(-0.0002582), Real.valueOf(0.0000158)},
            {Real.valueOf(-0.0002582), Real.valueOf(0.0007561), Real.valueOf(0.0000679)}, {Real.valueOf(0.0000158), Real.valueOf(0.0000679), Real.valueOf(0.0005783)}};

    private final DenseMatrix<Real> FALL_INVERSE_COVARIANCE_MATRIX = DenseMatrix.valueOf(FALL_INVERSE_COVARIANCE_VALUES);

    //private final RealMatrix FALL_INVERSE_COVARIANCE_MATRIX = new Array2DRowRealMatrix(FALL_INVERSE_COVARIANCE_VALUES);

//    private final double[][] NON_FALL_INVERSE_COVARIANCE_VALUES = {{0.0132,-0.0036,-0.0047},{-0.0036,0.0054,-0.0001},
//            {-0.0047,-0.0001,0.0108}};

    private final Real[][] NON_FALL_INVERSE_COVARIANCE_VALUES = {{Real.valueOf(0.0132), Real.valueOf(-0.0036), Real.valueOf(-0.0047)},
            {Real.valueOf(-0.0036), Real.valueOf(0.0054), Real.valueOf(-0.0001)}, {Real.valueOf(-0.0047), Real.valueOf(-0.0001), Real.valueOf(0.0108)}};

    private final DenseMatrix<Real> NON_FALL_INVERSE_COVARIANCE_MATRIX = DenseMatrix.valueOf(NON_FALL_INVERSE_COVARIANCE_VALUES);

    // private final RealMatrix NON_FALL_INVERSE_COVARIANCE_MATRIX = new Array2DRowRealMatrix(NON_FALL_INVERSE_COVARIANCE_VALUES);

    //private final double[] FALL_MEANS = {78.5251,52.283,61.5768};

    private final Real[][] FALL_MEANS = {{Real.valueOf(78.5251)}, {Real.valueOf(52.283)}, {Real.valueOf(61.5768)}};

    //private final RealMatrix FALL_MEANS_MATRIX = new Array2DRowRealMatrix(FALL_MEANS);

    private final DenseMatrix<Real> FALL_MEANS_MATRIX = DenseMatrix.valueOf(FALL_MEANS);

    //private final double[] NON_FALL_MEANS = {10.0271,11.8262,10.1568};

    private final Real[][] NON_FALL_MEANS = {{Real.valueOf(10.0271)}, {Real.valueOf(11.8262)}, {Real.valueOf(10.1568)}};

    //private final RealMatrix NON_FALL_MEANS_MATRIX = new Array2DRowRealMatrix(NON_FALL_MEANS);

    private final DenseMatrix<Real> NON_FALL_MEANS_MATRIX = DenseMatrix.valueOf(NON_FALL_MEANS);

    private volatile boolean alarmOn;

    private static FallDetectionManager instance;

    private FallDetectionManager() {
        alarmOn = false;
    }

    public static FallDetectionManager getInstance() {
        if (instance == null) {
            instance = new FallDetectionManager();
        }

        return instance;

    }

    @Override
    public void registerFallDetectionListener(FallDetectionListener fallDetectionListener) {
        if (!fallDetectionListeners.contains(fallDetectionListener)) {
            fallDetectionListeners.add(fallDetectionListener);
        }
    }

    @Override
    public boolean removeFallDetectionListener(FallDetectionListener fallDetectionListener) {

        return fallDetectionListeners.remove(fallDetectionListener);
    }

    @Override
    public void notifyFallDetectionListeners() {

        for (FallDetectionListener f : fallDetectionListeners) {
            f.onFallDetected();
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (previousValues[0] != 0) {

            double[] slopes = calculateSlope(event.values);
            if (slopes == null) {
                return;
            }

            Real[][] sample = {{Real.valueOf(slopes[0])}, {Real.valueOf(slopes[1])}, {Real.valueOf(slopes[2])}};

            DenseMatrix<Real> sampleMatrix = DenseMatrix.valueOf(sample);
            Real fallMahalanobisDistance = calculateMahalanobisDistance(sampleMatrix, FALL_INVERSE_COVARIANCE_MATRIX,
                    FALL_MEANS_MATRIX);
            Real nonFallMahalanobisDistance = calculateMahalanobisDistance(sampleMatrix, NON_FALL_INVERSE_COVARIANCE_MATRIX,
                    NON_FALL_MEANS_MATRIX);


            if (fallMahalanobisDistance.compareTo(nonFallMahalanobisDistance) == -1 && !alarmOn) {
                Log.d("Fall", "Fall was detected");
                //Log.d("FallMD",fallMahalanobisDistance.toString());
                //Log.d("NFallMD",nonFallMahalanobisDistance.toString());
                alarmOn = true;
                notifyFallDetectionListeners();
            }
        } else {
            previous_time = System.currentTimeMillis();
            previousValues[0] = event.values[0];
            previousValues[1] = event.values[1];
            previousValues[2] = event.values[2];

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double[] calculateSlope(float[] values) {
        double[] slopes = new double[3];

        Real time = Real.valueOf(System.currentTimeMillis());
        Real timeDifference = time.minus(Real.valueOf(previous_time));
        Real multiplyFactor = Real.valueOf(10);

        if (timeDifference.doubleValue() == 0) {
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

        //Log.d("Slope ",accelerationDifference +" " + timeDifference.toString());

        previous_time = time.longValue();
        previousValues[0] = values[0];
        previousValues[1] = values[1];
        previousValues[2] = values[2];


        return slopes;
    }

    private Real calculateMahalanobisDistance(DenseMatrix<Real> sample, DenseMatrix<Real> inverseCovariance, DenseMatrix<Real> mean) {

        DenseMatrix<Real> temp = sample.minus(mean);

        DenseMatrix<Real> temp2 = temp.transpose();

        temp2 = temp2.times(inverseCovariance);

        temp2 = temp2.times(temp);

        return temp2.get(0, 0);

    }

    public void alarmOff() {
        alarmOn = false;
    }


}
