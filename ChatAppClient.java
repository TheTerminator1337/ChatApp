import java.io.*;
import java.net.*;

public class ChatAppClient {

	public static void main(String[] args) throws Exception
	{
		ChatAppClient client = new ChatAppClient();
        client.run();
	}
	
	private void run() throws IOException
	{
		Socket socket = new Socket(m_serverIp, PORT);
		
		m_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
	    m_out = new PrintWriter(socket.getOutputStream(), true);
	    
	    //Print all of the messages
	    while (true)
	    	System.out.println(m_in.readLine());
	}
	
	private BufferedReader m_in;
    private PrintWriter m_out;
    
    private String m_serverIp;
    private static final int PORT = 1337;
}
