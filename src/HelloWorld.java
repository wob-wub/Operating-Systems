public class HelloWorld extends UserProcess{
    HelloWorld(){
        super("HelloWorld");
    }

    @Override
    public void run() {
        // Wait until Kernel schedules HelloWorld for the first time.
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Ask Kernel for the PID of the Console process.
        Message message = new Message(0, KernelMessageType.locate.ordinal(), "Console");
        sendMessage(message);
        message = getMessage();
        int consolePid = (Integer) message.data[0];

        // Send each Hello World message to Console and cooperate with Kernel.
        for(int run = 1; run <= 99; run++){
            Message paste = new Message(consolePid, 0, "Hello World " + run);
            sendMessage(paste);
            cooperate();
        }
        // Tell Kernel that HelloWorld is finished.
        Message toKernel = new Message(0, KernelMessageType.exit.ordinal());
        sendMessage(toKernel);
    }
}