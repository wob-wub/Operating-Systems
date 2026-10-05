public class Console extends UserProcess{
    Console (){
        super("Console");
    }
    @Override
    public void run() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(true){
           Message m = getMessage();
            System.out.println(m.data[0]);
            cooperate();
        }
    }
}
