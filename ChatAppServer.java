import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

//Multithreaded server

public class ChatAppServer
{
	private static class UserThread extends Thread 
	{
		//Create the thread and copy the socket
		public UserThread(Socket socket) { this.m_socket = socket; }
		
		public void run()
		{
			try
			{
				m_in = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
				m_out = new PrintWriter(m_socket.getOutputStream(), true);
				
				//Request name for client
				while (true)
				{
					m_out.println("NAME:");
					m_name = m_in.readLine();
					
					if (m_name == null)
						return;
					
					//Make sure our threads don't interfere with each other and have memory issues
					synchronized(m_clientNames)
					{
						if (!m_clientNames.contains(m_name))
						{
							m_clientNames.add(m_name);
							break;
						}
					}
				}
				
				m_out.println("NAMEGOOD");
				m_clientWriters.add(m_out);
				
				//Start accepting messages from the client
				while (true)
				{
					String bufferedInput = m_in.readLine();
					
					if (bufferedInput == null)
						return;
					
					for (PrintWriter i : m_clientWriters)
					{
						i.println("MSG " + m_name + ": " + bufferedInput);
						
						m_messageArea.append(m_name + ": " + bufferedInput + "\n");
					}
				}
				
			} catch (IOException e) { System.out.println(e); } finally
			{
				//Clean up stuff if the server is failing
				if (m_name != null)
					m_clientNames.remove(m_name);
				
				if (m_out != null)
					m_clientWriters.remove(m_out);
				
				try { m_socket.close(); } catch (IOException e) {}
			}
		}
		
		private String m_name;
		private Socket m_socket;
		private BufferedReader m_in;
		private PrintWriter m_out;
	}
	
	public static void main(String[] args) throws Exception
	{
		m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		m_frame.setVisible(true);
		
		//Init the GUI elements
		m_messageArea.setEditable(false);
		
		m_frame.getContentPane().add(new JScrollPane(m_messageArea), "Center");
		m_frame.pack();
		
		m_messageArea.append("Chat server is initializing.\n");
		m_messageArea.append("Created by Peter Senyszyn\n");

		ServerSocket listener = new ServerSocket(PORT);
		
		m_messageArea.append("Server now running on port: " + PORT + ".\n");
		
		try
		{
			while (true)
				new UserThread(listener.accept()).start();
		} finally { listener.close(); }
	}
	
	private static int PORT = 1337;
	
	private static HashSet<String> m_clientNames = new HashSet<String>();
	private static HashSet<PrintWriter> m_clientWriters = new HashSet<PrintWriter>();	
	
	static JFrame m_frame = new JFrame("Chat Server - v1.0");
	static JTextArea m_messageArea = new JTextArea(8, 40);
}
