import java.net.*;
import java.io.*;
public class Serwer {

	public static void main(String[] args) {
		
		ServerSocket serverSocket = null; // gniazdo serwera
		Socket socket = null;	
		
		
		try{
			serverSocket = new ServerSocket(6666);
			while(true){
				try{
					socket = serverSocket.accept();            // akceptuje polaczenia na porcie 6666
					System.out.println("Po��czenie przychodz�ce"+" " +socket.toString() );
					new ConnectionThread(socket);				//utworzenie w�tku kt�ry
				}catch(IOException e){
					System.out.println(e);
				}
			}
		}catch(IOException e){
			System.out.println("B��d przy tworzeniu gniazda serwera");
			System.exit(-1); 
			
		}
		
	
		
			
	}	
}
