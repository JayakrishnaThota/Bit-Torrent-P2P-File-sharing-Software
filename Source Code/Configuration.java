import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public class Configuration 
{
    
	
	
	//Read the PeerInfo and Common.cgf files 

	
	
	String    commonFileName = "Common.cfg";
	
	String   peerInfoFileName = "PeerInfo.cfg";
	
	
   
	
	Logger   LOGGER = Logger.getLogger(Configuration.class.getName());
	
	
	
	
	
	//Reading the common.cfg values in to a map and returning the map
	
	
	
	static        Map            <          String     ,   String    >    readCommonProperties()
	{    
		//If configuration is null then initialize the configuration
		if   (  c   ==   null   )
		
			//Initializing the configuration
			c   =   new Configuration()   ;
		
		
		//returning the map
		return Cmap;
	}
	
	
	//Reading the PeerInfo.cfg values into a map and returning the map.
	
 static   Map  <   Integer     ,    String   >    readPeerProperties()
 
 {
	//If configuration is null then initialize the configuration
		if    (   c == null    )
			
			//Initializing the configuration
			c = new Configuration();
		
		//returning the map
		
		return Pmap;
	}
 static  Map  <  Integer  ,   String   >    Pmap   =   null;
 
	
 //Default constructor of the class
	
 static   Configuration     c     =     null;
 
 static    Map   <   String   ,   String   >    Cmap   =   null;
 
	public Configuration() 
	
	
	{
	 
		//Initializing the file input stream object
		FileInputStream   inputStream    ;
		 
		
		try 
		
		{
			//Initializing the maps
			
			Pmap   =   new   HashMap   <   Integer    ,    String   >    ()  ;
			
			Cmap =     new       HashMap    <       String     ,      String     >    ()     ;
			
			
			//Reading the common.cfg
			
			File file    =    new    File   (   commonFileName   )   ;
			
			inputStream     =      new      FileInputStream     (    new     File    (    commonFileName    )      )    ; 
			
			//Initializing the buffered reader to read the file
			
			
			BufferedReader bufferReader	   =    new     BufferedReader    (   new          InputStreamReader    (    inputStream    )   )  ;
			
			String S =    null   ;
			
			
			//Checking for the end of the file
			
			while (   (S =     bufferReader     .    readLine()   ) != null    ) 
			{
				String[] split = S.split(" ");
				
				//Adding values into the map
				
				Cmap   .   put   (   split[0]      ,       split   [   1   ] )  ;
			}
			
			//Closing the buffered reader 
			bufferReader   .    close    ()    ;
			
			//Initializing to read PeerInfo
			
			
			inputStream    =    new    FileInputStream   (  new    File    (peerInfoFileName)   )   ;
			
			bufferReader    =    new    BufferedReader   (     new    InputStreamReader  (          inputStream   )   )   ;
			
			
			String St = null;
			
			while (   (   St =    bufferReader.readLine())    !=   null   ) 
			
			{
				
				String[]    split   =    St   .     split      (" ")    ;
			
			//Adding values into the Pmap	
				
				Pmap.put(Integer.parseInt(split[0]), St);
			
			}
		
			
			//Closing the buffered reader
			
			bufferReader.close();
	
		} 
		
		// Catch if any exception occurred and display the error message
		catch (FileNotFoundException error)
		{
			//Displaying File not found error
			
			System.out.println(error.getMessage());
			
		}
		catch (IOException error) 
		{	
			//Displaying the input or output exception
			
			System.out.println(error.getMessage()); 
			
		}
	}
}