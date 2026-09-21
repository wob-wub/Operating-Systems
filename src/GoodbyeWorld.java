public class GoodbyeWorld extends Process{

    GoodbyeWorld(){
        super("GoodbyeWorld");
    }

    @Override
    public void run() {
        Message message = new Message(0, KernelMessageType.locate.ordinal(), "Console");
        sendMessage(message);
        message = getMessage();
        int consolePid = (Integer) message.data[0];
        System.out.println("Goodbye World Locates Console, pid: " + consolePid);
        for(int run = 1; run <= 99; run++){
            Message paste = new Message(consolePid,0, "Goodbye World " + run);
            sendMessage(paste);
        }
    }
}
