public class Main {
    public static void main(String[] args) {
        // Start the system with Init and Idle as the first user processes.
        Process[] processes = {new Init(), new Idle()};
        Kernel kernel = new Kernel(processes);
        kernel.startSystem();
    }
}