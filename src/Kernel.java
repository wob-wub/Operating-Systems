public class Kernel extends Process {
    PCB[] array;
    Kernel(Process[] processes){
        super("Kernel");
        this.array = new PCB[100];
        PCB kernelPCB = new PCB(this);
        array[0] = kernelPCB;
        MessageExchange newME = new MessageExchange(array);
        PCB messageExchangePCB = new PCB(newME);
        array[1] = messageExchangePCB;
        for(int loop = 0; loop < processes.length ; loop++){
            PCB newPCB = new PCB(processes[loop]);
            array[loop + 2] = newPCB;
        }

    }

    @Override
    public void run() {
        while(true){
            Message m = getMessage();
            if(m.what == KernelMessageType.createProcess.ordinal()){
                Process newProcess = (Process) m.data[0];
                PCB newPCB = new PCB(newProcess);
                for(int run = 0; run < array.length; run++){
                    if(array[run] == null){
                       array[run] = newPCB;
                        break;
                    }
                }
            }
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
        }
    }
}
