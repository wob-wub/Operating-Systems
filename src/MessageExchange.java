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
        while(true){
            for(int run = 0; run < array.length; run++){
                if(array[run] != null){
                    Optional<Message> result = array[run].process.outbox.getMessage();
                    if(result.isPresent()){
                        Message m = result.get();
                        array[run].process.outbox_Full.release();
                        m.senderPid = array[run].pid;
                        for(int target = 0; target < array.length;target++){
                            if(array[target] != null && array[target].pid == m.targetPid){
                                boolean delivered = array[target].process.inbox.writeMessage(m);
                                if(!delivered){
                                    array[target].buffer.add(m);
                                }
                                else{
                                    array[target].process.inbox_Full.release();
                                }
                                break;
                            }
                        }
                    }
                    if(!array[run].buffer.isEmpty()){
                        Message bufferedMessage = array[run].buffer.peek();
                        boolean delivered = array[run].process.inbox.writeMessage(bufferedMessage);
                        if(delivered){
                            array[run].buffer.poll();
                            array[run].process.inbox_Full.release();
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
}
