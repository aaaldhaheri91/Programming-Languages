import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.InputMismatchException;
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
public class Test {
	
	//pointer for displaying records and userChoice
	private static int pointer = 0;
	private static String userMenuChoice = "RUN";
	private static int recordNumbers = 1;
	
	
	public static void main(String[] args) throws IOException {
		
		/* declare objects */
		Student record = new Student(); 
		Scanner keyIn = new Scanner(System.in);
		RandomAccessFile randomFile = null;
		
		/* start main menu */
		do{
			char choice;
			
			/* check if record is the size of students */
			if(recordNumbers >= 20)
				recordNumbers = 1;
			
			System.out.println("\nPlease choose from the following:");
			System.out.println("1. Create a raondom access file\n2. Display a random access file\n3. "
								+ "Retrieve a record\n4. Modify a record\n5. Add a new record\n6. Delete a record\n7. Exit");
			choice = keyIn.nextLine().charAt(0);
				
			switch(choice){
				case '1': randomFile = createRandomFile(randomFile, record, keyIn);break;
				case '2': nextScreen(randomFile, record, keyIn);break;
				case '3': retrieve(randomFile, record, keyIn);break;
				case '4': modify(randomFile, record, keyIn);break;
				case '5': add(randomFile, record, keyIn);break;
				case '6': delete(randomFile, record, keyIn);break;
				case '7': userMenuChoice = "EXIT";break;
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
	public static RandomAccessFile createRandomFile(RandomAccessFile randomFile, Student record, Scanner keyIn) throws IOException{
		
		String randomFileName = "database"; 
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
	public static void subMenu(RandomAccessFile raf, Student rec, Scanner keyIn) throws IOException{
		
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
	public static void nextScreen(RandomAccessFile raf, Student rec, Scanner keyIn) throws IOException{
		
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
	 * Retrieves a record and display's the record
	 * @param raf the random access file
	 * @param rec the student record
	 * @param keyIn the scanner to read
	 * @throws IOException
	 */
	public static void retrieve(RandomAccessFile raf, Student rec, Scanner keyIn) throws IOException{
		
		int recordNumber = enterRecord(keyIn);
		
		//clear scanner
		keyIn.nextLine();
		
		//check if record # exist in file
		if(validateRecord(raf, rec, recordNumber) || rec.getfName().toUpperCase().contains("DELETE")){
			System.out.println("Record does not exist in file");
			return;
		}
		
		raf.seek((recordNumber - 1) * rec.size());
		try{
			while(true){
				rec.readFromFile(raf);
				System.out.println(rec);
				break;
			}
		}catch(EOFException e){
			
		}
		
	}//end method
	

	/**
	 * Modifies a record in the random access file
	 * @param raf the random access file
	 * @param rec the student record
	 * @param keyIn the scanner to read
	 * @throws IOException
	 */
	public static void modify(RandomAccessFile raf, Student rec, Scanner keyIn) throws IOException{
		
		int recordNumber = enterRecord(keyIn);
		String fName = null, lName = null; int ID; double GPA;
		//clear scanner
		keyIn.nextLine();
		
		//check if record number exist
		if(validateRecord(raf, rec, recordNumber) || rec.getfName().toUpperCase().contains("DELETE")){
			System.out.println("Record does not exist in file");
			return;
		}
		
		raf.seek((recordNumber - 1) * rec.size());
		rec.readFromFile(raf);
		System.out.println(rec);
		String choice = modifyMenu(keyIn);
		raf.seek((recordNumber - 1) * rec.size());
		
		//check which record to be modified
		if(choice.equalsIgnoreCase("first name")){
			System.out.println("Please enter first name: ");
			fName = keyIn.nextLine();
			rec.setfName(fName, raf);
			
		}
		else if(choice.equalsIgnoreCase("last name")){
			System.out.println("Please enter last name: ");
			lName = keyIn.nextLine();
			rec.setlName(lName, raf);
			
		}
		else if(choice.equalsIgnoreCase("id")){
			System.out.println("Please enter student ID number: ");
			ID = keyIn.nextInt();
			keyIn.nextLine();
			rec.setID(ID, raf);
			
		}
		else if(choice.equalsIgnoreCase("gpa")){
			System.out.println("Please enter student GPA: ");
			GPA = keyIn.nextDouble();
			keyIn.nextLine();
			rec.setGPA(GPA, raf);
			
		}
		else{
			System.out.println("Please Enter first name, last name, student ID, GPA: ");
			rec.readFromTextFile(keyIn);
			keyIn.nextLine();
			rec.writeToFile(raf);
		}

		System.out.println(rec);
		
	}//end method
	
	
	/**
	 * Ask's user what field to modify
	 * @param keyIn scanner to read input
	 * @return user choice
	 */
	public static String modifyMenu(Scanner keyIn){
		
		char choice;
		
		System.out.println("Please choose from following: ");
		System.out.println("A. Modify first name\nB. Modify last name\nC. Modify ID"
				+ "\nD. Modify GPA\nE. Modify all fields");
		choice = keyIn.nextLine().charAt(0);
		
		switch(choice){
			case 'A': case 'a': return "first name";
			case 'B': case 'b': return "last name";
			case 'C': case 'c': return "id";
			case 'D': case 'd': return "gpa";
			case 'E': case 'e': return "all fields";
			default:
				System.out.println("You entered invalid choice, Please try again");
				return modifyMenu(keyIn);
		}
		
	}//end method
	
	
	/**
	 * Adds a record to the random access file
	 * @param raf the random access file
	 * @param rec the student record
	 * @param keyIn scanner to read input
	 * @throws IOException
	 */
	public static void add(RandomAccessFile raf, Student rec, Scanner keyIn) throws IOException{
		
		System.out.println("Please Enter first name, last name, student ID, GPA: ");
		rec.readFromTextFile(keyIn);
		raf.seek(raf.length());
		rec.writeToFile(raf);
		raf.seek(0);
		
		//clear scanner
		keyIn.nextLine();
		
	}//end method

	
	/**
	 * Lazy deletion to a record in random access file
	 * @param raf the random access file
	 * @param rec the student record
	 * @param keyIn the scanner to read
	 * @throws IOException
	 */
	public static void delete(RandomAccessFile raf, Student rec, Scanner keyIn) throws IOException{
		
		int recordNumber = enterRecord(keyIn);
		
		//clear scanner
		keyIn.nextLine();
		
		/* check if record exist */
		if(validateRecord(raf, rec, recordNumber)){
			System.out.println("Record does not exist in file");
			return;
		}
		raf.seek((recordNumber - 1) * rec.size());
		rec.readFromFile(raf);
		
		//check if record has been deleted
		if(rec.getfName().toUpperCase().contains("DELETE")){
			System.out.println("Record does not exist in file");
			return;
		}
		
		raf.seek((recordNumber - 1) * rec.size());
		rec.readFromFile(raf);
		raf.seek((recordNumber - 1) * rec.size());
		rec.setfName("DELETE", raf);
		
	}//end method
	
	
	/**
	 * ask user for record number and reads it
	 * @param keyIn the scanner to read
	 * @throws IOException 
	 */
	public static int enterRecord(Scanner keyIn){
		
		try{
			System.out.println("Please enter record number: ");
			return keyIn.nextInt();
		}catch(InputMismatchException e){
			return -1;
		}
	}//end method
	
	
	/**
	 * Validates user record input
	 * @param raf the random access file
	 * @param rec the student record
	 * @param recordNumber the record number
	 * @throws IOException 
	 */
	public static boolean validateRecord(RandomAccessFile raf, Student rec, int recordNumber) throws IOException{
		
		return (recordNumber - 1) * rec.size() > raf.length();
	}
}//end class
