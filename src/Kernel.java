public class Kernel extends Process {
    PCB[] array;
    MessageExchange messageExchange;
    // Keeps track of the last process Kernel allowed to run.
    private int lastScheduled = 1;
    Kernel(Process[] processes){
        super("Kernel");
        this.array = new PCB[100];
        PCB kernelPCB = new PCB(this);
        array[0] = kernelPCB;
        messageExchange = new MessageExchange(array);
        PCB messageExchangePCB = new PCB(messageExchange);
        array[1] = messageExchangePCB;
        for(int loop = 0; loop < processes.length ; loop++){
            PCB newPCB = new PCB(processes[loop]);
            array[loop + 2] = newPCB;
        }
    }
    public void startSystem(){
        messageExchange.start();
        for(int loop = 2; loop < array.length; loop++){
            if(array[loop] != null){
                array[loop].process.start();
            }
        }
        this.start();
    }

    @Override
    public void run() {
        // Start the first user process before Kernel begins handling messages.
        scheduleNext();
        while(true){
            Message m = getMessage();
            // Create a new process, give it a PCB, and place it in an open spot.
            if(m.what == KernelMessageType.createProcess.ordinal()){
                Process newProcess = (Process) m.data[0];
                PCB newPCB = new PCB(newProcess);
                for(int run = 0; run < array.length; run++){
                    if(array[run] == null){
                       array[run] = newPCB;
                       newProcess.start();
                        break;
                    }
                }
            }
            // Find a process by name and send its PID back to the process that asked.
            else if(m.what == KernelMessageType.locate.ordinal()){
                String nameProcess = m.data[0].toString();
                for(int run = 0; run < array.length ; run++){
                    if(array[run] != null){
                        if(array[run].process.name.equals(nameProcess)){
                            Message message = new Message(m.senderPid,KernelMessageType.locate.ordinal(),array[run].pid);
                            sendMessage(message);
                            break;
                        }
                    }
                }
            }
            // Update the process state based on why it stopped, then choose the next process.
            else if(m.what == KernelMessageType.reschedule.ordinal()){
                ProcessState reason = (ProcessState) m.data[0];
                for(int run = 0; run < array.length; run++){
                    if(array[run] != null && array[run].pid == m.senderPid){
                        if(reason == ProcessState.QuantumExpired){
                            array[run].state = ProcessState.Runnable;
                        }
                        // Check the queue again in case it changed before Kernel handled the request.
                        else if(reason == ProcessState.InboxEmpty){
                            if(array[run].process.inbox.isEmpty()){
                                array[run].state = ProcessState.InboxEmpty;
                            }
                            else{
                                array[run].state = ProcessState.Runnable;
                            }
                        }
                        else if(reason == ProcessState.OutboxFull){
                            if(array[run].process.outbox.isFull()){
                                array[run].state = ProcessState.OutboxFull;
                            }
                            else{
                                array[run].state = ProcessState.Runnable;
                            }
                        }

                        break;
                    }
                }

                scheduleNext();
            }
            // Remove a finished process from the PCB array and schedule another process.
            else if (m.what == KernelMessageType.exit.ordinal()) {
                for(int run = 0; run < array.length;run++){
                    if(array[run] != null && array[run].pid == m.senderPid){
                        array[run] = null;
                        break;
                    }

                }
                scheduleNext();
            }
        }
    }
    private void scheduleNext(){
        // Go through the PCB array to find the next process that can run.
        for (int count = 0; count < array.length; count++) {
            // Start after the last scheduled process and wrap around if needed.
            int index = (lastScheduled + 1 + count) % array.length;
            // Skip empty slots and anything that is not a user process.
            if (array[index] == null ||
                    !(array[index].process instanceof UserProcess)) {
                continue;
            }
            // Only choose a process that is ready to run.
            if (array[index].state == ProcessState.Runnable) {
                lastScheduled = index;
                UserProcess user = (UserProcess) array[index].process;
                user.startTimer();
                user.semaphore.release();
                return;
            }

        }

    }
}
