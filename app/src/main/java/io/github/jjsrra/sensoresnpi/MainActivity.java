package io.github.jjsrra.sensoresnpi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Color;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView girox;
    private TextView giroy;
    private TextView giroz;
    private TextView aceleracionx;
    private TextView aceleraciony;
    private TextView aceleracionz;
    private TextView posicion;
    private RelativeLayout background;
    SensorManager mSensorManager;
    Sensor giroscopio;
    Sensor acelerometro;
    private Button button;

     private static final double Eps = 0.1;

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
        background = findViewById(R.id.background);
        button = findViewById(R.id.button);
        aceleracionx = findViewById(R.id.AcelerometroX);
        aceleraciony = findViewById(R.id.AcelerometroY);
        aceleracionz = findViewById(R.id.AcelerometroZ);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS){
                    background.setBackgroundColor(Color.MAGENTA);
                    //return true;
                //}
                //return false;
            }
        });
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
            if(event.values[0] < Eps && event.values[1] < Eps && event.values[2] < Eps){

                posicion.setText("El dispositivo está quieto");
                //background.setBackgroundColor(Color.CYAN);
            }
        else{
                posicion.setText("El dispositivo está moviéndose");
                //background.setBackgroundColor(Color.GREEN);
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

