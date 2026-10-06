import java.util.Optional;
import java.util.concurrent.*;

public abstract class UserProcess extends Process {
    // Used by Kernel to stop and resume this process.
    Semaphore semaphore;
    // Backup message for Kernel when the normal outbox is full.
    volatile Message forKernel;
    // Keeps track of whether this process has used its time limit.
    volatile boolean timeUp = false;
    ScheduledExecutorService timer;
    // Keeps track of the timer so we can cancel the old one before starting another.
    ScheduledFuture<?> timerTask;

    UserProcess(String name){
        super(name);
        semaphore = new Semaphore(0);
        timer = Executors.newSingleThreadScheduledExecutor();
    }

    // Checks whether this process has used its CPU time quantum.
    // If time has expired, request rescheduling and pause until
    // Kernel releases the semaphore to allow execution again.
    protected void cooperate(){
        if(timeUp){
            Message m = new Message(0,KernelMessageType.reschedule.ordinal(),ProcessState.QuantumExpired);
            if(outbox.writeMessage(m) == false){
                forKernel = m;
            }
            try{
                semaphore.acquire();
            }
            catch (InterruptedException e){
                throw new RuntimeException();
            }
        }
    }

    // Starts the 100ms timer whenever Kernel allows the process to run.
    void startTimer() {

        // If there is already a timer, cancel it before starting another.
        if (timerTask != null) {
            timerTask.cancel(false);
        }

        // Reset timeUp since the process is getting another turn.
        timeUp = false;

        // After 100ms, timeUp becomes true so the process knows
        // it needs to give up its turn the next time it calls cooperate().
        timerTask = timer.schedule(
                () -> timeUp = true,
                100,
                TimeUnit.MILLISECONDS
        );
    }
    @Override
    protected void sendMessage(Message m){
        // Keep trying until the original message can be added.
        while (!outbox.writeMessage(m)) {

            // Create a separate message telling Kernel why we need to stop.
            Message request = new Message(0,
                    KernelMessageType.reschedule.ordinal(),
                    ProcessState.OutboxFull);

            // Store the request here since the normal outbox is already full.
            forKernel = request;


            // Wait until Kernel allows this process to continue.
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Gets a message from the inbox.
    // If there is nothing to read, we let Kernel know and wait.
    @Override
    protected Message getMessage() {
        Optional<Message> result = inbox.getMessage();
        while (result.isEmpty()) {
            // Let Kernel know that there is nothing in the inbox.
            Message request = new Message(0, KernelMessageType.reschedule.ordinal(), ProcessState.InboxEmpty);
            // If the outbox is full, use our backup message.
            if (!outbox.writeMessage(request)) {
                forKernel = request;
            }
            // Wait until Kernel allows this process to continue.
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Try reading the inbox again after waking up.
            result = inbox.getMessage();

        }
        // Return the message once we successfully receive one.
        return result.get();
    }
}
