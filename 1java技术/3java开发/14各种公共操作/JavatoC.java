package util;

public class JavatoC {
	
	private byte[] buf = new byte[28]; // 为说明问题，定死大小，事件中可以灵活处理
	
	public String name = "";  
    public int id = 0;  
    public float salary = 0;  
    
	public JavatoC(String name, int id, float salary) {  
	    this.name = name;  
	    this.id = id;  
	    this.salary = salary;  
	      
	    byte[] temp = name.getBytes();  
	    System.arraycopy(temp, 0, buf, 0, temp.length);  

	    temp = tolh(id);  
	    System.arraycopy(temp, 0, buf, 20, temp.length);  

	    temp = tolh(salary);  
	    System.arraycopy(temp, 0, buf, 24, temp.length);  
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


