package fall_detection;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import org.jscience.mathematics.number.Real;
import org.jscience.mathematics.vector.DenseMatrix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by abdielrosado on 3/2/16.
 */
public class FallDetectionManager implements FallDetector, SensorEventListener {

    private List<FallDetectionListener> fallDetectionListeners = new ArrayList<FallDetectionListener>();

    private float[] previousValues = new float[3];

    private long previous_time;

    //private final double[][] FALL_INVERSE_COVARIANCE_VALUES = {{382.3,-258.2,15.8},{-258.2,756.1,67.9},{15.8,67.9,578.3}};

    private final Real[][] FALL_INVERSE_COVARIANCE_VALUES = {{Real.valueOf(0.0002447302456), Real.valueOf(-0.0000214938914933133), Real.valueOf(-0.00000718507564204711)},
            {Real.valueOf(-0.0000214938914933133), Real.valueOf(0.000126294693009017), Real.valueOf(-0.0000182498286063061)},
            {Real.valueOf(-0.00000718507564204711), Real.valueOf(-0.0000182498286063061), Real.valueOf(0.000170484778924723)}};

    private final DenseMatrix<Real> FALL_INVERSE_COVARIANCE_MATRIX = DenseMatrix.valueOf(FALL_INVERSE_COVARIANCE_VALUES);

    //private final RealMatrix FALL_INVERSE_COVARIANCE_MATRIX = new Array2DRowRealMatrix(FALL_INVERSE_COVARIANCE_VALUES);

//    private final double[][] NON_FALL_INVERSE_COVARIANCE_VALUES = {{0.0132,-0.0036,-0.0047},{-0.0036,0.0054,-0.0001},
//            {-0.0047,-0.0001,0.0108}};

    private final Real[][] NON_FALL_INVERSE_COVARIANCE_VALUES = {{Real.valueOf(0.00584466950764467), Real.valueOf(-0.001246764431), Real.valueOf(-0.001718949159)},
            {Real.valueOf(-0.001246764431), Real.valueOf(0.003938964257), Real.valueOf(-0.0008250059424)},
            {Real.valueOf(-0.001718949159), Real.valueOf(-0.0008250059424), Real.valueOf(0.004164093876)}};

    private final DenseMatrix<Real> NON_FALL_INVERSE_COVARIANCE_MATRIX = DenseMatrix.valueOf(NON_FALL_INVERSE_COVARIANCE_VALUES);

//     private final RealMatrix NON_FALL_INVERSE_COVARIANCE_MATRIX = new Array2DRowRealMatrix(NON_FALL_INVERSE_COVARIANCE_VALUES);

    //private final double[] FALL_MEANS = {78.5251,52.283,61.5768};

    private final Real[][] FALL_MEANS = {{Real.valueOf(95.93624633)}, {Real.valueOf(102.222734)}, {Real.valueOf(94.7043037)}};

    //private final RealMatrix FALL_MEANS_MATRIX = new Array2DRowRealMatrix(FALL_MEANS);

    private final DenseMatrix<Real> FALL_MEANS_MATRIX = DenseMatrix.valueOf(FALL_MEANS);

//    private final double[] NON_FALL_MEANS = {10.0271,11.8262,10.1568};

    private final Real[][] NON_FALL_MEANS = {{Real.valueOf(13.3182)}, {Real.valueOf(14.1203)}, {Real.valueOf(13.6385)}};

    //private final RealMatrix NON_FALL_MEANS_MATRIX = new Array2DRowRealMatrix(NON_FALL_MEANS);

    private final DenseMatrix<Real> NON_FALL_MEANS_MATRIX = DenseMatrix.valueOf(NON_FALL_MEANS);

    private AtomicBoolean alarmOn;

    private AtomicBoolean freeFall;

    private int count;

    private Real multiplyFactor;

    private static FallDetectionManager instance;

    private FallDetectionManager() {
        alarmOn = new AtomicBoolean(false);
        freeFall = new AtomicBoolean(false);
        count = 0;
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

//            Real[][] sample = {{Real.valueOf(slopes[0])}, {Real.valueOf(slopes[1])}, {Real.valueOf(slopes[2])}};
//
//            DenseMatrix<Real> sampleMatrix = DenseMatrix.valueOf(sample);
//            Real fallMahalanobisDistance = calculateMahalanobisDistance(sampleMatrix, FALL_INVERSE_COVARIANCE_MATRIX,
//                    FALL_MEANS_MATRIX);
//            Real nonFallMahalanobisDistance = calculateMahalanobisDistance(sampleMatrix, NON_FALL_INVERSE_COVARIANCE_MATRIX,
//                    NON_FALL_MEANS_MATRIX);



//            Log.d("Mahalanobis",fallMahalanobisDistance.toString() + ", " + nonFallMahalanobisDistance.toString());
//            Log.d("Mahalanobis",new Boolean(freeFall.get()).toString() + fallMahalanobisDistance.toString());

            synchronized (this){
//                if ((fallMahalanobisDistance.minus(nonFallMahalanobisDistance).doubleValue() < -50) && !alarmOn.get() && freeFall.get()) {
//                if(fallMahalanobisDistance.doubleValue() < 1.5 && !alarmOn.get() && freeFall.get()){
                if((calculateMagnitude(event.values) - 96.2361) > 300 && !alarmOn.get() && freeFall.get()){
                    Log.d("Fall", "Fall was detected");
                    //Log.d("FallMD",fallMahalanobisDistance.toString());
                    //Log.d("NFallMD",nonFallMahalanobisDistance.toString());
                    FallDetectionManagement.stopListening();
                    alarmOn.set(true);
                    previousValues[0] = 0;
                    notifyFallDetectionListeners();
                }
            }


            if((calculateMagnitude(event.values) - 96.2361) < -60){
                    freeFall.set(true);
                    count = 0;

            }else if(count > 5){
                freeFall.set(false);
                count = 0;
            }
            count ++;


        } else {
            previous_time = System.currentTimeMillis();
            previousValues[0] = event.values[0];
            previousValues[1] = event.values[1];
            previousValues[2] = event.values[2];

        }


    }

    private float calculateMagnitude(float[] values){
        return (values[0]*values[0] + values[1]*values[1] + values[2]*values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double[] calculateSlope(float[] values) {
        double[] slopes = new double[3];

        Real time = Real.valueOf(System.currentTimeMillis());
        Real timeDifference = time.minus(Real.valueOf(previous_time));



        if (timeDifference.compareTo(Real.valueOf(100)) == -1) {
            multiplyFactor = Real.valueOf(100);
        } else {
            multiplyFactor = Real.valueOf(1000);
        }


        if (timeDifference.doubleValue() == 0) {
            return null;
        }


        Real accelerationDifference = Real.valueOf(values[0]).minus(Real.valueOf(previousValues[0]));
        Real slope = accelerationDifference.divide(timeDifference).times(multiplyFactor);
        slopes[0] = slope.abs().doubleValue();

        accelerationDifference = Real.valueOf(values[1]).minus(Real.valueOf(previousValues[1]));
        slope = accelerationDifference.divide(timeDifference).times(multiplyFactor);
        slopes[1] = slope.abs().doubleValue();


        accelerationDifference = Real.valueOf(values[2]).minus(Real.valueOf(previousValues[2]));
        slope = accelerationDifference.divide(timeDifference).times(multiplyFactor);
        slopes[2] = slope.abs().doubleValue();

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
        alarmOn.set(false);
    }


}
