package util;

public class Test1 implements Runnable{

	public void run() {
		long start=System.currentTimeMillis();
        
        for(int i=0;i<sum;++i){
            synchronized (this) {
                if(count>0){
                    try{
                        Thread.sleep(100);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                    //System.out.println(count--);
                    System.out.println(Thread.currentThread().getName()+"正在卖票"+count--);
                }
            }
        }
        
        long time = System.currentTimeMillis() - start;
        System.out.println("运行耗时= "+time+" 毫秒");
    }
 
    public static void main(String[] args) {
    	Test1 he=new Test1();
        Thread h1=new Thread(he);
        Thread h2=new Thread(he);
        Thread h3=new Thread(he);
        Thread h4=new Thread(he);
        Thread h5=new Thread(he);
        Thread h6=new Thread(he);
        Thread h7=new Thread(he);
        Thread h8=new Thread(he);
        Thread h9=new Thread(he);
        Thread h10=new Thread(he);
     
        h1.start();
        h2.start();
        h3.start();
        h4.start();
        h5.start();
        h6.start();
        h7.start();
        h8.start();
        h9.start();
        h10.start();
        
    }
    private int count=100;
    private int sum=100;

}

