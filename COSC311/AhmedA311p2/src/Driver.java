import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

/**
 * Read the students' info from a text file and write them to a random access file 
 * and then perform a few simple tasks using the random access file
 * @author 	Ahmed Aldhaheri
 * @date: 	10/16/2016
 * @course:	COSC311 
 */

public class Driver {

	//pointer for displaying records and userChoice
	private static int pointer = 0;
	private static String userMenuChoice = "RUN";
	private static int recordNumbers = 1;
	private static int rafSize = 0;
	
	
public static void main(String[] args) throws IOException {
		
		/* declare objects */
		Student record = new Student(); 
		Scanner keyIn = new Scanner(System.in);
		RandomAccessFile randomFile = null;
		OrderedList<Pair<Integer>> list = null;
		
		/* start main menu */
		do{
			char choice;
			
			/* check if record is the size of students */
			if(recordNumbers >= rafSize)
				recordNumbers = 1;
			
			System.out.println("\nPlease choose from the following:");
			System.out.println("1. Make a raondom-access file\n2. Display a random-access file\n3. "
								+ "Build the index\n4. Display the index"
								+ "\n5. Retrieve a record\n6. Modify a record\n7. Add a new record\n"
								+ "8. Delete a record\n9. Exit");
			choice = keyIn.nextLine().charAt(0);
				
			switch(choice){
				case '1': randomFile = createRandomFile(randomFile, record, keyIn);break;
				case '2': nextScreen(randomFile, record, keyIn);break;
				case '3': list = buildIndex(keyIn);break;
				case '4': displayIndex(list, keyIn);break;
				case '5': retrieve(list, keyIn, record, randomFile);break;
				case '6': modify(list, keyIn, record, randomFile);break;
				case '7': add(list, keyIn, record, randomFile);break;
				case '8': delete(list, keyIn, record, randomFile);break;
				case '9': userMenuChoice = "EXIT";break;
				default: 
					System.out.println("You entered invalid choice, Please try again");
					break;
			}
			
		}while(!userMenuChoice.equalsIgnoreCase("EXIT"));
		
		randomFile.close();
		keyIn.close();
	}//end main
	
