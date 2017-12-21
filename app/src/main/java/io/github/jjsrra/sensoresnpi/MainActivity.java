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
    private TextView aceleracionx;
    private TextView aceleraciony;
    private TextView aceleracionz;
    private TextView posicion;
    SensorManager mSensorManager;
    Sensor giroscopio;
    Sensor acelerometro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        girox = findViewById(R.id.GiroscopioX);
        giroy = findViewById(R.id.GiroscopioY);
        giroz = findViewById(R.id.GiroscopioZ);
        posicion = findViewById(R.id.GiroscopioPosicion);
        aceleracionx = findViewById(R.id.AcelerometroX);
        aceleraciony = findViewById(R.id.AcelerometroY);
        aceleracionz = findViewById(R.id.AcelerometroZ);
    }

    @Override
    protected void onResume(){
        super.onResume();
        mSensorManager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, acelerometro, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
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
            if(event.values[0] < 1 && event.values[1] < 1){

                posicion.setText("El dispositivo está quieto");

            }
        else{
                posicion.setText("El dispositivo está moviéndose");
            }


        }

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            aceleracionx.setText("x = " + Float.toString(event.values[0]));
            aceleraciony.setText("y = " + Float.toString(event.values[1]));
            aceleracionz.setText("z = " + Float.toString(event.values[2]));

        }

    }

    @Override
    public void onAccuracyChanged(Sensor event, int accuracy) {}
}

