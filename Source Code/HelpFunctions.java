import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class HelpFunctions {

	
	
      static int by_in(byte[] b)
      {
     
		 return   b[3] & 0xFF |
            (b[2] & 0xFF) << 8 |
            (b[1] & 0xFF) << 16 |
            (b[0] & 0xFF) << 24;
    }

 static byte[] in_byte(final int a) {
       
       return new byte[] { (byte) ((a >> 24) & 0xFF),(byte) ((a >> 16) & 0xFF),   (byte) ((a >> 8) & 0xFF),   (byte) (a & 0xFF)  } ;
       
    }
 
 static MessageTypes.M_Types get_type(byte[] msg)
 {
     String s1 = Arrays.toString(msg);
     for (MessageTypes.M_Types d1 : MessageTypes.M_Types.values()) 
     {
         if (d1.value == msg[4]) 
             return d1;
         
     }
     return null;
 } 
   
     static byte[] concArr(byte[] a, byte[] b ) 
     {                 // The method is used to byte arrays
        byte[] ans = new byte[a.length + b.length];                      //create a destination array that is the size of the two arrays  
        System.arraycopy(a, 0, ans, 0, a.length);                        // copy a into start of result (from pos 0, copy a.length bytes)
        System.arraycopy(b, 0, ans, a.length, b.length);
        return ans;
    }

     static byte[] concArr(byte[] a, int aLength, byte[] b, int bLength) {  // the overloaded function has the length of the two streams given to it    
        byte[] ans = new byte[aLength + bLength];
        System.arraycopy(a, 0, ans, 0, aLength);
        System.arraycopy(b, 0, ans, aLength, bLength);
        return ans;
    }

     static byte[] concArrs(byte b, byte[] a) {                   // One byte is added to the array
        byte[] ans = new byte[a.length + 1];                            //Length incremented by 1 byte 
        System.arraycopy(a, 0, ans, 0, a.length);                       // byte b is truncated to the array a
        ans[a.length] = b;
        return ans;
    }

   
  
    static byte[] getBytes(InputStream input_stream, byte[] bArr, int len) throws IOException 
    {          // The data is read as bytes from input stream
        int l = len;
        int i = 0;
        while (l != 0) {
            
        	int d = input_stream.available();
            
            int read = (len <= d) ? len : d;
            
            byte[] dr = new byte[read];
            
            if (read != 0)
            {
                input_stream.read(dr);
                bArr = concArr(bArr, i, dr, read);
                i += read;
                l -= read;
            }
        }
        return bArr;
    }
    
    static byte[] getnMes(int id)
    {
    		byte[] 		temp	=	concArr	(	MessageTypes	.	HANDSHAKE_HEADER	, MessageTypes	.	ZERO_BITS	)	;
    		return 	concArr	(	temp	, 	in_byte(id	)	)	;
    }

    static byte[] getMess(String load, MessageTypes.M_Types msgType) 
    {
        int l = load.getBytes().length;
    
        byte[] msgL = in_byte(l + 1); // plus one for message type
        
        byte [] temp= concArrs(msgType.value, load.getBytes());
        
        return concArr(msgL,temp);
    }

   

    static byte[] getMess(byte[] load, MessageTypes.M_Types msgType)
    {
        byte[] ml = in_byte(load.length + 1); // plus one for message type
    
        return concArr(cbyte(ml, msgType.value), load);
    }

    static byte[] readMess(InputStream in, MessageTypes.M_Types bitfield) 
    {
        byte[] bylen = new byte[4];
    
        int flag = -1;
        
        byte[] g1 = null;
        
        try {
            flag = in.read(bylen);
            
            int d_l = by_in(bylen);
          
            byte[] msgType = new byte[1];
            
            in.read(msgType);
            
            if (msgType[0] == bitfield.value) {
            
            	int alen = d_l- 1;
                g1 = new byte[alen];
                g1 = getBytes(in, g1, alen);

            } 
        } 
        catch (IOException e) 
        {
           
            System.out.println(e.getMessage());
        }
        return g1;
    }
    
     static byte[] getMess(MessageTypes.M_Types msgType) {
        
    	 byte[] ml = in_byte(1); // plus one for message type
        
        return cbyte(ml, msgType.value);
    }

    static byte[] cbyte(byte[] a, byte b) {                       // One byte is added to the array
       
    	byte[] ans = new byte[a.length + 1];                             //Length incremented by 1 byte
        
    	System.arraycopy(a, 0, ans, 0, a.length);                        // array a is attached after byte b
        
    	ans[a.length] = b;
        
    	return ans;
    }



}
