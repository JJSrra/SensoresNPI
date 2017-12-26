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
import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView girox;
    private TextView giroy;
    private TextView giroz;
    private TextView aceleracionx;
    private TextView aceleraciony;
    private TextView aceleracionz;
    private TextView multitouch;
    private RelativeLayout background;
    private boolean boton_pulsado;
    SensorManager mSensorManager;
    Sensor giroscopio;
    Sensor acelerometro;
    private Button button, resetButton;
    private int touch_position_y;
    private int touch_current_position_y;
    private float global_x, global_y, global_z, current_x, current_y, current_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        boton_pulsado = false;

        girox = findViewById(R.id.GiroscopioX);
        giroy = findViewById(R.id.GiroscopioY);
        giroz = findViewById(R.id.GiroscopioZ);
        button = findViewById(R.id.button);
        resetButton = findViewById(R.id.resetMultitouch);
        aceleracionx = findViewById(R.id.AcelerometroX);
        aceleraciony = findViewById(R.id.AcelerometroY);
        aceleracionz = findViewById(R.id.AcelerometroZ);
        background = findViewById(R.id.background);
        multitouch = findViewById(R.id.multitouch);

        global_x = 0;
        global_y = 0;
        global_z = 0;
        current_x = 0;
        current_y = 0;
        current_z = 0;

        touch_position_y = 0;
        touch_current_position_y = 0;

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeButtonStatus();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                multitouch.setText("Esto cambiará cuando deslices dos dedos sobre la pantalla la suficiente distancia");
            }
        });

        background.setOnTouchListener(new RelativeLayout.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent m) {
                handleTouch(m);
                return true;
            }
        });
    }

    protected void changeButtonStatus(){
        boton_pulsado = !boton_pulsado;

    }

    protected void handleTouch(MotionEvent m){
        int pointerCount = m.getPointerCount();
        if (pointerCount == 2) {
            int action = m.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_POINTER_DOWN:
                    touch_position_y = (int) m.getY(1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_current_position_y = (int) m.getY(1);
                    int diff = touch_position_y - touch_current_position_y;
                    if (diff < -300)
                        multitouch.setText("¡Deslizado con 2 dedos!\nPulsa el botón \"Reiniciar\" para volver a probar");
                        global_x = 0;
                        global_y = 0;
                        global_z = 0;
                    break;
            }
        }
        else{
            touch_position_y = 0;
            touch_current_position_y = 0;
        }
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

        if (boton_pulsado) {
            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                current_x = event.values[0];
                current_y = event.values[1];
                current_z = event.values[2];

                global_x = roundValues(global_x+current_x, 2);
                global_y = roundValues(global_y+current_y, 2);
                global_z = roundValues(global_z+current_z, 2);

                girox.setText("x = " + global_x);
                giroy.setText("y = " + global_y);
                giroz.setText("z = " + global_z);

            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                aceleracionx.setText("x = " + Float.toString(event.values[0]));
                aceleraciony.setText("y = " + Float.toString(event.values[1]));
                aceleracionz.setText("z = " + Float.toString(event.values[2]));

            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor event, int accuracy) {}

    public static float roundValues(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}

