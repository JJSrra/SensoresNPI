using UnityEngine;
using System.Net.Sockets;
using System.Net;
using System.Threading;
using System;
using AssemblyCSharp;

public class simpleServer {



    // Variables para el servidor.
    protected readonly int PORT;
	protected readonly TcpListener mServer = null;

    protected Thread mClientThread;
    protected clientHandler mHandler;
    protected readonly clientHandlerDelegate client_delegate;

    // Constructor.
    public simpleServer(clientHandlerDelegate client_d, int puerto=6000 )
    {
        this.PORT = puerto;
        this.client_delegate = client_d;

        this.mServer = new TcpListener(IPAddress.Any, this.PORT);
        Debug.Log("Creado servidor");
    }

    // Función que ejecutará el servidor en la hebra.
    public void startServer() {

        Debug.Log("Comenzando a escuchar a clientes");
        this.mServer.Start();
        this.mServer.BeginAcceptTcpClient(new AsyncCallback(acceptClient), this.mServer);

    }

    // Función asíncrona que acepta el cliente.
	protected void acceptClient(IAsyncResult ar){

        int threadId = Thread.CurrentThread.ManagedThreadId;
		TcpListener	listener = (TcpListener)ar.AsyncState;
		TcpClient mClient = listener.EndAcceptTcpClient (ar);

		Debug.Log ("Cliente aceptado");
		Debug.Log ("Comenzando a escuchar del cliente");

        mHandler = new clientHandler(mClient, this.client_delegate);

        Thread clientThread = new Thread(new ThreadStart(mHandler.Run));
        mClientThread = clientThread;
        Debug.Log("Thread cliente iniciada");
        mClientThread.Start();
	}

    // Función para para el servidor.
    protected void stopListening()
    {
        this.mServer.Stop();
        Debug.Log("El servidor ha dejado de escuchar");
    }

    // Función para terminar la conexión con el cliente.
    public void Close()
    {
        this.mServer.Stop();
        Debug.Log("Cerrando conexión con la hebra");
        mHandler.closeClientConnection();
        Debug.Log("Destruyendo hebra");
        mClientThread.Abort();

    }



    
}
