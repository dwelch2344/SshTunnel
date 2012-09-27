package co.ntier.ssh;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.LocalPortForwarder;

public class SimpleTunnel {

	String user, pass, host, destHost;
	int port, lport, destPort;
	
	private Connection conn;
	private LocalPortForwarder forwarder;
	
	public void loadProperties() throws IOException {

		File file = new File("ssh.properties");
		if( !file.exists() ){
			throw new FileNotFoundException("Missing " + file.getAbsolutePath());
		}
		
		Properties props = new Properties();
		FileInputStream fis = new FileInputStream(file);
		props.load(fis);
		
		
		
		user = props.getProperty("user");
		pass = props.getProperty("password");
		host = props.getProperty("host");
		port = Integer.valueOf( props.getProperty("port") );
		
		lport = Integer.valueOf( props.getProperty("fwd.lport") );
		destHost = props.getProperty("fwd.dest");
		destPort = Integer.valueOf( props.getProperty("fwd.destPort") );
	}
	
	public void connect() throws IOException{
		conn = new Connection(host, port);
//		ConnectionInfo info = conn.connect();
		System.out.println("Connecting...");
		conn.connect(null, 5 * 1000, 5 * 1000);
		System.out.println("Authenticating...");
		conn.authenticateWithPassword(user, pass);
		
		System.out.println("Beginning forwarding");
		forwarder = conn.createLocalPortForwarder(lport, destHost, destPort);
		String msg = String.format("Successfully connected to %s. \nClose this dialog to terminate connection & exit", host);
		JOptionPane.showMessageDialog(null, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
		
		forwarder.close();
		conn.close();
	}
	
	private void run(){

		try{
			loadProperties();
		}catch(Exception e){
			error("Failed loading configuration", e);
			return;
		}
		
		System.out.println("Configured...");
		
		try {
			connect();
		} catch (IOException e) {
			error("Failed on connection", e);
			return;
		}
		
		System.out.println("Done");
	}
	
	private void error(String message, Throwable t){
		String msg = "An unexpected error occurred: \n" + message + "\n" + t.getMessage();
		JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
		t.printStackTrace();
	}
	
	public static void main(String[] args){
		SimpleTunnel tunnel = new SimpleTunnel();
		System.out.println("Loading...");
		tunnel.run();
		System.out.println("Exiting...");
		
	}
}

