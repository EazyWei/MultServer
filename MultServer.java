package iterativeSocketServer;

import java.io.*;
import java.net.*;

/* CNT 4504 Project 1 : Iterative Socket Server - Server Side
 * Code by Jonathan Shih
 * UNFID N01447401
 * Date 7/19/2021
 * */

public class MultServer { // simulate a multiple-threaded server

	private static Boolean exitServ = false;
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 1) { // check for port number
			System.out.println("Error: Enter a valid port number.");
			System.exit(1);
		}
		
		System.out.println("Running server..."+"\n");
		
		int portNum = Integer.parseInt(args[0]); // read port number
		
		try {
			ServerSocket servSock = new ServerSocket(portNum);
			
			while(exitServ == false) {
				Socket cliSock = servSock.accept(); // new threads
				System.out.println("New client connected...");
				ServerThread st = new ServerThread(cliSock); 
				new Thread(st).start();
			}
			servSock.close();
		}
		catch(IOException e) {
			System.out.println("Error occurred while listening to port "+portNum);
			System.out.println(e.getMessage());
		}
		System.out.println("Goodbye!");
	} // end main
	
	public static class ServerThread implements Runnable { // method to handle multiple threading
		
		private Socket newClient;
		
		public ServerThread(Socket cliSock) { // thread constructor
			newClient = cliSock;		
		}
		
		public void run() { 
			
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(newClient.getInputStream())); // read client input
				PrintWriter out = new PrintWriter(newClient.getOutputStream(), true); // print to console		
				
				String inputLine;
				while((inputLine = in.readLine()) != null) { // read from client
					String reply = parseRequest(inputLine); // parse client input
					System.out.println(reply); // for debugging
					out.println(reply); // reply to client
					out.println("EaZy123"); // signal done to client
				}
			}
			catch (IOException e) {
			// generic IOException handling	
			}
		}

		public void start() {
			// TODO Auto-generated method stub
			
		}
	}
	
	private static String parseRequest(String userInput) { // user input parsed

		switch (userInput.toLowerCase()) {
			
			case "date":
				System.out.println("Date and time requested...");
				return unixCom("date");
			case "uptime":
				System.out.println("Uptime requested...");
				return unixCom("uptime");
			case "mem":
				System.out.println("Memory use requested...");
				return unixCom("cat /proc/meminfo");
			case "netstat":
				System.out.println("Net stats requested...");
				return unixCom("netstat");
			case "users":
				System.out.println("No. of users requested...");
				return unixCom("w");
			case "processes":
				System.out.println("List of processes requested...");
				return unixCom("ps -A");
			case "exit":
				System.out.println("Goodbye!");
				System.exit(0);
			default:
				return "Command not recognized.";
		}// end switch
	} // end ParseRequest
	
	private static String unixCom(String command) { // returns output from UNIX commands
		String output = "";
		String nextLine;
		Process execute;
		
		try {
			execute = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(execute.getInputStream()));
			
			while ((nextLine = reader.readLine()) != null) { // print until last line
				output = output + nextLine + "\n";
			}
			
		}
		catch (IOException e) {
		// generic IOException handling	
		}
		return output;	
	} // end unixCom
	
	@SuppressWarnings("unused")
	private static void exitServer() {
		exitServ = true;
	}
} // end MultServer class