/*Importing required things for the program */

import java.util.*; //Imports java utility package	

import java.io.*;  //Imports java input/output package

import java.util.logging.Logger;   //imports built-in java's logging api

import java.util.concurrent.ConcurrentHashMap; //imports concurrent HashMap 

import java.util.concurrent.ThreadLocalRandom;  //imports java's concurrent thread 

import java.net.Socket;  //imports java's built-in socket facility


/*Definition of peer class */

public class Client 


{
	/*Constructor for Client class*/
	public Client(Socket s) 
    {
        this.s1 = s;
        try 
        {
            o1 = new BufferedOutputStream(s1.getOutputStream());
            i1 = new BufferedInputStream(s1.getInputStream());
        } 
        catch (IOException error) 
        {
           System.out.println(error.getMessage());
        }
    }
	/*Method to get the next Bit Field*/
	synchronized int getBitField() 
    {
        byte[] b1 = getBitReq(), b4 = getBitValue();
        byte[] b2 = new byte[code.length], b3 = new byte[code.length];
        for (int j = 0; j < b1.length; j++)  b3[j] = (byte) (b1[j] & b4[j]);
        for (int j = 0; j < b3.length; j++)  b2[j] = (byte) ((b3[j] ^ code[j]) & ~b3[j]);
        ArrayList<Integer> l = new ArrayList<>();
        for (int k = 0; k < b2.length; k++) 
        {
            byte temp = b2[k];
            if(temp != 0)     l.add(k);
        }
        if (l.isEmpty())  return -1;
        int position = ThreadLocalRandom.current().nextInt(0, 8);
        int i = l.get(ThreadLocalRandom.current().nextInt(0, l.size()));
        byte b5 = b2[i];
        while(b5 == 0 || (b5 & (1 << position)) == 0)
        {
            i = ThreadLocalRandom.current().nextInt(0, b2.length);
            position = ThreadLocalRandom.current().nextInt(0, 8);
            b5 = b2[i];
        }
        insertInBitReq(i, position);
        int c = i*8;
        position = 7 - position;
        int result = c + position;
        setr_I(result);
        return result;
    }
	
static int myId;//Declaring a variable for storing the id of a peer
    
    long DRate;//Variable for Downloading rate
               //Using long for the reason of making sure that the Drate may exceed the normal int's range  
    
    int iNum,r_I;
    
    //Method to insert the download rate
    synchronized void placeTheRate(final long d) 
    {
        DRate = -d;
    }
    
    //Method to place the client value
    void place_client_value(final boolean v) 
    {
        client_value = v;
    }
    
    //Method to read the downloading rate
    synchronized long readRate() 
    {   
    	return DRate;
    }
    
	boolean   unchoke_Value   =   false;
    
	boolean client_value = false;
    
	//Method to check whether or not the peer is unchoked
    boolean checkUnchokedOrNot  ()
    {
        return  unchoke_Value;
    }
    
  //set Method for the requested index 
    synchronized  void setr_I(final int i) 
    {
        r_I = i;
    }
    
  //Method to get the requested index
    synchronized  int getRequestedIndex() 
    
    {
        return r_I;
    }
    
    //Method to correct the unchoke status
    void placeUnchokeValue(boolean status)
    {
    	unchoke_Value = status;   
    }
    
	//Synchronized HashMap for request time
	static    Map   <    Integer  ,   Long   >   timeOfRequestMap =  Collections  .    synchronizedMap    (   new    HashMap   <   Integer  ,    Long   >  ()   )  ;
	//Synchronized HashMap for Download time
	static   Map   <   Integer   ,    Long   > timeOfDownloadMap =    Collections    .    synchronizedMap    (    new       ConcurrentHashMap      <       Integer    ,      Long     >    ()     )               ;
    //Declaring a Logger
	static final    Logger     Log    =   Logger  .    getLogger  (   Logger.GLOBAL_LOGGER_NAME   )  ;
    //List of peers
	static List<Client>   list    =      Collections   .   synchronizedList          (       new   ArrayList        <    Client     >   ()   )  ;
	
	//Method to remove a peer from the list
    static synchronized void deleteFromMyList(Client d)
    {
    	Client.list.remove(d);
    }

