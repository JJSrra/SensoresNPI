package io.github.jjsrra.sensoresnpi;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView girox;
    private TextView giroy;
    private TextView giroz;
    private TextView multitouch;
    private RelativeLayout background;
    private boolean boton_pulsado;
    SensorManager mSensorManager;
    Sensor giroscopio;
    Sensor acelerometro;
    Sensor rotationSensor;
    private Button button, resetButton;
    private int touch_position_y;
    private int touch_current_position_y;
    private float global_x, global_y, global_z, current_x, current_y, current_z;

    private connectTask mConnection;
    private tcpClient mTcpClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        boton_pulsado = false;

        girox = findViewById(R.id.GiroscopioX);
        giroy = findViewById(R.id.GiroscopioY);
        giroz = findViewById(R.id.GiroscopioZ);
        button = findViewById(R.id.button);
        resetButton = findViewById(R.id.resetMultitouch);
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

        mConnection = new connectTask();
        mConnection.execute();

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
                        girox.setText("x = 0");
                        giroy.setText("y = 0");
                        giroz.setText("z = 0");

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
        // Lo registramos.
        mSensorManager.registerListener(this,rotationSensor,mSensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        mConnection.cancel(true);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            girox.setText(R.string.act_main_no_accuracy);
            giroy.setText(R.string.act_main_no_accuracy);
            giroz.setText(R.string.act_main_no_accuracy);
        }
        if (boton_pulsado) {
            // Creamos una matriz de rotation con los valores obtenidos de los values del sensor.
            float[] rotationMatrix = new float[16];
            mSensorManager.getRotationMatrixFromVector(rotationMatrix,event.values);

            // Remap coordenadas.
            float[] remapRotationMatrix = new float[16];
            mSensorManager.remapCoordinateSystem(rotationMatrix,
                    mSensorManager.AXIS_X,
                    mSensorManager.AXIS_Z,
                    remapRotationMatrix);

            // Obtenemos la orientación y convertimos las orientaciones a grados.
            float[] orientaciones = new float[3];
            mSensorManager.getOrientation(remapRotationMatrix,orientaciones);

            for(int i=0; i < 3; i++){
                orientaciones[i] = (float) (Math.toDegrees(orientaciones[i]));
            }

            int aux, auy, auz;
            aux = (int) orientaciones[0];
            auy = (int) orientaciones[1];
            auz = (int) orientaciones[2];

            girox.setText( "x = " + aux );
            giroy.setText( "y = " + auy );
            giroz.setText( "z = " + auz );

            String GiroMsg = aux+"|"+auy+"|"+"|"+auz+"\n";
            mTcpClient.sendMessage(GiroMsg);

            return;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor event, int accuracy) {}

    public static float roundValues(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    class connectTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... msg){
            mTcpClient = new tcpClient();
            mTcpClient.runClient();

            return null;
        }

        @Override
        protected void onCancelled(){
            super.onCancelled();
            if(mTcpClient != null) {
                mTcpClient.stopClient();
            }
        }


    }

}

