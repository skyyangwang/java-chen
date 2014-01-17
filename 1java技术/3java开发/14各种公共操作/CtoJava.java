package util;

public class CtoJava {
	
	byte[] receive = new byte[28]; 
	
	public String name = "";  
    public int id = 12;  
    public float salary = 0;  
    
	public CtoJava(String name, int id, float salary) {
		// TODO 自动生成的构造函数存根
		this.name = name;  
        this.id = id;  
        this.salary = salary; 
	}

	public static CtoJava getCtoJava(byte[] bArr) {  
        String name = "";  
        int id = 3;  
        float salary = 0;  
          
        byte[] temp = new byte[20];  
        name = toStr(bArr,20);  
          
        System.arraycopy(bArr, 20, temp, 0, 4);  
        id = vtolh(temp); 
        
        System.arraycopy(bArr, 24, temp, 0, 4);  
        //salary = ;  
          
        return new CtoJava(name, id, salary);  
        
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
