import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Logger;

//Implementation of Threading
public class ClientThreads extends Thread {
	
	//Initializing peer object to null
		Client check_Peer_Connection = null;
    
    //Parameterized constructor with socket verify_client and input_id
		
    public ClientThreads(Socket socket, boolean verify_client, int input_id) 
    
    
    {
    	//Initialize the socket
        
    	this.new_socket = socket;
        
    
    	this.verifyClientorNot = verify_client;
       
    	//Initializing the peer object with new socket input
    	
    	check_Peer_Connection = new Client(new_socket);
        
    	
    	//Verifying the Peer
    	if ( 		verifyClientorNot		) 
    		
    	{
            //Setting the peerId to peer object
    		
        	check_Peer_Connection.insertINum(input_id);
            
        	check_Peer_Connection.place_client_value(true);
            
        	//sending the handshake message to the peer
        	
        	check_Peer_Connection.peerHSSend();
            
        	check_Peer_Connection.getHMsg();
        } 
        
    	else 
        
    	{
        
    		int z = check_Peer_Connection.getHMsg();
           
    		check_Peer_Connection.insertINum(z);
            
    		check_Peer_Connection.peerHSSend();

        }
       
        if(input_id == -1)
            
        	
        	this.setName("Peer : " + Client.myId);
        
        
        else
           
        	this.setName("Peer : " + input_id);
        

      //Start a new thread
        start_Thread = new Thread() 
        {
        
        	//Overriding the run function
        	
        	public void run() 
        	{
                
               //sending the bit field message
        		
        		check_Peer_Connection.bitMSG();
                
        		//reading the message
        		
        		check_Peer_Connection.MsgRead();

                //checking if peer is interested
        		
        		if 	(	check_Peer_Connection	.	intOrNot	()	) 
                
        			//If peer is interested send interested message
                    check_Peer_Connection	.	intMsg()	;
               
                else 
                   //If peer is not interested send not interested message
                	
                    check_Peer_Connection	.	notSend	()	;
                

              

                if 	(	verifyClientorNot	== 	true) 
                    
                	//Writing to lof file that connection is established
                		LOGWRITER	.	info	(	"Peer " + 	Configuration	.	readCommonProperties()	.	get	(	"peerId"	)	 + " makes a connection to Peer " + check_Peer_Connection.retrieveINum());
                
                else 
                    
                	
                	LOGWRITER	.	info	(	"Peer " 	+ Configuration	.	readCommonProperties()	.	get("peerId") + " is connected from " + check_Peer_Connection.retrieveINum());
                
                
                check_Peer_Connection.insertInit(true);
            }

        };
      //Starting the thread
        
        start_Thread.start();


    }
    //setting verify client or not to false
    
    boolean verifyClientorNot= false;
    
    Thread start_Thread = null;
    
  //Instantiating the logger class to write log files
    
    static final Logger LOGWRITER = LogGenerator.getMyLogger();
//overriding the run method of thread
    @Override
    public void run() 
    