	/**
	 * Creates Random Access File
	 * @param fin the input file
	 * @return randomFile the random access file
	 * @throws IOException 
	 */
	public static RandomAccessFile createRandomFile(RandomAccessFile randomFile, 
			Student record, Scanner keyIn) throws IOException{
		
		String randomFileName = "database.txt"; 
		Scanner inputFile = null;
		randomFile = new RandomAccessFile(randomFileName, "rw");
			
		//check if randomFile exist
		if(exist(randomFileName))
			randomFile.setLength(0);
		
		//ask user for input file
		inputFile = readUserFileEntry(inputFile, keyIn);

		//read data from input file and write it to a random access file
		while(inputFile.hasNext()){
			record.readFromTextFile(inputFile);
			record.writeToFile(randomFile);
		}
		
		rafSize = (int) randomFile.length() / record.size();
		inputFile.close();
		return randomFile;
	}
	
	
	/**
	 * Ask's user for input file name
	 * @param fin where to store the input file
	 * @param keyIn read user input
	 * @return fin the input file name
	 * @throws IOException
	 */
	public static Scanner readUserFileEntry(Scanner fin, Scanner keyIn)throws IOException{
		
		//boolean variable for correct input
		boolean fileFound = false;
		while(!fileFound){
			try{
				System.out.println("Please enter input text file name: ");
				String temp = keyIn.nextLine();
				
				//check if extension entered
				if(!temp.contains(".txt"))
					temp += ".txt";
				
				//search for file directory and get path
				temp = getFilePath(temp);
				fin = new Scanner(new FileInputStream(temp));
				fileFound = true;
			}catch(FileNotFoundException e){
				System.out.println("File not found, try again");
				continue;
			}
		}//end while loop
		
		return fin;
	}//end method
	
	
	/**
	 * Searches for the text file name in the directory and sub directories
	 * @param fileName the text file name
	 * @return fileName the filePath
	 */
	public static String getFilePath(String	fileName){
		
		//define the root path as path of project
		File root = new File("C:\\Users\\ahmed\\Downloads\\COSC 311\\AhmedA311p2");
		Collection files = FileUtils.listFiles(root, null, true);
		
		for(Iterator iterator = files.iterator(); iterator.hasNext();){
			File file = (File)iterator.next();
			if(file.getName().equals(fileName))
				fileName = file.getAbsolutePath();
		}
		
		return fileName;
	}//end method

	
	/**
	 * checks if random access file exist or not
	 * @param rafName the random access file name
	 * @return true if file exist, false otherwise
	 */
	public static boolean exist(String rafName){
		
		//file root
		File root = new File("C:\\Users\\ahmed\\Downloads\\COSC 311\\AhmedA311p1");
		Collection files = FileUtils.listFiles(root, null, true);
		
		for(Iterator iterator = files.iterator(); iterator.hasNext();){
			File file = (File)iterator.next();
			if(file.getName().equals(rafName))
				return true;		
		}
		
		return false;
	}//end method
	
	
	
	
	/**
	 * Displays sub menu and ask user to choose an option
	 * @param raf the random access file
	 * @param rec the student record
	 * @param keyIn the scanner to read input
	 * @throws IOException 
	 */
	public static void subMenu(RandomAccessFile raf, 
			Student rec, Scanner keyIn) throws IOException{
		
		char choice;
		System.out.println();
		System.out.println("\nPlease choose from the following: ");
		System.out.println("M. Return to main menu\nN. Next screen\nA. Display all");
		choice = keyIn.nextLine().charAt(0);
		
		switch(choice){
			case 'M': case 'm': break;
			case 'N': case 'n': nextScreen(raf, rec, keyIn);break;
			case 'A': case 'a': displayAll(raf, rec);break;
			default: 
				System.out.println("You entered invalid choice, Please try again");
				subMenu(raf, rec, keyIn);
		}
		
		
	}//end method
	
	
	/**
	 * Display 5 students' records neatly on the screen
	 * @param raf the random access file
	 * @param rec the student record
	 * @param keyIn the scanner to read
	 * @throws IOException
	 */
	public static void nextScreen(RandomAccessFile raf, 
			Student rec, Scanner keyIn) throws IOException{
		
		int i = 0;
		/* check if all records have been displayed */
		if((pointer * rec.size()) >= raf.length()){
			System.out.println("All records have been displayed");
			pointer = 0;
			return;
		}
		
		raf.seek(pointer * rec.size());
		try {
			while (true){
				rec.readFromFile(raf);
				if(!rec.getfName().toUpperCase().contains("DELETE")){
					i++;
					System.out.println(recordNumbers + ".\t" + rec);
					recordNumbers++;
				}
				/* increment pointer to skip deleted record */
				else
					pointer++;
				/* if printed 5 than break */
				if(i != 0 && i % 5 == 0)
					break;
			}//end while
		}
		catch (EOFException e){
			
		}
		pointer += rec.printScreen();
		
		/*adjust record numbers display */
		if(recordNumbers > raf.length() / rec.size())
			recordNumbers = 1;
		
		subMenu(raf, rec, keyIn);
	}//end method
	
	
	/**
	 * Displays all the data in students' records neatly on the screen
	 * @param raf the random access file
	 * @param rec the student record
	 * @throws IOException
	 */
	public static void displayAll(RandomAccessFile raf, Student rec) throws IOException{
		
		raf.seek(pointer * rec.size());
		try {
			while (true){
				rec.readFromFile(raf);
				if(!rec.getfName().toUpperCase().contains("DELETE")){
					System.out.println(recordNumbers + ".\t" + rec);
					recordNumbers++;
				}
				
			}//end while
		}
		catch (EOFException e){
			
		}
		
		/* reset file pointer */
		pointer = 0;
		
	}//end method
	