    //Method to add a peer to the list
    static synchronized void insertToMyList(Client d)
    {
        
    	Client.list.add(d);
    }

    //Method to find if the peer is interested or not
    synchronized boolean intOrNot() 
    {
        int i = 0;
        byte[] myBitField = getBitValue();
        byte[] result = new byte[myBitField.length];
        for (byte byt : myBitField) 
        {
            result[i] = (byte) (byt ^ code[i]);
            i++;
        }
        i = 0;

        for (byte b : myBitField) 
        {
            result[i] = (byte) (result[i] & ~b);
            if (result[i] != 0)  return true;
        }
        return false;
    }
    
    //Map for peers which are not neighbors
	static     Map       <           Integer     ,      Client       >         noNeighboursMap      =         Collections     .         synchronizedMap       (     new      HashMap          <           Integer        ,            Client          >             ()          )           ;
    //Map for peers which are choked
	static      Map   <        Integer     ,       Client        >         choke        =    Collections      .       synchronizedMap        (         new     HashMap      <     Integer    ,    Client   >   ()   )   ;
    //Map for peers which are unchoked
	static    Map          <         Integer        ,      Client        >          unChoke         =          Collections     .         synchronizedMap        (       new     HashMap     <    Integer    ,     Client     >    ()   )   ;
    //Map  for storing peer handshake information
    static   Map   <    Integer  ,   Boolean    >   peerHandShakeMap   =   new    HashMap  <   Integer   ,    Boolean  >   ()   ;
    
    
    //Set method for inserting Id number
    void insertINum(final int inum) {
        this.iNum = inum;
    }
    
    Boolean z = false;
    //Get method for z
    boolean checkZ()
    {
        return z;
    }
    //Method to return iNum
    int retrieveINum() 
    {
        return iNum;
    }
    
    //Method to check if the peer is in the interested list
    static synchronized boolean checkMyList(Client d)
    {
        for(Client p : Client.list) if   (   p  .  retrieveINum() == d . retrieveINum()  ) return true;
        
        return false;
    }
    
  //Method to read unchoked messages
    synchronized void msgUnChoke() 
    {
        byte[] b = HelpFunctions.getMess(MessageTypes.M_Types.UNCHOKE);
        try 
        {
            o1.write(b);
            o1.flush();
        } 
        catch (IOException error) 
        {
            error.printStackTrace();
        }
    }
    
    static byte[] bitValue,byteValue;
    //Method to get the bitValue
    static synchronized byte[] getBitValue() 
    {
        return bitValue;
    }
    //Method to get the byteValue
    static byte[] getbyteValue() 
    {
        return byteValue;
    }
    
  //Method to insert the bit
    synchronized void insertInBitReq(int z4, int z5)
    {
    
    	bitReq[z4] |= (1 << z5);
    }
  
  //Method to insert the initialised variable
    public synchronized  void insertInit(boolean status)
    {
        z = true;
        notify();
    }

  static byte[] bitReq;

  //Method to get the bit req
     static synchronized byte[] getBitReq() 
     {
     
    	 return bitReq;
    
     }
     //Method to insert the bitvalue
    static synchronized void insertInBitValue(int z4, int i) 
    
    {
         
    	bitValue[z4] |=   (1 << (7 - i));
    
    }

    static byte[] bitsShared = null;
    
    
    static 
    
