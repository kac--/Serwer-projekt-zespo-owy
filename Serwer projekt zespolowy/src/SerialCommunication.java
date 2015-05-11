import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.TooManyListenersException;

import javax.comm.*;




public class SerialCommunication implements SerialPortEventListener {
	
	
	StringBuilder sb = new StringBuilder("");
    @SuppressWarnings("rawtypes")
	Enumeration ports;
    @SuppressWarnings("rawtypes")
	HashMap portMap = new HashMap();

    CommPortIdentifier selectedPortIdentifier = null;
    SerialPort serialPort = null;
    
    InputStream input = null;
    OutputStream output = null;

    final int TIMEOUT = 2000;
    
    //final  int SPACE_ASCII = 32;
    //final  int DASH_ASCII = 45;
    //final  int NEW_LINE_ASCII = 10;
    final  int HASHTAG = 24; 
    
    String logText = "";
	
	public SerialCommunication(){}

    @SuppressWarnings("unchecked")
	public boolean searchForPorts()
    {
    	
    	ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements())
        {
        	CommPortIdentifier curPort;
        	try{
        		curPort = (CommPortIdentifier)ports.nextElement();
        	}catch(NoSuchElementException e){
        		return false;
        	}
        	if(curPort.getPortType() == CommPortIdentifier.PORT_SERIAL)
            {
                portMap.put(curPort.getName(), curPort);
            }
        }
       return true;
    }
    
    public boolean connect(String selectedPort)
    {
         
        selectedPortIdentifier = (CommPortIdentifier)portMap.get(selectedPort);

        CommPort commPort;

        try
        {
            commPort = selectedPortIdentifier.open("Serwer", TIMEOUT);
            serialPort = (SerialPort)commPort;
            //serialPort.setSerialPortParams(9600,8,1,0);                                // parametry transmisji RS232
            logText = selectedPort + " opened successfully.";   
        }catch (PortInUseException e)
        {
            logText = selectedPort + " is in use. (" + e.toString() + ")";
            return false;	
        }catch (Exception e)
        {
            logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
            return false;
        }
        System.out.println(logText);
        return true;
    }
    
    public boolean initIOStream()
    {
        try {
            input = serialPort.getInputStream();
            output = serialPort.getOutputStream();
        }catch (IOException e) {
            logText = "I/O Streams failed to open. (" + e.toString() + ")";
            System.out.println(logText);
            return false;
        }
        return true;
    }
    
    public boolean initListener()
    {
        try
        {
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        }
        catch (TooManyListenersException e)
        {
            logText = "Too many listeners. (" + e.toString() + ")";
            System.out.println(logText);
            return false;
        }
        return true;
    }	
    public void disconnect()
    {
        try
        {
        	serialPort.removeEventListener();
        }catch (Exception e)
        {
        	logText = "Failed to remove EventListener " + serialPort.getName() + "(" + e.toString() + ")";
        	System.out.println(logText);
        }
        try
        {
        	serialPort.close();
        }catch(Exception e)
         {
        	 logText = "Failed to close(serialPort) " + serialPort.getName()
                     + "(" + e.toString() + ")";
        	 System.out.println(logText); 
         }
        try
        {
        	input.close();
        }catch(Exception e){
        	 logText = "Failed to close(serial input) " + serialPort.getName()
                     + "(" + e.toString() + ")";
        	 System.out.println(logText);   
        }
        try
        {
        	output.close();
        }catch(Exception e)
         {
        	 logText = "Failed to close(serial output) " + serialPort.getName() + "(" + e.toString() + ")";
        	 System.out.println(logText); 
         }

    }
    public void serialEvent(SerialPortEvent evt) {
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                
            	byte singleData = (byte)input.read();

                if (singleData != HASHTAG)
                {
                    sb.append(new String(new byte[] {singleData}));
                    System.out.println(sb);
                }
                else
                {
                  String temp = sb.toString();
                  sb.delete(0,temp.length()-1);
                  System.out.println(temp);
                }
            }catch (Exception e)
            {
                logText = "Failed to read data. (" + e.toString() + ")";
            }
        }
    }
    public void serialInit(){
    	if(!searchForPorts()){
    		System.out.println("Failed attempt to find COM ports");
    		return;
    	}
    	if(!connect("COM16")){
    		System.out.println("Failed attempt to connect with chosen port ");
    		return;
    	}
    	if(!initIOStream()){
    		System.out.println("Failed attempt to initiate IO streams");
    		return;
    	}
    	if(!initListener()){
    		System.out.println("Failed attempt to initiate Listener");
    		return;
    	}
    }
}
