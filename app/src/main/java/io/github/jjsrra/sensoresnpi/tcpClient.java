package io.github.jjsrra.sensoresnpi;

import android.util.Log;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Alberto on 15/01/2018.
 */

public class tcpClient {
    private static String IP_SERVER = "192.168.1.6";
    private static int SERVER_PORT = 5000;

    private PrintWriter pw;
    private Socket mSocket;
    private Boolean run = true;

    public tcpClient(String ip, int port){
        IP_SERVER = ip;
        SERVER_PORT = port;
    }

    public tcpClient(){

    }


    public void stopClient(){
        run = false;
        try {
            mSocket.close();
            pw.close();
        }catch (IOException e){

        }
    }

    public void sendMessage(String msg){
        if( pw != null && !pw.checkError() ){
            new sendMessageThread(msg).start();
        }
    }

    public void runClient(){
        run = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(IP_SERVER);

            mSocket = new Socket(serverAddr,SERVER_PORT);
            pw = new PrintWriter(mSocket.getOutputStream());

            Log.println(Log.DEBUG,"socket","conectado al server");


        } catch (UnknownHostException e1) {
            e1.printStackTrace();
            Log.e("socket","no se pudo conectar con el cliente, unknownhostconnection");

        } catch (IOException e1) {
            e1.printStackTrace();
            Log.e("socket","no se pudo conectar ioexception");


        }

    }

    public class sendMessageThread extends Thread {
        private String msg;

        public sendMessageThread(String message) {
            this.msg = message;
        }

        @Override
        public void run() {
            pw.write(msg);
            pw.flush();
            Log.println(Log.VERBOSE,"message","mensage enviado");
        }
    }



}
