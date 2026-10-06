import java.util.Optional;

public class MessageExchange extends Process{

    PCB[] array;
    MessageExchange(PCB[] array){
        super("MessageExchange");
        this.array = array;
    }

    public void run(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Continuously check each process for messages that need to be delivered.
        while(true){
            for(int run = 0; run < array.length; run++){
                if(array[run] != null){
                    // Check if this user process has a message waiting for Kernel.
                    if (array[run].process instanceof UserProcess) {
                        UserProcess user = (UserProcess) array[run].process;
                        if (user.forKernel != null){
                            // Take the emergency message and clear the backup.
                            Message emergency = user.forKernel;
                            user.forKernel = null;

                            // Tell Kernel which process sent this request.
                            emergency.senderPid = array[run].pid;

                            // Send the emergency message through our delivery helper.
                            deliverMessage(emergency);
                        }
                    }
                    Optional<Message> result = array[run].process.outbox.getMessage();
                    if(result.isPresent()){
                        Message m = result.get();

                        // Let the process know that space was freed in its outbox.
                        array[run].process.outbox_Full.release();
                        // If this process was waiting for outbox space,
                        // it can now be scheduled again.
                        if (array[run].state == ProcessState.OutboxFull) {
                            array[run].state = ProcessState.Runnable;
                        }
                        // Set the sender PID before delivering the message.
                        m.senderPid = array[run].pid;

                        // Deliver the message to the correct process.
                        deliverMessage(m);
                    }
                    // Retry messages that were saved because the process inbox was full.
                    if(!array[run].buffer.isEmpty()){
                        Message bufferedMessage = array[run].buffer.peek();
                        boolean delivered = array[run].process.inbox.writeMessage(bufferedMessage);
                        if(delivered){
                            array[run].buffer.poll();
                            array[run].process.inbox_Full.release();
                            // A buffered message has entered the inbox,
                            // so a process waiting for a message can run again.
                            if (array[run].state == ProcessState.InboxEmpty) {
                                array[run].state = ProcessState.Runnable;
                            }
                        }
                    }
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Finds the process that should receive the message.
    // If its inbox is full, we save the message in its PCB buffer.
    private void deliverMessage(Message m){
        for(int target = 0; target < array.length;target++){
            if(array[target] != null && array[target].pid == m.targetPid){
                boolean delivered = array[target].process.inbox.writeMessage(m);
                if(!delivered){
                    array[target].buffer.add(m);
                }
                else{
                    array[target].process.inbox_Full.release();
                    // If this process was waiting for a message,
                    // it can now be scheduled again.
                    if (array[target].state == ProcessState.InboxEmpty) {
                        array[target].state = ProcessState.Runnable;
                    }
                }
                break;
            }
        }

    }
}