	/**
	 * Asks user for database name, reads the database
	 * record sequentially and creates an ordered singly
	 * linked list. The list will be ordered based on 
	 * students ID. linked list will contain pair(key, address).
	 * key is the student ID, address is the position of the
	 * record containing the ID in the database
	 * and a reference to a node.
	 * @param keyIn the Scanner 
	 * @throws IOException 
	 */
	public static OrderedList buildIndex(Scanner keyIn) throws IOException{
		/* declare objects */
		RandomAccessFile database = null;
		Student record = new Student();
		OrderedList<Pair<Integer>> list = new OrderedList<Pair<Integer>>();
		
		/* Ask user for datatbase */
		System.out.println("Enter a database name: ");
		String databaseName = keyIn.nextLine();
		
		if(!databaseName.contains(".txt"))
			databaseName += ".txt";
		
		/* check if database exist */
		if(!exist(databaseName))
			return null;
		
		/* get file path */
		databaseName = getFilePath(databaseName);
		
		/* create random access file */
		database = new RandomAccessFile(databaseName, "rw"); 
		
		
		
		/* read database and build linked list */
		database.seek(0);
		int i = 0;
		try{
			while(true){
				record.readFromFile(database);
				list.add(new Pair<Integer>(record.getID(), i));
				i++;
			}
		}catch(EOFException e){
			
		}
		
		return list;
		
	}
	
	/**
	 * Asks the user whether the like to see entire index 
	 * or part of the index. calls helper methods based
	 * on user choice.
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 */
	public static void displayIndex(OrderedList<Pair<Integer>> list, Scanner keyIn ){
		int choice;
		/* Ask user for selection to display list */
		System.out.println("Choose from the following: ");
		System.out.println("1. Entire index\n2. Part of index");
		choice = keyIn.nextInt();
		keyIn.nextLine();
		
		switch(choice){
		case 1:displayAllIndex(list);break;
		case 2:searchIndex(list, keyIn);break;
		default:System.out.println("Invlaid choice, try again");
				displayIndex(list, keyIn);
				break;
		}
		
	}
	
	/**
	 * helper method for displayIndex to display all index
	 * @param list the linkedlist
	 */
	public static void displayAllIndex(OrderedList<Pair<Integer>> list){
		/* declare iterator and implement */
		Iterator<Pair<Integer>> itr = list.iterator();
		
		/* display all data in linkedlist */
		while(itr.hasNext())
			System.out.println(itr.next());
		
	}
	
	/**
	 * helper method for displayIndex to display part of index
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 */
	public static void searchIndex(OrderedList<Pair<Integer>> list, Scanner keyIn){
		Pair<Integer> found = searchPair(list, keyIn);
		
		/* see if ID exist */
		if(pairExist(found))
			return;
		
		/* display starting from key */
		Pair<Integer> temp = null;
		Iterator<Pair<Integer>> itr = list.iterator();
		while(itr.hasNext()){
			temp = itr.next();
			if(temp.getAddress() == found.getAddress())
				break;
		}
		while(itr.hasNext()){
			System.out.println(temp);
			temp = itr.next();
		}
		System.out.println(temp);	
	}
	
	/**
	 * Ask's the user for record to retrieve and displays it
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 * @param record the student record
	 * @throws IOException 
	 */
	public static void retrieve(OrderedList<Pair<Integer>> list, Scanner keyIn, 
			Student record, RandomAccessFile raf) throws IOException{
		Pair<Integer> keyPair = searchPair(list, keyIn);
		
		/* see if ID exist */
		if(pairExist(keyPair))
			return;
		
		/* get student record */
		record = searchRecord(keyPair.getAddress(), raf, record);
		
		/* display record */
		System.out.println(record);
	}
	
	/**
	 * Modifies a record in a database
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 * @param record the student record
	 * @throws IOExcepiton
	 */
	public static void modify(OrderedList<Pair<Integer>> list, Scanner keyIn, 
			Student record, RandomAccessFile raf) throws IOException{
		
		String fName = null, lName = null; double GPA;
		
		/* ask user for ID */
		Pair<Integer> keyPair = searchPair(list, keyIn);
		
		/* check if ID exists */
		if(pairExist(keyPair))
			return;
		
		/* get student record */
		record = searchRecord(keyPair.getAddress(), raf, record);
		System.out.println(record);
		String choice = modifyMenu(keyIn);
		
		/* check which record to be modified */
		raf.seek(keyPair.getAddress() * record.size());
		if(choice.equalsIgnoreCase("first name")){
			System.out.println("Please enter first name: ");
			fName = keyIn.nextLine();
			record.setfName(fName, raf);
			
		}
		else if(choice.equalsIgnoreCase("last name")){
			System.out.println("Please enter last name: ");
			lName = keyIn.nextLine();
			record.setlName(lName, raf);
			
		}
		else if(choice.equalsIgnoreCase("gpa")){
			System.out.println("Please enter student GPA: ");
			GPA = keyIn.nextDouble();
			keyIn.nextLine();
			record.setGPA(GPA, raf);
		}
		else{
			System.out.println("Please Enter first name, last name, GPA: ");
			record.readFromFileNoID(keyIn);
			keyIn.nextLine();
			record.writeToFile(raf);
		}
		
		System.out.println(record);
		
	}
	