    {
        String f1 = Configuration.readCommonProperties().get("FileName");
        String f1String = Configuration.readCommonProperties().get("FileSize");
        Integer f1_size = Integer.parseInt(f1String);
        long count = 0;
        int countSize = Integer.parseInt(Configuration.readCommonProperties().get(
                "PieceSize"));

        if (f1_size % countSize == 0) {
            count = f1_size / countSize;
        } else {
            count = f1_size / countSize + 1;
        }
        double bl = Math.ceil(count / 8.0f);
        bitValue = new byte[(int) bl];
        byteValue = new byte[(int) bl];

        bitReq = new byte[(int) bl];
        bitsShared = new byte[Integer.parseInt(Configuration.readCommonProperties().get("FileSize"))];
        File f = new File(f1);
        if (f.exists())
        {
            if (f.length() != f1_size)
          
                System.exit(-1);
           
            else {
               
            	FileInputStream fis2 = null;
                
            	try 
                {
                    fis2 = new FileInputStream(f);
                    fis2.read(bitsShared);
                    fis2.close();
                } 
                
                catch (FileNotFoundException e) 
                
                {
                   System.out.println(e.getMessage());
                
                }
                
                catch (IOException e)
                
                {
                
                	System.out.println(e.getMessage());   
                }

            }
            if (count % 8.0 == 0) 
            {
            
            	Arrays.fill(bitValue, (byte) 255);
                
            	Arrays.fill(byteValue, (byte) 255);
            } 
            else 
            {
                int h1 = (int) count % 8;
               
                Arrays.fill(bitValue, (byte) 255); 
                
                Arrays.fill(byteValue, (byte) 255);
                
                bitValue[bitValue.length - 1] = 0; 
                
                byteValue[byteValue.length - 1] = 0;
                
                while (h1 != 0) 
                {
                    bitValue[bitValue.length - 1] |= (1 << (8 - h1));
                    byteValue[byteValue.length - 1] |= (1 << (8 - h1));
                    h1--;
                }
            }
        }
        else 
        {
            if (count % 8.0 == 0) 
                Arrays.fill(byteValue, (byte) 255);
            
            else 
            {
                int h2 = (int) count % 8;
                
                Arrays.fill(byteValue, (byte) 255);
                
                byteValue[byteValue.length - 1] = 0; 
                while (h2 != 0) 
                {
                    byteValue[byteValue.length -    1  ] |=   (    1   <<   (   8   -   h2  )  );
                    h2--;
                }
            }
       
        
        }

    }

    Socket s1 = null;
    OutputStream o1 = null;
    InputStream i1 = null;

    

    //Method to close the connection
    public void closeTheConnection() 
    {
        try 
        {
            s1.close();
        } 
        catch (IOException error) 
        {
        	
        	System.out.println(error.getMessage());
        }
    }

  //Method to close the connection permanently
    public void closeFinally() 
    {
        this.closeTheConnection();
    }

    //Method to send the peers handshake messages
    public synchronized void peerHSSend() 
    {
        synchronized (peerHandShakeMap) 
        {
            byte[] b = HelpFunctions.concArr(HelpFunctions.concArr(MessageTypes.HANDSHAKE_HEADER,
                            MessageTypes.ZERO_BITS), Configuration.readCommonProperties().get("peerId").getBytes());
            try 
            {
                o1.write(b);
                o1.flush();
                peerHandShakeMap.put(iNum, false);
            } 
            catch (IOException error) 
            {
                Log.severe("Failed while sending the handshake" + error.getMessage());
                error.printStackTrace();
            }
        }

    }

  //Method to read the handshake messages
    public synchronized int getHMsg() 
    {
        try 
        {
            byte[] y1 = new byte[32];
            i1.read(y1);
            byte[]   g     =    Arrays   .     copyOfRange  (  y1   ,   28  ,   32  )   ;
            String s = new String(g);
            Integer p_Id= Integer.parseInt(s);
            if (  client_value  ) 
                if  (   peerHandShakeMap  .   containsKey  (   p_Id  )  &&   peerHandShakeMap   .   get    (   p_Id   )   ==    false   ) 
                	peerHandShakeMap  .   put   (    p_Id   ,   true   )  ;
            return    p_Id  ;
        } 
        catch (   IOException error   ) 
        {  
            System.out.println  (  error  .   getMessage  () ) ;
        }
        return -1;
    }
  //Method for reading the bit field messages
    public synchronized void bitMSG() {
        try {
            byte[] j    =     getBitValue()   ;
            byte[]    b  =  HelpFunctions  .  getMess   (  j,   MessageTypes  .   M_Types  .   BITFIELD  )  ;
            o1   .  write   (  b   ) ;
            o1  .   flush  ()  ;
        } 
        catch (IOException error)
        {
            System.out.println(error.getMessage());
        }
    }

    //Method to update the message id
    synchronized void MsgUpdate(int a) 
    {
        int p = 7 - (a % 8);
        code[a / 8] |= (1 << p);
    }

