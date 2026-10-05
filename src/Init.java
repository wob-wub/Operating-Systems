public class Init extends UserProcess {
    Init(){
        super("Init");
    }

    @Override
    public void run() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Init Started");
        Console console = new Console();
        Message message = new Message(0,KernelMessageType.createProcess.ordinal(),console);
        sendMessage(message);

        System.out.println("Beep");

        HelloWorld helloWorld = new HelloWorld();
        Message hWorld = new Message(0,KernelMessageType.createProcess.ordinal(),helloWorld);
        sendMessage(hWorld);

        GoodbyeWorld goodbyeWorld = new GoodbyeWorld();
        Message gWorld = new Message(0,KernelMessageType.createProcess.ordinal(),goodbyeWorld);
        sendMessage(gWorld);

        Message finalMessage = new Message(0,KernelMessageType.exit.ordinal());
        sendMessage(finalMessage);


    }
}
