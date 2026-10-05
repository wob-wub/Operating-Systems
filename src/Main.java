public class Main {
    public static void main(String[] args) {
        Process[] processes = {new Init(), new Idle()};
        Kernel kernel = new Kernel(processes);
        kernel.startSystem();
    }
}