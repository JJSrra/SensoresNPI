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
import android.widget.Toast;

import org.w3c.dom.Text;

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
    private RelativeLayout background;
    private boolean boton_pulsado;
    SensorManager mSensorManager;
    Sensor giroscopio;
    Sensor acelerometro;
    Sensor rotationSensor;
    private Button button, connectButton;
    private int touch_position_y;
    private int touch_current_position_y;
    private int global_x, global_y, global_z, current_x, current_y, current_z;

    private connectTask mConnection;
    private tcpClient mTcpClient;
    private boolean pausado;
    private int last_angle;

    private TextView iptext,porttext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        giroscopio = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        rotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        boton_pulsado = false;
        pausado = false;

        girox = findViewById(R.id.GiroscopioX);
        giroy = findViewById(R.id.GiroscopioY);
        giroz = findViewById(R.id.GiroscopioZ);
        button = findViewById(R.id.button);
        connectButton = findViewById(R.id.connectButton);
        background = findViewById(R.id.background);

        iptext = findViewById(R.id.ipEdit);
        porttext = findViewById(R.id.portEdit);

        global_x = 0;
        global_y = 0;
        global_z = 0;
        current_x = 0;
        current_y = 0;
        current_z = 0;

        touch_position_y = 0;
        touch_current_position_y = 0;
        last_angle = 0;



        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeButtonStatus();
            }
        });


        connectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(iptext.getText().toString().isEmpty() || porttext.getText().toString().isEmpty() ) {
                    mConnection = new connectTask();
                    mConnection.execute();
                    iptext.setEnabled(false);
                    porttext.setEnabled(false);
                }
                else{
                    String [] params = {iptext.getText().toString(),porttext.getText().toString()};
                    mConnection = new connectTask();
                    mConnection.execute(params);
                    iptext.setEnabled(false);
                    porttext.setEnabled(false);
                }
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
        if(!boton_pulsado){
            last_angle += global_z;
            last_angle %= 360;
            pausado = true;
        }

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
                    if (diff < -200) {
                        String resetMsg = "reset\n";
                        mTcpClient.sendMessage(resetMsg);
                    }


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

            global_z = (int) orientaciones[2];

            int toSend;
            if(pausado)
                toSend = (last_angle+auz)%360;
            else
                toSend = auz;

            String GiroMsg = toSend+"\n";
            Log.println(Log.DEBUG,"message","bytes del mensaje"+GiroMsg.length());
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

    class connectTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msg){

            if(msg.length > 0) {
                String ip = msg[0];
                int port = new Integer(msg[1]);
                mTcpClient = new tcpClient(ip, port);
                mTcpClient.runClient();

            }else{
                mTcpClient = new tcpClient();
                mTcpClient.runClient();

            }

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

