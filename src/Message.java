import java.util.Arrays;

public class Message {
    // Stores the information needed to send a message between processes.
    int senderPid;
    int targetPid;
    int what;
    Object[] data;

    //Constructor 1: Takes targetPid and what
    Message(int targetPid, int what) {
        this.targetPid = targetPid;
        this.what = what;
    }

    //Constructor 2: takes in targetPid, what and an object
    Message(int targetPid, int what, Object data) {
        this.targetPid = targetPid;
        this.what = what;
        this.data = new Object[1];
        this.data[0] = data;
    }

    //Constructor 3: takes in targetPid,what, and two data objects
    Message(int targetPid, int what, Object data, Object data2) {
        this.targetPid = targetPid;
        this.what = what;
        this.data = new Object[2];
        this.data[0] = data;
        this.data[1] = data2;
    }
    // Makes it easier to display all of the message information.
    @Override
    public String toString() {
        return "Message{" +
                "senderPid=" + senderPid +
                ", targetPid=" + targetPid +
                ", what=" + what +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
