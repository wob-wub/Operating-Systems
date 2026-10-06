import java.util.Optional;

public class MessageQueue {
    private Message[] messages;
    private int head;
    private int tail;

    MessageQueue(){
        this.messages = new Message[100];
        this.head = 0;
        this.tail = 0;
    }

    protected boolean writeMessage(Message m){
        if((head + 1) % messages.length == tail){
            return false;
        }
        messages[head] = m;
        head = (head + 1) % messages.length;
        return true;
    }
    protected Optional<Message> getMessage(){
        if(head == tail){
            return Optional.empty();
        }
        Message m = messages[tail];
        tail = (tail + 1) % messages.length;
        return Optional.of(m);
    }
    protected boolean isEmpty(){
        return head == tail;
    }
    protected boolean isFull(){
        return (head + 1) % messages.length == tail;
    }

}
