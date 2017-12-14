package io.github.jjsrra.sensoresnpi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView girox;
    private TextView giroy;
    private TextView giroz;
    private long mRotationTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor giroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        girox = (TextView) findViewById(R.id.GiroscopioX);
        giroy = (TextView) findViewById(R.id.GiroscopioY);
        giroz = (TextView) findViewById(R.id.GiroscopioZ);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                girox.setText(R.string.act_main_no_accuracy);
                giroy.setText(R.string.act_main_no_accuracy);
                giroz.setText(R.string.act_main_no_accuracy);
            }
            return;
        }

        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            girox.setText("x = " + Float.toString(event.values[0]));
            giroy.setText("y = " + Float.toString(event.values[1]));
            giroz.setText("z = " + Float.toString(event.values[2]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor event, int accuracy) {}
}

