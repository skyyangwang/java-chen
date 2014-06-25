package util;

public class JavatoS {
	
	//头长度
	private byte[] buf = new byte[92]; 
	
	//头中的9个字段
	public int ulMsgLen = 0;  			//4
	public int ulMsgId = 0;			//4
	public int ulSaveSock = 0;			//4
	public int ulaction = 0;			//4
	public int ulErrCode = 0;			//4
	public String aucToolIpAddr = "";	//4
	
	public String aucPhone_id = "";		//16
	public String aucRoute_id = "";		//32
	public int ulRoute = 0;			//4   
	
	public JavatoS(byte[] bArr) {
		
		//头中的9个字段-长度；
        byte[] temp1 = new byte[4];  
        byte[] temp2 = new byte[4]; 
        //byte[] temp3 = new byte[4];
        byte[] temp4 = new byte[4];
        byte[] temp5 = new byte[4];
        byte[] temp6 = new byte[4];
        
        byte[] temp7 = new byte[16];
        byte[] temp8 = new byte[32];
        byte[] temp9 = new byte[4];
        
        //头中的9个字段-取值；
        System.arraycopy(bArr, 0, temp1, 0, 4);
        System.arraycopy(bArr, 4, temp2, 0, 4);
        
        //System.arraycopy(bArr, 8, temp3, 0, 4); 
        
        System.arraycopy(bArr, 12, temp4, 0, 4); 
        System.arraycopy(bArr, 16, temp5, 0, 4); 
        System.arraycopy(bArr, 20, temp6, 0, 4); 
        
        System.arraycopy(bArr, 24, temp7, 0, 16); 
        System.arraycopy(bArr, 40, temp8, 0, 32); 
        System.arraycopy(bArr, 72, temp9, 0, 4); 

        ulSaveSock = 1;  
	    
        byte[] temp = temp1;
        System.arraycopy(temp, 0, buf, 0, 4);
        temp = temp2;
        System.arraycopy(temp, 0, buf, 4, 4); 
        
        temp = tolh(ulSaveSock); 
        System.arraycopy(temp, 0, buf, 8, 4); 
        
        temp = temp4;
        System.arraycopy(temp, 0, buf, 12, 4);
        temp = temp5;
        System.arraycopy(temp, 0, buf, 16, 4); 
        temp = temp6;
        System.arraycopy(temp, 0, buf, 20, 4); 
        
        temp = temp7;
        System.arraycopy(temp, 0, buf, 24, 16);
        temp = temp8;
        System.arraycopy(temp, 0, buf, 40, 32);
        temp = temp9;
        System.arraycopy(temp, 0, buf, 72, 4); 
	} 
	
	/** 
     * 返回要发送的数组 
     */  
    public byte[] getbuf() {  
        return buf;  
    }
    
	 /** 
     * 将int转为低字节在前，高字节在后的byte数组 
     */  
    private static byte[] tolh(int n) {  
        byte[] b = new byte[4];  
        b[0] = (byte) (n & 0xff);  
        b[1] = (byte) (n >> 8 & 0xff);  
        b[2] = (byte) (n >> 16 & 0xff);  
        b[3] = (byte) (n >> 24 & 0xff);  
        return b;  
    } 
    
    /** 
     * 将float转为低字节在前，高字节在后的byte数组 
     */  
    private static byte[] tolh(float f) {  
        return tolh(Float.floatToRawIntBits(f));  
    } 
}
