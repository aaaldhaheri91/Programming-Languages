import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class Driver {

	//average wait time
	private static double totalWaitTime = 0;
	private static double totalCustomers = 1;
	
	public static void main(String[] args) {
		
		//initialize queue, Scanner, Poisson and variables
		ListQueue<Event> priorityQueue;
		ArrayQueue<Event> waitQueue = null;
		Poisson poisson = null;
		Scanner keyIn = new Scanner(System.in);
		String fileName = "report.txt";
		
		//user input
		char userChoice = 'Y';
		
		//id and length of simulation,
		int id = 1234; int length, i = 0;	
		//average time between dial-ins attempts and percentage time modems are busy
		double dialInAvg, percentageTime;
		//average connection time
		int connectionAvgTime;
		//number of modems in the bank
		int modems;
		//size of waiting queue
		int waitSize;
		
		//check if report file already exist and delete it
		deleteFile(fileName);
		
		//start simulation
		do{
			
			//clear priority queue for next simulation
			priorityQueue = null;
			
			try{
				//ask user for input
				System.out.println("Enter length of simulation: ");
				length = keyIn.nextInt();
				
				System.out.println("Enter average time between dial-in attempts: ");
				dialInAvg = keyIn.nextDouble();
				
				
				System.out.println("Enter average connection time: ");
				connectionAvgTime = keyIn.nextInt();
				
				System.out.println("Enter number of modems in bank: ");
				modems = keyIn.nextInt();
				
				System.out.println("Enter size of waiting queue: ");
				waitSize = keyIn.nextInt();
			
			}catch(InputMismatchException e){
				 
				System.out.println("Invalid input, please try again\n");
				keyIn.nextLine();
				continue;
				
			}
			
			//clear scanner
			keyIn.nextLine();
			
			//assign size to wait queue
			waitQueue = new ArrayQueue<Event>(waitSize);
			
  			//initialize priority queue
			priorityQueue = new ListQueue<Event>();
			priorityQueue = initailizeEventQueue(priorityQueue, poisson, dialInAvg, length);
			
			
			//start simulation
			percentageTime = simulation(priorityQueue, poisson, waitQueue, connectionAvgTime, length, modems, waitSize);
			
			System.out.println("Percentage of time modems were busy: " + String.format("%.2f", percentageTime) + "%");
			System.out.println("Average wait time: " + String.format("%.2f", totalWaitTime / totalCustomers) + "\n");
			
			//ask if another simulation is desired
			System.out.println("Run simulation again (y) for yes or (n) for no");
			userChoice = keyIn.nextLine().charAt(0);
			
			//write report to file
			writeReport(fileName, userChoice, i, percentageTime, waitQueue);
			
			i++;
			
		}while(userChoice == 'y' || userChoice == 'Y');
			
		System.out.println("\nSimulation is complete: summary in file report.txt");
		
		
	}//end main
	
	
	/**
	 * Initializes priority queue (Event queue)
	 * @param listQueue the linkedlist priority queue
	 * @param poisson the poisson distribution 
	 * @param average the average time between dial-in attempts
	 * @param length the length of simulation
	 * @return listQueue the initialized priority queue
	 */
	public static ListQueue initailizeEventQueue(ListQueue<Event> listQueue, Poisson poisson, double average, int length){
		
		//initialize id
		int id = 1234;
		poisson = new Poisson(average);
		
		for(int i = 1; i <= length; i++){
			
			int eventsPerUnitTime = poisson.nextInt();
			for(int j = 1; j <= eventsPerUnitTime; j++){
				listQueue.offer(new Event(id++, i, "dial-in"));
			}
			
		}
		
		return listQueue;
		
	}//end method
	
	 
	/**
	  * Starts simulation. Takes Events by unit time and assigns them to modem.
	  * If no modem available it puts event in waiting queue.
	  * If waiting queue is full it discards event
	  * @param listQueue the linkedlist priority queue
	  * @param waitQueue the wait queue
	  * @param poisson the poisson distribution 
	  * @param average the average connection time
	  * @param length the length of simulation
	  * @param modems the number of modems bank has
	  * @param waitSize the waiting queue size
	  * @return the percentage time modems was busy
	  */
	public static double simulation(ListQueue<Event> listQueue, Poisson poisson, ArrayQueue<Event> waitQueue, int average, int length, int modems, int waitSize){
		
		final int temp = modems;	//temp variable to check if any modems are busy for percentage
		double totalBusyTime = 0;  //total time modems are busy
		poisson = new Poisson(average);
		
		for(int i = 1; i <= length; i++){
			
			do{
				if(eventExist(listQueue.peek(), i)){
					
					//get the connection time
					int hangUpTime = poisson.nextInt();
					
					//check if modems available
					if(modems > 0){
						
						//add connection time to totalBusyTime
						totalBusyTime += hangUpTime;
						
						//add event time to connection time to get hang up time
						hangUpTime += i;
						modems = availableModem(listQueue, waitQueue, modems, hangUpTime, i);
						
					}//end modems > 0
					
					//no modems available
					else{
						
						modems = noModems(listQueue, waitQueue, modems, hangUpTime, i, waitSize);
					
					}//end no modems else
				
				}//end if(eventExist(listQueue.peek(), i)
				
				
				//no events at unit time i
				else
					break;
				
			}while(true);
			
			
		}//end for loop
		
		
		return Math.round((totalBusyTime / (length * temp)) * 100);
		
	}//end method
	
	
	/**
	 * if modems available assigns a modem to a customer
	 * @param listQueue the linkedlist priority queue
	 * @param waitQueue the wait queue
	 * @param modems the modems available
	 * @param hangUpTime the unit time where hang up should be added
	 * @param currentTime the current time simulation is at
	 * @return modems the number of modems available left
	 */
	public static int availableModem(ListQueue<Event> listQueue, ArrayQueue<Event> waitQueue, int modems, int hangUpTime, int currentTime){
		
		
		//check if anyone in wait queue
		if(waitQueue.size() == 0){
			
			//check if dial-in
			if(dialIn(listQueue.peek())){
				
				//assign modem and add hangup event
				addHangUp(listQueue, hangUpTime);
				modems--;
			}
			
			//if hang up event
			else{
				
				//remove hang up
				listQueue.poll();
				modems++;
			}
			
		}//end waitQueue.size() == 0
		
		//wait queue has events
		else{
			
			//assign modem and add hangup event, remove event from wait
			addHangUp(listQueue, waitQueue, hangUpTime, currentTime);
			modems--;
			
		}
		
		
		return modems;
	}
	
	
	/**
	 * if no modems available adds customer into wait queue
	 * @param listQueue the linkedlist priority queue
	 * @param waitQueue the wait queue
	 * @param modems the modems available
	 * @param hangUpTime the unit time where hang up should be added
	 * @param currentTime the current time simulation is at
	 * @param waitSize the wait queue size
	 * @return modems the number of modems available left
	 */
	public static int noModems(ListQueue<Event> listQueue, ArrayQueue<Event> waitQueue, int modems, int hangUpTime, int currentTime, int waitSize){
		
		//check if dial-in
		if(dialIn(listQueue.peek())){
			
			//add to wait queue
			if(waitQueue.size() < waitSize){
				
				Event tempEvent = listQueue.poll();
				waitQueue.offer(tempEvent);
			}
			
			//if wait queue is full
			else{
				System.out.println("The following event will not be served: " + listQueue.poll());
			}
		
		}//end if dialIn(listQueue.peek()
		
		//if hang up event remove
		else{
		
			listQueue.poll();
			modems++;
		}
		
		return modems;
	}
	
	/**
	 * removes current event and adds a hang up event in priority queue
	 * @param listQueue the linkedlist priority queue
	 * @param hangUpTime the unit time where hang up should be added
	 */
	public static void addHangUp(ListQueue<Event> listQueue, int hangUpTime){
		
		//remove event and save it
		Event hangUp = listQueue.poll();
		//add poisson number to unit time
		hangUp.setTime(hangUpTime);
		
		//set type to hang up
		hangUp.setType("hang-up");
		
		//add hang up in the specified unit time
		listQueue.offer(hangUpTime, hangUp);
		
	}//end method
	
	
	/**
	 * removes current event from wait queue and adds a hang up event in priority queue
	 * @param listQueue the linkedlist priority queue
	 * @param waitQueue the waiting queue
	 * @param hangUpTime the unit time where hang up should be added
	 * @param currentTime the unit time current simulation is at
	 */
	public static void addHangUp(ListQueue<Event> listQueue, ArrayQueue<Event> waitQueue, int hangUpTime, int currentTime){
		
		//remove event and save it
		Event hangUp = waitQueue.poll();
		
		//get the customer wait time
		waitTime(hangUp, currentTime);
		
		//add poisson number to current time to assign a hang up event
		hangUp.setTime(hangUpTime);
		
		//change type to hang up
		hangUp.setType("hang-up");
		
		//add hang up in the specified unit time
		listQueue.offer(hangUpTime, hangUp);
		
		//add to total customers in wait queue that been served for average wait time
		totalCustomers++;
		
		
	}//end method
	
	
	/**
	 * calculates the wait time of indiviual customers and adds it total time
	 * @param Event the event being kicked out of wait queue 
	 * @param currentTime the current simulation time
	 *  
	 */
	public static void waitTime(Event event, int currentTime){
		totalWaitTime += (currentTime - event.getTime());
	}
	
	
	/**
	 * writes simulation report to file
	 * @param fileName the output file name
	 * @param userChoice the user input
	 * @param index the simulation number
	 * @param percentageTime the percentage unit time modems were busy
	 * @param waitQueue the wait queue
	 */
	public static void writeReport(String fileName, char userChoice, int index, double percentageTime, ArrayQueue<Event> waitQueue){
		
		//create report file and write simulation report to it
		try{
			FileWriter fw = new FileWriter(fileName, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw);
			
			//check if first simulation write column headers
			if(index == 0){
				out.println("Busy Time %\t\tAverage Wait\t\tUsers Waiting");
			}
			
			//write simulation report to file
			out.println((index + 1) + ". " + String.format("%.2f", percentageTime) + "\t\t\t" + String.format("%.2f", totalWaitTime / totalCustomers)
							+ "\t\t\t" + waitQueue.size());
			
			//check if user quitting to write trailer sectionf
			if(Character.toUpperCase(userChoice) == 'N'){
				out.println("");
				out.println("Busy Time: the percentage modems were busy through each simulation");
				out.println("Average Wait: the average unit time wait customers were put in wait queue");
				out.println("Users Waiting: the number of customers remained in wait queue when simulation is done");
			}
			
			out.close();
		
		}catch(IOException e){
			
			System.out.println(e.getMessage());
		
		}
		
	}//end method
	
	
	/**
	 * Searches for the text file name in the directory and sub directories
	 * @param fileName the text file name
	 * @return fileName the filePath
	 */
	public static String getFilePath(String	fileName){
		
		//define the root path as path of project
		File root = new File("C:\\Users\\ahmed\\Downloads\\COSC 311\\AhmedA311p1");
		Collection files = FileUtils.listFiles(root, null, true);
		
		for(Iterator iterator = files.iterator(); iterator.hasNext();){
			File file = (File)iterator.next();
			if(file.getName().equals(fileName))
				fileName = file.getAbsolutePath();
		}
		
		return fileName;
	}//end method
	
	
	/**
	 * Checks if report file exists and deletes it
	 * @param fileName the file name
	 */
	public static void deleteFile(String fileName){
		
		//check if report file exist
		if(exist(fileName)){
					
			//get path of report file
			String temp = getFilePath(fileName);
					
			//delete existing report file
			try{
						
				File tempFile = new File(temp);
				tempFile.delete();
						
			}catch(SecurityException e){
						
				System.out.println(e.getMessage());
						
			}
					
		}
		
	}//end method
	
	
	/**
	 * checks if event is dial in or hang up
	 * @param event the event 
	 * @return true if dial in, false if hang up
	 */
	public static boolean dialIn(Event event){
		
		if(event.getType().compareTo("dial-in") == 0)
			return true;
		
		return false;
	}//end method
	
	
	/**
	 * checks if event exist at a specified unit time
	 * @param event the event
	 * @param index the unit time 
	 * @return true if event exist, false if not
	 */
	public static boolean eventExist(Event event, int index){
		
		if(event != null && event.getTime() == index)
			return true;
		
		return false;
	}//end method
	
	
	/**
	 * checks if report file exist or not
	 * @param rafName the random access file name
	 * @return true if file exist, false otherwise
	 */
	public static boolean exist(String fileName){
		
		//file root
		File root = new File("C:\\Users\\ahmed\\Downloads\\COSC 311\\AhmedA311p3");
		Collection files = FileUtils.listFiles(root, null, true);
		
		for(Iterator iterator = files.iterator(); iterator.hasNext();){
			File file = (File)iterator.next();
			if(file.getName().equals(fileName))
				return true;		
		}
		
		return false;
	}//end method
}
