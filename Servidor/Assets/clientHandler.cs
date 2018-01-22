using UnityEngine;
using System.Collections;
using System.Net.Sockets;
using System.IO;
using System.Text;
using System.Threading;


namespace AssemblyCSharp
{
	public interface clientHandlerDelegate{
		void clientSocketDidReadMessage (clientHandler client, string message);	
	};

	public class clientHandler
	{
		protected readonly TcpClient client;
		protected readonly string separator = "\n";
		protected readonly Encoding enconding = Encoding.UTF8;
		protected clientHandlerDelegate client_delegate;

		public clientHandler (TcpClient c,clientHandlerDelegate client_d)
		{
            Debug.Log("cliente creado");
            this.client = c;
			this.client_delegate = client_d;
		}

		public void closeClientConnection(){
			if (this.client != null) {
				Debug.Log ("Cerrando conexión con el cliente");
				this.client.Close ();
			}
		}

		public void Run(){
			try{

                Debug.Log("Ejecución del run.");

				StringBuilder dataBuffer = new StringBuilder();
                StreamReader sreader = new StreamReader(client.GetStream());
              

                while (true){
					byte [] read_buffer = new byte[256];
                    //int read_length = client.GetStream().Read(read_buffer, 0, read_buffer.Length);

                    Debug.Log("datos recibidos del cliente.");
                    string toMove = sreader.ReadLine();
                    client_delegate.clientSocketDidReadMessage(this, toMove);
                    Thread.Sleep(20);

                    //if(read_length > 0){
						//dataBuffer.Append(this.enconding.GetString(read_buffer,0,read_length) );
						//string data = dataBuffer.ToString();

						//string[] toMove = data.Split(this.separator.ToCharArray());

						//for(int i=0; i < toMove.Length; i++){
                            //Debug.Log(toMove[i]);
							//this.client_delegate.clientSocketDidReadMessage(this,toMove[i]);
						//}
					//}

                    
				}
			}
			catch (ThreadAbortException exception) 
			{
				Debug.Log ("Thread aborted"+ exception);
			} 
			catch (SocketException exception) 
			{
				Debug.Log ("Socket exception" + exception);
			} 
			finally 
			{
				this.client.Close();
				Debug.Log ("Socket client closed " + this.client.Client.RemoteEndPoint);
			}
		
		}

	};
}

