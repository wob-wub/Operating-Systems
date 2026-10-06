import java.util.Optional;

public class MessageQueue {
    // Circular queue used to store messages for a process.
    private Message[] messages;
    private int head;
    private int tail;

    MessageQueue(){
        this.messages = new Message[100];
        this.head = 0;
        this.tail = 0;
    }

    // Adds a message to the queue and returns false if the queue is full.
    protected boolean writeMessage(Message m){
        if((head + 1) % messages.length == tail){
            return false;
        }
        messages[head] = m;
        head = (head + 1) % messages.length;
        return true;
    }
    // Removes the next message and returns empty if there are no messages.
    protected Optional<Message> getMessage(){
        if(head == tail){
            return Optional.empty();
        }
        Message m = messages[tail];
        tail = (tail + 1) % messages.length;
        return Optional.of(m);
    }
    // Used to check the queue without adding or removing a message.
    protected boolean isEmpty(){
        return head == tail;
    }
    protected boolean isFull(){
        return (head + 1) % messages.length == tail;
    }

}
