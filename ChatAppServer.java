import java.io.*;
import java.net.*;
import java.util.*;

public class ChatAppServer
{
	//Thread for the class
	private static class ChatThread extends Thread
	{
		public ChatThread(Socket socket) { this.m_socket = socket;}
		
		public void run()
		{
			try
			{
				//Make sure that we can send more than bytes
				m_in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
				m_out = new PrintWriter(m_socket.getOutputStream(), true);
				
				while (true)
				{
					m_out.println("Please input your name.");
					
					m_clientName = m_in.readLine();
					
					if (m_clientName == null)
						return;
					
					synchronized (m_names)
					{
						if (!m_names.contains(m_clientName))
						{
							m_names.add(m_clientName);
							
							break;
						}
					}
				}
				
				m_out.println("Welcome, " + m_clientName + ".");
				m_writers.add(m_out);
				
				m_out.println("To disconnect, type ded.");
				
				while (true)
				{
					String buffer = m_in.readLine();
					
					if (buffer == null)
						return;
					
					else if (buffer == "ded")
					{
						m_names.remove(m_clientName);
						m_writers.remove(m_out);
						m_socket.close();
						
						m_out.println("Bye!");
						
						break;
					}
					
					for (PrintWriter writer : m_writers)
						writer.println(m_clientName + ": " + buffer);
				}
			} catch (IOException e) { System.out.println(e);
			} finally 
			{
                if (m_clientName != null)
                    m_names.remove(m_clientName);
      
                if (m_out != null)
                    m_writers.remove(m_out);
                
                try { m_socket.close(); } catch (IOException e) {}
			}
			}
			
		
		private BufferedReader m_in;
		private PrintWriter m_out;
		private Socket m_socket;
		private int m_clientNum;
		private String m_clientName;
	}
	
	//public methods
	public static void main(String[] args) throws IOException
	{
		ServerSocket listener = new ServerSocket(PORT);
		
		try
		{
			while (true) 
			{
				System.out.println("Server is running!");
				new ChatThread(listener.accept()).start();
			}
		} finally { listener.close(); }
	}
	
	//private vars
	private static final int PORT = 1337;
	
	//All of the clients in the chat room
    private static HashSet<String> m_names = new HashSet<String>();

    //A sort of queue for the messages
    private static HashSet<PrintWriter> m_writers = new HashSet<PrintWriter>();
}
