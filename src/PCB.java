import java.util.LinkedList;

public class PCB {
    Process process;
    int pid;
    static int nextPid;
    LinkedList<Message> buffer;

    PCB(Process process){
        this.process = process;
        this.pid = nextPid;
        nextPid++;
        buffer = new LinkedList<Message>();
    }
}
