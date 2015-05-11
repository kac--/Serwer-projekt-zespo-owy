import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class ConnectionThread implements Runnable  {
	
	
	boolean readerCreated = false;
	Socket client;
	BufferedReader in;
	String fromClient = null;
	BufferedWriter out;
	boolean writerCreated = false;
	
	public ConnectionThread(Socket client){
		this.client=client;
		Thread watek = new Thread(this);
		watek.start();
		}
	
	boolean isConnected(){
		if((null == client)||( client.isClosed())){
			return false;	
		}
		if((! readerCreated)||(! writerCreated)){
			return false;
		}
		return true;
		}
		
	void disconnect()  {
		try {
			in.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run()  {
		System.out.println("start : " + Thread.currentThread().getName());
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			readerCreated = true;
			out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			writerCreated = true;
		} catch (IOException e) {
			readerCreated = false;
		}
		while(isConnected()){
			try {
				fromClient = in.readLine();     // wypisze na konsolê to co zosta³o wys³ane przez klienta
			}catch (IOException e) {
				disconnect();
			}
			if(null == fromClient){
				disconnect();
				break;
			}
			
			int charExists = fromClient.indexOf("|");
			
			if(charExists == -1){ // bo funkcja indexOf zwraca -1 je¿eli nie znajdzie znaku "|"
				System.out.println(fromClient);
				continue;
			}
			String commandCode = fromClient.substring(0,charExists);
			switch(commandCode){
				case "timestamp" : {
					handleTimestamp(fromClient.substring(charExists+1,fromClient.length()));
					break;
				}
				default :{
					System.out.println(fromClient);
					break;
				}
			}
		}

		System.out.println("stop : " + Thread.currentThread().getName());
	}
	
	void handleTimestamp(String timestamp){
		System.out.println(timestamp);
		send("odpowiedŸ");
	}
	public int send(String toSend){
		if(null == toSend){
			return -3;
		}
		if((null == client)||(client.isClosed())){
			return -1;
		}
		if(null == out){
			return -2;
		}
		try {
			out.write(toSend+"\r\n");
			out.flush();
		} catch (IOException e) {
			return -1;
		}
		return 0;
	}
}
