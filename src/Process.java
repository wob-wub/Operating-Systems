import java.util.Optional;
import java.util.concurrent.Semaphore;

public class Process implements Runnable{
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
      thread.start();

  }
  protected void sendMessage(Message m){
      while(outbox.writeMessage(m) == false){
         try{
             outbox_Full.acquire();
         } catch (InterruptedException e) {
             throw new RuntimeException();
         }
      }
  }
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
}
