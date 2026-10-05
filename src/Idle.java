import static java.lang.Thread.sleep;

public class Idle extends UserProcess {
    Idle(){
        super("Idle");
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        while(true){
            cooperate();
            try {
                sleep(1);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    }
}
