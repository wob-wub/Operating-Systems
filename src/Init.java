public class Init extends UserProcess {
    Init() {
        super("Init");
    }

    @Override
    public void run() {
        // Wait until Kernel schedules Init for the first time.
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Ask Kernel to create the other user processes.
        Console console = new Console();
        Message message = new Message(0, KernelMessageType.createProcess.ordinal(), console);
        sendMessage(message);

        HelloWorld helloWorld = new HelloWorld();
        Message hWorld = new Message(0, KernelMessageType.createProcess.ordinal(), helloWorld);
        sendMessage(hWorld);

        GoodbyeWorld goodbyeWorld = new GoodbyeWorld();
        Message gWorld = new Message(0, KernelMessageType.createProcess.ordinal(), goodbyeWorld);
        sendMessage(gWorld);

        // Init is finished after requesting the other processes to be created.
        Message finalMessage = new Message(0, KernelMessageType.exit.ordinal());
        sendMessage(finalMessage);
    }
}