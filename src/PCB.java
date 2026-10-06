import java.util.LinkedList;

public class PCB {
    // Stores the information Kernel needs to keep track of each process.
    Process process;
    int pid;
    static int nextPid = 0;
    LinkedList<Message> buffer;
    ProcessState state;

    PCB(Process process){
        this.process = process;
        this.pid = nextPid++;
        // Holds messages temporarily if the process inbox is full.
        buffer = new LinkedList<Message>();
        this.state = ProcessState.Runnable;
    }
}