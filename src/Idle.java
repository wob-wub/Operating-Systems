import static java.lang.Thread.sleep;

public class Idle extends UserProcess {
    Idle(){
        super("Idle");
    }
    @Override
    public void run() {
        // Wait until Kernel schedules Idle for the first time.
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Keep cooperating so Kernel can schedule another process when needed.
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
