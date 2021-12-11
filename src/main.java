import java.util.concurrent.Semaphore;
import java.util.Random;

public class main {
static final int BUFFER_SIZE = 10;
    static final int ROUND = 20 ;
    static int [] buffer = new int[BUFFER_SIZE];
    static int limit = ROUND;
    static int next_in = 0 , next_out = 0;
    static Semaphore emptyBuffer = new Semaphore(buffer.length);
    static Semaphore occupiedBuffer = new Semaphore(0);
    static int count = 0;
    static boolean done = false;
    static Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable(){
            public void run(){
                try {
                    producer();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(new Runnable(){
            public void run(){
                try {
                    consumer();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    public static void producer() throws InterruptedException{
        while (!done){
            int k1 = random.nextInt(BUFFER_SIZE/2)+1;
            for(int i = 0; i < k1-1; i++){
                if(emptyBuffer.availablePermits()>0){
                    emptyBuffer.acquire();
                    buffer[i] = 1;
                    occupiedBuffer.release();
                }else{
                    break;
                }
            }

            next_in = (next_in + k1) % buffer.length;
            System.out.println("So far producer OK " + next_in);
            limit--;
            if(limit <= 0){
                System.out.println("Producer exits system without any race problem.");
                done = true;
            }
            Thread.sleep((int) (Math.random()*900 + 100));
        }
    }

    public static void consumer() throws InterruptedException {
        while (!done){
            Thread.sleep((int) (Math.random()*900 + 100));
            int k2 = random.nextInt(BUFFER_SIZE/2) + 1;
            int data;
            for(int i = 0; i < k2-1; i++){
                occupiedBuffer.acquire();
                data=buffer[i];
                if(data != 1){
                    System.out.println("No value available in the buffer.");
                    occupiedBuffer.release();
                }

            }
            next_out = (next_out + k2) % buffer.length;
            System.out.println("So far consumer OK " + next_out);
            limit--;
            if(limit <= 0) {
                System.out.println("Consumer exits system without any race problem.");
                done = true;
            }
            Thread.sleep((int) (Math.random()*900 + 100));
        }
    }
}