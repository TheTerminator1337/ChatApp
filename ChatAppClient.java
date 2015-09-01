import java.io.*;
import java.net.*;
import javax.swing.*;
import java.applet.*;
import java.net.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

//Swing based client
public class ChatAppClient 
{
	static public class ClientSounds
	{
		public static void main(String[] args) throws Exception{ messageClip = Applet.newAudioClip(new URL("file://message_sound.wav")); }
		
		public void playMessageClip() { messageClip.play(); }
		
		static AudioClip messageClip;
	}
	
	public ChatAppClient()
	{
		//Init the GUI elements
		m_textField.setEditable(false);
		m_messageArea.setEditable(false);
		
		m_frame.getContentPane().add(m_textField, "North");
		m_frame.getContentPane().add(new JScrollPane(m_messageArea), "Center");
		m_frame.pack();
		
		//Add listeners for keyboard input
		 m_textField.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent e) { m_out.println(m_textField.getText()); m_textField.setText(""); } });
	}
	
	private String getServerIP() { return JOptionPane.showInputDialog(m_frame, "Enter IP address of server: ", "Welcome", JOptionPane.QUESTION_MESSAGE); }
	
	private String getName() { return JOptionPane.showInputDialog(m_frame, "Choose a screen name: ", "Select name", JOptionPane.PLAIN_MESSAGE); }
	
	//Connects to the server, then continually updates the client
	private void run() throws IOException
	{
		String IP = getServerIP();
		
		//Make the connection
		Socket socket = new Socket(IP, PORT);
		
		m_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		m_out = new PrintWriter(socket.getOutputStream(), true);
		
		//Start the update loop
		while (true)
		{
			String lineBuffer = m_in.readLine();
			
			if (lineBuffer.startsWith("NAME:"))
				m_out.println(getName());
			
			else if (lineBuffer.startsWith("NAMEGOOD"))
				m_textField.setEditable(true);
			
			else if (lineBuffer.startsWith("MSG"))
			{
				m_messageArea.append(lineBuffer.substring(4)+ "\n");
				//m_sounds.playMessageClip();
			}
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		ChatAppClient client = new ChatAppClient();
		
		client.m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.m_frame.setVisible(true);
		client.run();
	}
	
	BufferedReader m_in;
	PrintWriter m_out;
	
	JFrame m_frame = new JFrame("Chat Client - v1.0");
	JTextField m_textField = new JTextField(40);
	JTextArea m_messageArea = new JTextArea(8, 40);
	
	//ClientSounds m_sounds = new ClientSounds();
	
	private static int PORT = 1337;
}
