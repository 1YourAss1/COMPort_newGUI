import jssc.*;

public class App {
    public static void main(String[] args) {
        String[] portNames = SerialPortList.getPortNames();
        GUI gui = new GUI(portNames);
    }
}
