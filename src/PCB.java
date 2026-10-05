import java.util.LinkedList;

public class PCB {
    Process process;
    int pid;
    static int nextPid = 0;
    LinkedList<Message> buffer;
    ProcessState state;

    PCB(Process process){
        this.process = process;
        this.pid = nextPid++;
        buffer = new LinkedList<Message>();
        this.state = ProcessState.Runnable;
    }
}
