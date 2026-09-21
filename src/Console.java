public class Console extends Process{
    Console (){
        super("Console");
    }
    @Override
    public void run() {
        while(true){
           Message m = getMessage();
            System.out.println(m.data[0]);
        }
    }
}
