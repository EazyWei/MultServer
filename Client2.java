package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/* CNT 4504 Project 1 : Iterative Socket Server - Client Side
 * Code by Jonathan Shih
 * UNFID N01447401
 * Date 7/19/2021
 * */

public class Client2 {
	
	static ArrayList<RequestThread> threads = new ArrayList<RequestThread>(); // container for threads
	
	public static void main(String[] args) throws IOException { // begin main

		try {
		InetAddress hostIPAddr = InetAddress.getByName("CNT4505D.ccec.unf.edu"); // get IP address
		
			if (args.length != 1) { // check for port number
			System.out.println("Error: Please enter a valid port number as argument.");
			System.exit(1);
			}
		
		int portNum = Integer.parseInt(args[0]); // get port number

		try (Socket newSock = new Socket(hostIPAddr, portNum); // connect sockets
				PrintWriter output = new PrintWriter(newSock.getOutputStream(), true);
				BufferedReader input = new BufferedReader(new InputStreamReader(newSock.getInputStream()));			
				BufferedReader sysIn = new BufferedReader(new InputStreamReader(System.in));
				) {
			
			String cliMsg; // holds client request
			int reqCount = 0; // holds number requested 
			
			while(true) { // loop main menu

				System.out.println("Hello. Please enter a command. Your choices are: date and time, uptime, memory use, netstat,"
						+ " current users, processes, or exit (to quit)."); // menu choices

				cliMsg = sysIn.readLine().toLowerCase(); // read command string
				
				if (cliMsg.equals("exit")) { // exit case
					System.out.println("Goodbye!"+"\n");
					output.println("exit");
					System.exit(0);
				}
				
				System.out.println("How many client requests would you like to generate? Options are 1, 5, 10, 15, 20, 25, and 100.");
				
				try { reqCount = Integer.parseInt(sysIn.readLine()); // read integer and error check user input
				
					if (cliMsg.equals("date and time") || cliMsg.equals("uptime") || cliMsg.equals("memory use") || cliMsg.equals("netstat") || cliMsg.equals("current users") ||
								cliMsg.equals("processes") || cliMsg.equals("exit")) {
						}
						else {
							throw new Exception();   
						}
						if(reqCount > 100 || reqCount < 1) { 
							throw new Exception();
						}
					}
				catch(Exception e) {
					System.out.println("Please enter a valid command and an integer value between 1 and 100."+"\n"); // error message
					continue;
				}
					
				threads.clear(); // initialize array to null
					
				for(int i = 0; i<reqCount; i++) { // create number of request threads
					RequestThread r = new RequestThread(cliMsg, output, input);
					threads.add(r);
					r.start();
				}
				
				while(true) { // calculating turn around times
					if(checkAllThreadsComplete()) { // if all threads complete
						long totalms = 0;
						double avgms = 0;
						int numThreads = threads.size();
						
							for(RequestThread r : threads) {
								totalms += r.getTotalTime(); // get cumulative times
							}
							
						avgms = ( (double)totalms / (double)numThreads ); // calculate average time
						
						System.out.println("Total turn around time is: "+totalms+" ms"); // print times
						System.out.println("Average turn around time is: "+avgms+" ms"+"\n");
						break;
					}
					else {
						wait(1); // wait if still working
						continue;
					}
				}	
				
				if (cliMsg.equals("exit")) { // end loop
					break;
				}
			}
		}
		catch (UnknownHostException e) {
			System.err.println("Can't locate host "+hostIPAddr);
            System.exit(1);
	    }
		catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to "+hostIPAddr);
            e.printStackTrace();
            System.exit(1);
	    }
		}catch (Exception e){
			e.printStackTrace();
		}


	} // end main
	
	public static void wait(int ms) { // thread waits
	    try {
	        Thread.sleep(ms);
	    }
	    catch(InterruptedException ex) {
	        Thread.currentThread().interrupt();
	    }
	}
	
	public static Boolean checkAllThreadsComplete() { // checks if all threads have executed
		Boolean result = true;
		for(int i = 0; i<threads.size(); i++) {
			if(threads.get(i).getComplete() == false) { // if not complete return false
				result = false;
			}
		}
		return result;
	}

	} // end main
	
	class RequestThread implements Runnable { // threads for client requests
		
		private Thread t;
		private String command;
		private PrintWriter output;
		private BufferedReader input;
		private Boolean complete = false;
		private long totalTime = 0l;
		   
		RequestThread( String command, PrintWriter output, BufferedReader input) { // thread constructor
			this.command = command;
			this.output = output;
			this.input = input;
		}
		
		public long getTotalTime() { // timer
			return totalTime;
		}   
		
		public Boolean getComplete() { // checks complete
			return complete;
		}
		   
		public void run() {		
		}
		
		public void start () {
	      
			long startTime = System.nanoTime(); // start timer
		      
		    if (sendRequest(command)) { // send request to server
		    	String result;
				try {
					while((result = input.readLine()) != null) {
						if (result.equals("EaZy123")) { // server signals done
							long endTime = System.nanoTime(); // end timer
							long timeElapsed = ( endTime - startTime ) / 1000000; // calculate time
							System.out.println("\n"+"Turn around time is: "+timeElapsed+" ms"+"\n"); // print turn around time
							totalTime = timeElapsed;
							complete = true;
							break;
						}
						System.out.println(result);
						}
					} 
				catch (IOException e) {
						e.printStackTrace();
				}
		    }	 	      
		    if (t == null) {
		    	t = new Thread (this, command);
		        t.start();
		    }	    
		}

	private Boolean sendRequest(String userInput) { // user input protocol
	Boolean command = true;
		
		switch (userInput.toLowerCase()) {
			
			case "date and time":
				output.println("date");
				break;
			case "uptime":
				output.println("uptime");
				break;
			case "memory use":
				output.println("mem");
				break;
			case "netstat":
				output.println("netstat");
				break;
			case "current users":
				output.println("users");
				break;
			case "processes":
				output.println("processes");
				break;
			case "exit":
				System.out.println("Goodbye!"+"\n");
				command = false;
				System.exit(0);
			default:
				System.out.println("Command not recognized."+"\n");
				command = false;
				break;
		} // end switch
	return command;
	} // end SendRequest

	
} // end RequestThread class
	
	