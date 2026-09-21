public class Kernel extends Process {
    PCB[] array;
    Kernel(PCB[] array){
        super("Kernel");
        this.array = array;
    }

    @Override
    public void run() {
        while(true){
            Message m = getMessage();
            if(m.what == KernelMessageType.createProcess.ordinal()){
                Process newProcess = (Process) m.data[0];
                PCB newPCB = new PCB(newProcess);
            }
        }
    }
}
