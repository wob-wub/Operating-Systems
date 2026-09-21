public class Main {
    public static void main(String[] args) {
        Process[] processes = {new Console(),new HelloWorld(),new GoodbyeWorld()};
        Kernel kernel = new Kernel(processes);
    }
}