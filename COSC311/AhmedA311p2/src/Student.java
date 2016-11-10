
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Scanner;

/** Class for representing a student  */
public class Student {
	
	/** A student has a first name (fName), last name (lName),
	 * an id (ID), and a gpa (GPA).
	 */	 
	private final int LENGTH = 20;
	private final int SCREEN_PRINT = 5;
	private String fName;
	private String lName;
	private int ID;
	private double GPA;
	private final int RECSIZE = 92;
	
	/**
	 * Set the student info
	 * @param fName the first name
	 * @param lName the last name
	 * @param ID the ID
	 * @param GPA the GPA
	 */
	public void setData (String fName, String lName, int ID, double GPA){
		this.fName = pad(fName, LENGTH); this.lName = pad(lName, LENGTH); this.ID = ID; this.GPA = GPA;
	}
	
	/**
	 * Sets student first name
	 * @param fName the first name
	 * @param raf random access file
	 * @throws IOException 
	 */
	public void setfName(String fName, RandomAccessFile raf) throws IOException{
		this.fName = pad(fName, LENGTH);
		writeToFile(raf);
	}
	
	/**
	 * gets student first name
	 *@return student first name 
	 */
	public String getfName(){
		return this.fName;
	}
	
	/**
	 * Sets student last name
	 * @param lName the last name
	 * @param raf random access file
	 * @throws IOException 
	 */
	public void setlName(String lName, RandomAccessFile raf) throws IOException{
		this.lName = pad(lName, LENGTH);
		writeToFile(raf);
	}
	
	/**
	 * Sets student ID name
	 * @param ID the ID
	 * @param raf random access file
	 * @throws IOException 
	 */
	public void setID(int ID, RandomAccessFile raf) throws IOException{
		this.ID = ID;
		writeToFile(raf);
		
	}
	
	/**
	 * @return student ID
	 */
	public int getID(){
		return this.ID;
	}
	
	/**
	 * Sets student GPA
	 * @param GPA the GPA
	 * @param raf random access file
	 * @throws IOException 
	 */
	public void setGPA(double GPA, RandomAccessFile raf) throws IOException{
		this.GPA = GPA;
		writeToFile(raf);
	}
	
	/**
	 * get the student record size
	 * @return an int representing the record size
	 */
	public int size(){
		return this.RECSIZE;
	}
	
	/**
	 * get the print screen
	 * @return an int representing how many records to print per screen
	 */
	public int printScreen(){
		return this.SCREEN_PRINT;
	}
	
	/**
	 * Read a student info from a text file and pad the first and last names with a blank(s) if needed
	 * @param in the text file
	 * @throws IOException
	 */
	public void readFromTextFile(Scanner in) throws IOException {
		fName = in.next();
		lName = in.next();
		ID = in.nextInt();
		GPA = in.nextDouble();
		fName = pad (fName, LENGTH);
		lName = pad (lName, LENGTH);
	}
	
	/**
	 * Read a student info from a text file and pad the first and last names with a blank(s) if needed
	 * @param in the text file
	 * @throws IOException
	 */
	public void readFromFileNoID(Scanner in) throws IOException {
		fName = in.next();
		lName = in.next();
		GPA = in.nextDouble();
		fName = pad (fName, LENGTH);
		lName = pad (lName, LENGTH);
	}
	/**
	 * Padding a string with trailing blank(s) 
	 * @param s the string
	 * @param size the length of the resulting string
	 * @return a string of length size
	 */
	public static String pad (String s, int size){
		for (int i = s.length(); i < size; i++)
			s += ' ';
		return s;
	}
	
	/**
	 * Write a student record to the random access file
	 * @param out the random access file
	 * @throws IOException
	 */
	public void writeToFile(RandomAccessFile out) throws IOException {
		writeString(out, fName,LENGTH );
		writeString(out, lName, LENGTH);
		out.writeInt(ID);
		out.writeDouble(GPA);
		
	}
	
	/**
	 * Write size characters to the random access file
	 * @param out the random access file
	 * @param name the characters to write
	 * @param size number of character s to write
	 * @throws IOException
	 */
	private void writeString(RandomAccessFile out, String name, int size)throws IOException{
		char [] str = new char [size];
		str = name.toCharArray();
		for (int i = 0; i < str.length; i++)
			out.writeChar(str[i]);
	}
	
	/**
	 * Read a student record from the random access file
	 * @param in the random access file
	 * @throws IOException
	 */
	public void readFromFile(RandomAccessFile in)throws IOException {
		fName = readString(in, LENGTH);
		lName = readString(in, LENGTH);
		ID = in.readInt();
		GPA = in.readDouble();
	}
	
	/**
	 * Read size characters from the random access file
	 * @param in the random access file
	 * @param size the number of characters to read
	 * @return a string representing the characters read
	 * @throws IOException
	 */
	private String readString(RandomAccessFile in, int size)throws IOException{
		char [] str = new char [size];
		for (int i =0; i < str.length; i++)
			str[i] = in.readChar();
		return new String(str);
	}
	
	/**
	 * Create and return a string that represents a student 
	 * @return a string representing a student
	 */
	@Override
	public String toString(){
		return fName + "\t" +  lName + "\tstuden ID = "+ ID +
		       "\tGPA = " + GPA;
	}
	

}