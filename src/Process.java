import java.util.Optional;
import java.util.concurrent.Semaphore;

public abstract class Process implements Runnable{
    // Basic information each process needs for messaging and running its thread.
    Semaphore inbox_Full;
    Semaphore outbox_Full;
    Thread thread;
    String name;
    MessageQueue inbox;
    MessageQueue outbox;

  Process(String name){
      this.name = name;
      inbox_Full = new Semaphore(0);
      outbox_Full = new Semaphore(0);
      inbox = new MessageQueue();
      outbox = new MessageQueue();
      thread = new Thread(this);
  }
    // Adds a message to the outbox and waits if there is no space.
    protected void sendMessage(Message m){
      while(!outbox.writeMessage(m)){
         try{
             outbox_Full.acquire();
         } catch (InterruptedException e) {
             throw new RuntimeException();
         }
      }
  }
    // Gets the next inbox message and waits if the inbox is empty.
    protected Message getMessage(){
      Optional<Message>result = inbox.getMessage();
      while(result.isEmpty()){
          try {
              inbox_Full.acquire();
          } catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          result = inbox.getMessage();
      }
      return result.get();
  }
    // Starts the process thread when the system is ready.
    protected void start(){
      this.thread.start();
  }

}
