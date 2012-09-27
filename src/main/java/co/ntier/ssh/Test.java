package co.ntier.ssh;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.Console;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.LocalPortForwarder;


public class Test {

	public static char[] getPassword(){
		Console console = System.console();
		if( console == null ){
			System.out.println("<Entering password on clear text>");
			Scanner scanner = new Scanner(System.in);
			return scanner.nextLine().toCharArray();
		}else{
			System.out.println("<Entering password on console>");
			return console.readPassword();
		}
	}
	
	public static void main(String[] args) throws IOException, URISyntaxException {
		if( !Desktop.isDesktopSupported() ){
			throw new RuntimeException("Desktop not supported.");
		}
		Desktop desktop = Desktop.getDesktop();
		if( !desktop.isSupported(Action.BROWSE) ){
			throw new RuntimeException("Could not open browser");
		}
		desktop.browse(new URI("http://davidwelch.co"));
	}
	
	public static void main2(String[] args) throws Exception{
		
		System.out.print("Enter your password: ");
		char[] pass = getPassword();
		System.out.println("Got pass " + new String(pass));
		
		Connection conn = new Connection("davidwelch.co", 22999);
		ConnectionInfo info = conn.connect();
		conn.authenticateWithPassword("dwelch", new String(pass));

		
		System.out.println("Created connection!");
		
		LocalPortForwarder forwarder = conn.createLocalPortForwarder(9999, "google.com", 80);
		
		System.out.println("Forwarding... Press Enter to quit");
		System.in.read();
		forwarder.close();
	}
}