  //Method for reading the messages
    synchronized void MsgRead() 
    {
        code = HelpFunctions.readMess(i1,MessageTypes.M_Types.BITFIELD);
    }

    
  //Starting method for client.java
    synchronized void initialized() 
    {
        
    	while (z == false) 
    	{
            try 
            {
                wait(1000);
            
            } 
            
            catch (Exception e) 
            
            {
                
            }
        }
    }

    
   
  //Method to insert the interested messages
     synchronized void intMsg() 
     {   
        byte[] b = HelpFunctions.getMess(MessageTypes.M_Types.INTERESTED);
        try 
        {
            o1.write(b);
            o1.flush();
        } 
        
        catch (IOException error) 
        {
            error.printStackTrace();
        }
    }

   //Method to find the not send messages
    synchronized void notSend() 
    {
        byte[] b =    HelpFunctions  .  getMess   (    MessageTypes  .   M_Types  .   NOT_INTERESTED  )  ;
        try 
        {
            o1   .   write  (  b  )  ;
            o1  .  flush   ()   ;
        } 
        catch (IOException error) 
        {
            error .   printStackTrace  ()  ;
        }
    }

  //Method to send Handshake message
    synchronized void sendHaveMsg   (  int   p ) 
    
    {
        byte[] b = HelpFunctions.getMess  (   HelpFunctions   .  in_byte  (  p  )  ,  MessageTypes . M_Types . HAVE  );
        
        try 
        {
            o1   .  write  (   b  )  ;
            o1  .  flush()   ;
        } 
        
        catch (IOException error) 
        {
            System.out.println(error.getMessage());
        }
    }
    
    
  //Method for Message Choke
    synchronized  void MsgChoke() 
    {
        byte[] b = HelpFunctions
                .getMess(MessageTypes.M_Types.CHOKE);
        try 
        {
            o1.write(b);
            o1.flush();
        } 
        catch (IOException error) 
        { 
            error.printStackTrace();
        }
    }
    
    

    boolean ckd= true;


    //set Method for ckd
    synchronized void setCkdValue(boolean ckd_value) 
    {
        ckd = ckd_value;
    }
    
  //Method to determine whether the peer is choked or not
    synchronized boolean chokedPeerOrNot() 
    {
        return ckd;
    }

  //Method to request the message
    synchronized void requestMsg(int b1) 
    {
        if (b1 >= 0) 
        {
            byte[] b2 = HelpFunctions.in_byte(b1);
            byte[] b = HelpFunctions.getMess(b2, MessageTypes.M_Types.REQUEST);
            try 
            {
                o1.write(b);
                o1.flush();
                Client.timeOfRequestMap.put(iNum, System.nanoTime());
            } 
            catch (IOException error) 
            {
                error.printStackTrace();
            }
        }
    }

  //Method to reinsert the bits
    static synchronized  void reInsertInBitReq(int z4, int z5)
    {  
       
    	bitReq[z4] &= ~(1 << (7 - z5));
    }

    
    byte[] code= null;

  //Method to get the code
    synchronized byte[]  getcode()
    
    {
    
    	
    	return code;
    
    }
    

  //Method to send the messages
    synchronized void send(int b1) 
    {
        int newSize1 = Integer.parseInt(Configuration.readCommonProperties().get("PieceSize"));
        int i1 = newSize1 * b1;
        int endIndex = i1 + newSize1 - 1;
        if (endIndex >= bitsShared.length) endIndex = bitsShared.length - 1;

        byte[] data = new byte[endIndex - i1 + 1 + 4]; 
        byte[] pieceIndexByteArray = HelpFunctions.in_byte(b1);
        for (int j = 0; j < 4; j++) 
            data[j] = pieceIndexByteArray[j];
        int i = i1; 
        for (; i <= endIndex; i++) 
            data[i - i1 + 4] = bitsShared[i];

        byte[] b = HelpFunctions.getMess(data,MessageTypes.M_Types.PIECE);
        try 
        {
            o1.write(b);
            o1.flush();
        } 
        catch (IOException error) 
        {
            error.printStackTrace();
        }
    }
    
    
}