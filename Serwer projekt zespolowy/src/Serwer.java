import java.net.*;
import java.io.*;
public class Serwer {

	
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		ServerSocket serverSocket = null; // gniazdo serwera
		Socket socket = null;	
		
		SerialCommunication sc = new SerialCommunication();
		sc.serialInit();
		try{
			
			serverSocket = new ServerSocket(6666);
			while(true){
				try{
					socket = serverSocket.accept();            // akceptuje polaczenia na porcie 6666
					System.out.println("Po³¹czenie przychodz¹ce"+" " +socket.toString() );
					new ConnectionThread(socket);
				}catch(IOException e){
					System.out.println(e);
				}
			}
		}catch(IOException e){
			System.out.println("B³¹d przy tworzeniu gniazda serwera");
			System.exit(-1); 
		}
	}	
}
