public class GoodbyeWorld extends UserProcess{

    GoodbyeWorld(){
        super("GoodbyeWorld");
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Message message = new Message(0, KernelMessageType.locate.ordinal(), "Console");
        sendMessage(message);
        message = getMessage();
        int consolePid = (Integer) message.data[0];
        for(int run = 1; run <= 99; run++){
            Message paste = new Message(consolePid,0, "Goodbye World " + run);
            sendMessage(paste);
            cooperate();
        }
        Message exitMessage = new Message(0,KernelMessageType.exit.ordinal());
        sendMessage(exitMessage);
    }
}