	/**
	 * Ask's user what field to modify
	 * @param keyIn scanner to read input
	 * @return user choice
	 */
	public static String modifyMenu(Scanner keyIn){
		
		char choice;
		
		System.out.println("Please choose from following: ");
		System.out.println("A. Modify first name\nB. Modify last name"
				+ "\nC. Modify GPA\nD. Modify all fields");
		choice = keyIn.nextLine().charAt(0);
		
		switch(choice){
			case 'A': case 'a': return "first name";
			case 'B': case 'b': return "last name";
			case 'C': case 'c': return "gpa";
			case 'D': case 'd': return "all fields";
			default:
				System.out.println("You entered invalid choice, Please try again");
				return modifyMenu(keyIn);
		}
		
	}
	
	/**
	 * Adds a new record to database and adds 
	 * student ID and address to linkedlist
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 * @param record the student record
	 * @param raf the random access file
	 * @throws IOExcepiton
	 */
	public static void add(OrderedList<Pair<Integer>> list, Scanner keyIn, 
			Student record, RandomAccessFile raf) throws IOException{
		
		System.out.println("Please Enter first name, last name, student ID, GPA: ");
		record.readFromTextFile(keyIn);
		raf.seek(raf.length());
		record.writeToFile(raf);
		list.add(new Pair<Integer>(record.getID(), (int) ((raf.length() / record.size()) - 1)));
		rafSize++;
		//clear scanner
		keyIn.nextLine();
		
	}
	
	/**
	 *Deletes a record from database and linkedlist
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 * @param record the student record
	 * @throws IOExcepiton
	 */
	public static void delete(OrderedList<Pair<Integer>> list, Scanner keyIn, 
			Student record, RandomAccessFile raf) throws IOException{
		
		/* ask user for ID */
		Pair<Integer> keyPair = searchPair(list, keyIn);
		
		/* check if ID exists */
		if(pairExist(keyPair))
			return;
		
		/* get student record and delete */
		record = searchRecord(keyPair.getAddress(), raf, record);
		raf.seek(keyPair.getAddress() * record.size());
		record.setfName("DELETE", raf);
		rafSize--;
		/* delete from linkedlist index */
		list.remove(keyPair);
	}
	
	/**
	 * Finds if pair exist or not
	 * @param list the linkedlist
	 * @param keyIn the scanner
	 * @return item the Pair
	 */
	public static Pair<Integer> searchPair(OrderedList<Pair<Integer>> list, Scanner keyIn){
		
		/* ask user for key and use find method to find it */
		System.out.println("Enter ID: ");
		int key = keyIn.nextInt();
		keyIn.nextLine();
		Pair<Integer> keyPair = new Pair<Integer>(key, 0);
		keyPair = list.find(keyPair);
		return keyPair;
	}
	
	/**
	 * Searches for address of a record in database and returns it
	 * @param address the address of record
	 * @param rad the random access file
	 * @param record the student record
	 * @return record the student record
	 * @throws IOException 
	 */
	public static Student searchRecord(int address, RandomAccessFile raf, 
			Student record) throws IOException{
		
		raf.seek(address * record.size());
		record.readFromFile(raf);
		
		return record;
	}
	
	/**
	 * Checks if ID exist database
	 * @param keyPair the pair contains ID
	 * @return wheather it exists or not
	 */
	public static boolean pairExist(Pair<Integer> keyPair){
		
		if(keyPair == null)
			System.out.println("ID does not exist");
		return keyPair == null;
	}
}
