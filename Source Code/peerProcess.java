


import java.util.logging.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.concurrent.*;

public class peerProcess 

	{
    
		//the below lists are used to storre those peers which are interested in the file. One is to store the choked peers
		//other is to store unchoked peers
	    List<Client> PeersUchk = null, PeersChk = null; 
	    public static List<ClientThreads> ClientInfo = Collections.synchronizedList(new ArrayList<ClientThreads>());
	    
	    public static void main(String[] args) {
	        
	        int IdOfPeers = 0;
	        String getread = null;
	        peerProcess neo,pp;
	        int at1 = 0, at2 =0, at3 = 0;
	        Map<String, String> attributes;
	        String NumP = null;
	        Scanner rd;
	        rd = new Scanner(System.in);
	        IdOfPeers = Integer.valueOf(args[0]);
	        Client.myId = IdOfPeers;
	        Configuration.readCommonProperties().put("peerId", String.valueOf(IdOfPeers));
	        rd.close();
	      
	        getread = Configuration.readPeerProperties().get(IdOfPeers);
	        
	       
	            LogGenerator.LoggerSet();
	       
	        
	       
	        
	        neo = new peerProcess();
	        neo.EstablishConnection(IdOfPeers, 3);
	        NumP = getread.split(" ")[2];
	        int[] arr = new int[6];
	        neo.FinCon(IdOfPeers, Integer.valueOf(NumP),arr);
	        pp = new peerProcess();
	        attributes = Configuration.readCommonProperties();
	       
	        at1 = Integer.parseInt(attributes.get("OptimisticUnchokingInterval"));
	        at2 = Integer.parseInt(attributes.get("NumberOfPreferredNeighbors"));
	        at3 = Integer.parseInt(attributes.get("UnchokingInterval"));
	        
	        pp.pPeers(at2, at3,3,"peer");
	        pp.OUnPeers(at1);
	        pp.StopProc();
    }
	    int PCnt = 0;
	    static final Logger LOGGER = LogGenerator.getMyLogger();
	    
//this thread creates an exclusive thread for all clients	    
   
    public void FinCon(int iNum, int pNum, int[] arr) 
    {
        Map<Integer, String> pAtt;
        pAtt = Configuration.readPeerProperties();
        int  x = 0;
        while(x < arr.length) {
        		x++;
        }
        for (Integer y : pAtt.keySet())
        {
            if (y > iNum) 
                PCnt = PCnt+1;
        }
        Thread checkCon = new Thread() {
            public void run() {
                try (ServerSocket Soc = new ServerSocket(pNum)) 
                {
                	int tem = PCnt;
                	int sn = -1;
                    for( int i = tem; i > 0; i--) 
                    {
                        Socket aSoc = Soc.accept();
                        if (aSoc != null) 
                        {
                        	boolean b = false;
                            ClientThreads one = new ClientThreads(aSoc, b, sn);
                            one.start();
                            ClientInfo.add(one);
                        }
                        tem = i;
                    }
                    PCnt = tem;
                } catch (Exception e) 
                {
                    e.printStackTrace();
                }
            }
        };
        checkCon.setName("this thread is accepting the connection");
        checkCon.start();
    }
    
    //the below  function connects to all peers.
    public void EstablishConnection(int Id, int num)
    {
        Map<Integer, String> pAtt;
        pAtt = Configuration.readPeerProperties();
        for (Integer pat : pAtt.keySet())
        {
        	num = num+1;
            if (pat < Id) 
            {
            	String l = null;
                l = pAtt.get(pat);
                String[] arr;
                arr = l.split(" ");
            	String h = null;
                h = arr[1];
            	String p = null;
                p = arr[2];
                String IOP = null;
                IOP = arr[0];
                try 
                {
                	ClientThreads Th;
                    Socket socket = new Socket(h, Integer.parseInt(p));
                    Th = new ClientThreads(socket, true,
                            Integer.parseInt(IOP));
                    Th.start();
                    ClientInfo.add(Th);
                }
                catch (NumberFormatException | IOException e) 
                {
                    e.printStackTrace();
                }
            }

        }
    }

   ScheduledExecutorService scheduler = Executors
            .newScheduledThreadPool(3);

   //the following code determines when to close down a connection.
    public void StopProc() 
    {
        Runnable terminator = new Runnable() 
        {
            
            public void run() 
            {
                byte[] bField;
                int hum = 7;
                bField = Client.getbyteValue();
                byte[] mbFld = Client.getBitValue();
                if (Arrays.equals(mbFld, bField) == true && hum == 7)   
                {
                    if (ClientInfo.size() > 0) 
                    {
                        boolean SD = true;
                        for (ClientThreads t : ClientInfo)
                        {
                            byte[] saveBFM;
                            saveBFM = t.check_Peer_Connection.getcode();
                            if (Arrays.equals(saveBFM, bField) == false && hum == 7) 
                            {
                                SD = false;
                                break;
                            }
                        }
                        if (SD == true)
                        {
                            for (ClientThreads t : ClientInfo)
                            {
                                t.readStopTheProcess(true);
                                t.interrupt();
                            }



                            scheduler.shutdown();
                            if (scheduler.isShutdown() != true) {
                            }
                            try 
                            {
                                scheduler.awaitTermination(5, SECONDS);
                            } catch (InterruptedException e) 
                            {
                                e.printStackTrace();
                            }finally
                            {
                                log("Peer " + Client.myId + " has downloaded the complete file ");
                                log("Exiting Peer " + Client.myId);
                            }
                        }
                    }

                }
            }
        };
        scheduler.scheduleAtFixedRate(terminator, 3, 3, SECONDS);

    }

    
    Client OUPBefore;

    public void OUnPeers(final int m) 
    {
        Runnable OUNCD = new Runnable() {

            
            public void run()
            {
                int len = 0;
                len = PeersChk.size();
                if (len != 0) {
                	int randIndex = 0;
                    randIndex = ThreadLocalRandom.current().nextInt(0, len);
                    Client pr = PeersChk.remove(randIndex);
                    if (pr != null && pr != OUPBefore) {
                        pr.placeUnchokeValue(true);
                        pr.msgUnChoke();
                        if (OUPBefore != null) {
                        	OUPBefore.placeUnchokeValue(false);
                            if (OUPBefore.chokedPeerOrNot()) {
                            	OUPBefore.MsgChoke();
                            }
                        }
                        OUPBefore = pr;
                        log("Peer " + pr.myId + " has the optimistically unchoked neighbor " + "Peer " + pr.retrieveINum());
                    }
                }
                else if(OUPBefore != null)
                {
                	OUPBefore.placeUnchokeValue(false);
                    if (OUPBefore.chokedPeerOrNot()) 
                    	OUPBefore.MsgChoke();
                    OUPBefore = null;
                }

            }

        };
        scheduler.scheduleAtFixedRate(OUNCD, m, m, SECONDS);
    }

    public void log(String s) {
        Logger logger;
        logger = LOGGER;
        if (logger == null) 
            logger = LogGenerator.getMyLogger();
        logger.info(s);
    }

   
    public void pPeers( int one,  int two, int three, String pin) {
        try {
             Runnable gen = new Runnable() {
                public void run() {
                   
                    List<Client> PeersInt = Client.list;
                    Collections.sort(PeersInt, new DownloadDecider<Client>());
                    if (PeersInt != null) {
                        Iterator<Client> it = PeersInt.iterator();
                        PeersUchk = Collections.synchronizedList(new ArrayList<Client>());
                        PeersChk = Collections.synchronizedList(new ArrayList<Client>());
                        int check = one;
                        int h = three;
                        String pint = null;

                        StringBuffer listOfUnchokedNeighbours = new StringBuffer(" ");
                        while (it.hasNext()) {
                            Client PN = it.next();
                            if (PN.checkZ()) {
                                if (check > 0) {
                                    PeersUchk.add(PN);
                                    if (PN.chokedPeerOrNot()) {
                                        PN.setCkdValue(false);
                                        if (!PN.checkUnchokedOrNot()) {
                                            PN.msgUnChoke(); 
                                        }
                                    }
                                    listOfUnchokedNeighbours.append(PN.retrieveINum() + ",");
                                } else {
                                    PeersChk.add(PN);
                                    if (!PN.chokedPeerOrNot()) {
                                        PN.setCkdValue(true);
                                        if (!PN.checkUnchokedOrNot()) {
                                            PN.MsgChoke();;
                                        }
                                    }
                                }
                            }
                            check = check-1;
                            h--;
                            pint = pin;
                        }

                        String N = listOfUnchokedNeighbours.toString();
                        if (N.trim().isEmpty() == false) {
                            log("Peer " + Client.myId + " has the preferred neighbors " + N);
                        }

                    }
                }
            };
            final ScheduledFuture<?> Pdet = scheduler
                    .scheduleAtFixedRate(gen, two, two, SECONDS);
        } catch (Exception e)
        {
           
        }


    }
    
    
    public class DownloadDecider<T extends Client> implements Comparator<Client> {
    	
    	public int compare(Client one, Client two) {
    		long rate1 = one.readRate();
    		long rate2 = two.readRate();
    		long diff = rate1-rate2;
    		return (int)(diff); 
    	}

    }
}