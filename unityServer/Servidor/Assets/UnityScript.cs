using UnityEngine;
using System.Threading;
using AssemblyCSharp;
using System;


public class UnityScript : MonoBehaviour, clientHandlerDelegate {

    //public GameObject mModel=null;
    float degreesToMove = 0;
    float degreesBefore = 0;
    float realDeg = 180 ;
    float diff;
    private simpleServer mServer;
    private readonly Thread paserThread;

	// Use this for initialization
	void Start () {
        Debug.Log("hello");
        clientHandlerDelegate client_delegate = this;

        this.mServer = new simpleServer(client_delegate);

        ThreadStart ts = new ThreadStart(this.mServer.startServer);
        Thread serverThread = new Thread(ts);

        serverThread.Start();
	}

    private void OnApplicationQuit()
    {
        this.mServer.Close();
        this.mServer = null;
    }

    private void Update()
    {
        Debug.Log("degressBefore: " + degreesBefore);
        Debug.Log("degressAfter: " + degreesToMove);
        transform.Rotate(0, -diff, 0);
        degreesBefore = degreesToMove;
        realDeg = (realDeg - diff)%360;
        calculateDifference(); 
    }

    private void calculateDifference()
    {
        diff = degreesToMove - degreesBefore;
        Debug.Log("diferencia: " + diff);
        
    }

    public void clientSocketDidReadMessage(clientHandler client, string message)
    {
        Debug.Log("cliente ha leido el mensaje " + message);
        if( message == "reset")
        {
            Debug.Log("resetenando posición.");
           if(realDeg > 180)
            {
                diff = -(180 - realDeg);
            }
            else
            {
                diff = (realDeg - 180);
            }
        }
        else
        {
            Debug.Log("calculando diferencia");
            degreesToMove = float.Parse(message);
            calculateDifference();
        }
        

    }
}
