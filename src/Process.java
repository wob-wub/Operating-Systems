import java.util.concurrent.Semaphore;

public class Process implements Runnable{
  Semaphore inbox_Full;
  Semaphore outbox_Full;
  Thread thread;
  String name;

  Process(String name){
      this.name = name;
      inbox_Full = new Semaphore(0);
      outbox_Full = new Semaphore(0);
      thread = new Thread(this);
      thread.start();

  }


}
