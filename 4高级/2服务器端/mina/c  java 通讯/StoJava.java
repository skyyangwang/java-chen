package util;

public class StoJava {
	byte[] receive = new byte[88]; 
	
	public String phoneid = "";  
    public String routeid = "";  
    public int method = 0;  
    
	public StoJava(String phoneid, String routeid, int method) {
		// TODO 自动生成的构造函数存根
		this.phoneid = phoneid;  
        this.routeid = routeid;  
        this.method = method; 
	}

	public static StoJava getStoJava(byte[] bArr) {  
		String phoneid = "";  
	    String routeid = "";  
	    int method = 0;  
        
	    //声明要提取字段的字节最大长度；
        byte[] temp1 = new byte[16];  
        byte[] temp2 = new byte[32]; 
        byte[] temp3 = new byte[4]; 
        
        //bArr-全字节数组；24-本字节开始坐标；temp-存放本字节的字节数组；0-本字节相对起始坐标；16-本字节最大坐标长度；
        System.arraycopy(bArr, 24, temp1, 0, 16);  
        phoneid = toStr(temp1,16);
        
        System.arraycopy(bArr, 40, temp2, 0, 32);  
        routeid = toStr(temp2,32);
        
        System.arraycopy(bArr, 72, temp3, 0, 4);  
        method = vtolh(temp3);  
          
        return new StoJava(phoneid, routeid, method);  
        
    }
	
	/** 
     * 将byte数组转化成String 
     */  
    private static String toStr(byte[] valArr,int maxLen) {  
        int index = 0;  
        while(index < valArr.length && index < maxLen) {  
            if(valArr[index] == 0) {  
                break;  
            }  
            index++;  
        }  
        byte[] temp = new byte[index];  
        System.arraycopy(valArr, 0, temp, 0, index);  
        return new String(temp);  
    }
    
    /** 
     * 将byte数组 转换为高字节在前，低字节在后的int
     */  
    private static int vtolh(byte[] bArr) {  
        int n = 0;  
        for(int i=0;i<bArr.length&&i<4;i++){  
            int left = i*8;  
            n+= (bArr[i] << left);  
        }  
        return n;  
    } 
}