    {
       
//initializing the peer
    	
        check_Peer_Connection.initialized();
       //try catch block to catch any IO exceptions
            try 
            {
                
            	InputStream get_is = new BufferedInputStream(new_socket.getInputStream());
                
            	while 	(	!	stopTheProcess	) 
            	
            	{
                
            		byte[] messageTypeInBytes = new byte[5];
                
                    messageTypeInBytes = 	HelpFunctions	.	getBytes	(	get_is	, messageTypeInBytes	, 5	);

                    MessageTypes.M_Types actualMessageType = HelpFunctions.	get_type	( messageTypeInBytes  );
                    
                    
                    
                    switch (actualMessageType) 
                    
                    {
                    
                    //Bitfield message to share the pieces a peer has and send interested messages.
                        
                    case BITFIELD:
                            
                            break;
                            
                    //Have message is sent from peer to another peer if the peer has the particular piece
                            
                    case  HAVE:

                            byte[]  PieceReadInBytes  =   new 	byte	[	4	]	;

                            PieceReadInBytes = 	HelpFunctions	. getBytes	(	get_is	, 	PieceReadInBytes	, 4	)	;
    
                            int PieceRead = HelpFunctions	.	by_in	(	PieceReadInBytes	)	;
                           
                            byte[] getBits 	= 	Client	.	getBitValue()	;
                            
                            byte 	getBytes = 	getBits	[	PieceRead	 / 	8	]	;

                            if 	(	(	getBytes & (1 << (7 - (PieceRead % 8)))) 	==	 0	) 
                            
                            {
                                
                                check_Peer_Connection.intMsg();
                            }
                            
                            check_Peer_Connection	.	MsgUpdate	(	PieceRead	)	;
                            //Writing the have message to the log file of peer
                            
                            LOGWRITER	.	info	(	"Peer " + Client.myId + " received the have message from " + check_Peer_Connection.retrieveINum());
                            
                            break;
                        
                        
                        case CHOKE:
                           

                            int a = 	check_Peer_Connection	.	getRequestedIndex	()	;
                           
                            byte[] b 	= 	Client	.	getBitValue	()	;
                            
                            byte c = b	[		a 	/ 	8	]	;

                            if (	(	c & (1 << (7 - (a % 8)))) == 0	) 
                            
                            {
                              
                                Client	.	reInsertInBitReq	(	a / 8 	, 	a % 8	)	;
                            }

                            
                            Client	.	choke	.	put	(	check_Peer_Connection	.	retrieveINum()	, 	check_Peer_Connection	)	;
                            
                            
                            LOGWRITER	.	info	("Peer " 		+		 Client			.		myId 		+ 		" is choked by "		+ 			check_Peer_Connection	.		retrieveINum()		)	;
                            
                            
                            break;
                            
                        case INTERESTED:
                           
                            boolean peerInterested = Client	.	checkMyList	(	check_Peer_Connection	)	;

                           
                            if	(	!peerInterested	)
                            
                            {
                                
                                Client.insertToMyList(check_Peer_Connection);
                            }
                          
                            
                            LOGWRITER.info("Peer " + Client.myId + " received the interested message from " + check_Peer_Connection.retrieveINum());
                           
                            break;
                            
                        
                        case NOT_INTERESTED:
                         
                            
                        	Client	.	deleteFromMyList	(	check_Peer_Connection	)	;
                            
                            
                        	check_Peer_Connection	.	setCkdValue	(	true	)	;
                            
                            
                        	Client	.	noNeighboursMap	.	put	(	check_Peer_Connection	.	retrieveINum	()	, check_Peer_Connection);
                            
                            LOGWRITER.info("Peer " + Client.myId + " received the not interested message from " + check_Peer_Connection.retrieveINum());
                            
                            break;
                            
                        
                       
                        case PIECE:
                            
                            byte[] newb1= new byte[4];
                           /* int i=0;
                            while(i<4)
                            {
                            	 newb1[i] = messageTypeInBytes[i];
                            	 i++;
                            }*/
                            for (int i = 0; i < 4; i++) 
                            
                                newb1[i] = messageTypeInBytes[i];
                            
                            
                            int newLengthb1 = HelpFunctions.by_in(newb1);
                            
                            byte[] newb2 = new byte[4];
                            
         
                            newb2 = HelpFunctions.getBytes(get_is,newb2, 4);

                            
                            
                            int newLengthb2 = newLengthb1 - 1;
                            
                            
                            int newLengthb3 = newLengthb2 - 4;
                            
                            
                            byte[] newb3 = new byte[newLengthb3];
                            
                            
                            newb3 = HelpFunctions.getBytes(get_is, newb3, newLengthb3);

                            Long t1 = - Client.timeOfRequestMap.get(check_Peer_Connection.retrieveINum());

                            
                            Client	.	timeOfDownloadMap	. 	put	(	check_Peer_Connection	.	retrieveINum()	, 	t1	)		;
                            
                            
                            check_Peer_Connection.	placeTheRate	(	t1	)	;
                            
                            
                            int newb4 = HelpFunctions.by_in(newb2);
                            
                            
                            int newPieceSize = Integer.parseInt( Configuration .   readCommonProperties().get("PieceSize"));
                           /* int j =0;
                            while( j < newLengthb3 )
                            {
                            	Peer.dataShared[newb4 * newPieceSize + j] = newb3[j];
                            }*/
                            
                            for (int j = 0; j < newLengthb3; j = j+1) 
                            
                                Client.bitsShared[newb4 * newPieceSize + j] = newb3[j];
                            
                            LOGWRITER.info("Peer " + Client.myId + " has downloaded the piece " + newb4 + " from "+ check_Peer_Connection.retrieveINum());

                           
                            
                            int newb5= newb4 / 8;
                            
                            
                            int newb6 = newb4 % 8;
                            
                            
                            Client.insertInBitValue(newb5, newb6);
                            
                            
                            
                            for (ClientThreads new_Peer_Thread : peerProcess.ClientInfo)
                            
                                
                                new_Peer_Thread.check_Peer_Connection.sendHaveMsg(newb4);
                               
                           
                            
                            
                            
                            
                            int newb7 = check_Peer_Connection.getBitField();
                          
                            
                            
                            if (newb7 != -1)
                                    
                            
                            	if( Client.unChoke.containsKey(check_Peer_Connection.retrieveINum())) 
                                    
                                    
                               
                                check_Peer_Connection.requestMsg(newb7);
                            
                           
                            if(newb7 == -1)
                            
                            
                               
                                check_Peer_Connection.notSend();
                            
                            
                            
                            
                            if(newb7 == -1 )
                            	
                            	if((Arrays.equals(check_Peer_Connection.getBitValue(), check_Peer_Connection.getcode())))
                            
                               
                                check_Peer_Connection.intMsg();
                            

                            if(newb7 == -1 && Arrays.equals(Client.getBitValue(), Client.getbyteValue()))
                            {
                                
                            
                            	for (ClientThreads peerThread : peerProcess.ClientInfo) 
                                
                                
                            		peerThread.check_Peer_Connection.notSend();
                                    
                                
                                
                               
                            	File f1 = new File(Configuration.readCommonProperties().get("FileName"));
                                
                                
                            	try (FileOutputStream fileOutputStream = new FileOutputStream(f1))
                                
                                {
                                    fileOutputStream.write(Client.bitsShared);
                                
                                  } 
                                
                                catch (IOException e)
                                
                                {
                                    
                                
                                	
                                	System.out.println(e.getMessage());
                            
                                	
                                }
                            
                            
                            
                            
                            }

                        
                            
                            break;
                        
                        
                        case REQUEST:
                           
                            
                        	byte[] z1 = new byte[4];
                            
                        	get_is.read(z1);
                            
                        	int z2 = HelpFunctions.by_in(z1);
                            
                            
                            if (!check_Peer_Connection.chokedPeerOrNot() | check_Peer_Connection.checkUnchokedOrNot()) 
                               
                                
                            	check_Peer_Connection.send(z2);
                            
                            
                           
                        
                            
                            break;
                        
                        
                        case UNCHOKE:
                            
                            
                        	Client	.	unChoke	.	put	(	check_Peer_Connection	.	retrieveINum()	, check_Peer_Connection);
                            
                            
                            LOGWRITER	.	info	("Peer " + Client.myId + " is unchoked by " + 	check_Peer_Connection.retrieveINum());
                            
                            int		 y1 = 	check_Peer_Connection	.	getBitField		()	;
                         
                            if (y1 != -1)
                            
                                check_Peer_Connection		.		requestMsg(y1)	;
                            

                         

                            if(y1 == -1)
                            
                                
                                check_Peer_Connection.notSend();
                            

                            if( y1 == -1 )
                            		
                            		if((Arrays.equals(check_Peer_Connection.getBitValue(), check_Peer_Connection.getcode())))
                            
                                
                            				check_Peer_Connection.intMsg();
                            

                            break;
                        default:
                          
                    	}
                		
            	
            	}
              

            } 
            //catching any IO exception and printing the exception message
            
            catch  ( IOException e ) 
            {  
                	if	(		!		stopTheProcess		) 
                   System.out.println(e.getMessage());
                
            }
            //	Finally interrupting the thread
            
            finally 
            
            { 
                
                start_Thread.interrupt();
            
            }

      }
    
    //Instantiating the new socket
    
    Socket new_socket = null;
    
    
    boolean stopTheProcess = false;
  
    //Setting the Stop the process

    public void readStopTheProcess	(	boolean 		socket_closed_open	) 
    
    {
        
    	stopTheProcess = socket_closed_open;
        
    	if(socket_closed_open == true){
        
    		try 
    		
    		{
                if    (  !   new_socket.isClosed()  )
                
                    new_socket.close();
                
            } 
    		
    		//catching any IOException and printing the exception message
    		
            catch (IOException e) 
            
    		{
                
               System.out.println(e.getMessage());
            		
    		
    		}
        		
    	}
        
    
    			}
    
    
		
		}

