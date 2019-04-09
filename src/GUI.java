import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame{
    private JPanel mainPanel;
    private JPanel nakalPanel;
    private JTextField nakalCurrentTextField;
    private JTextField nakalVoltageTextField;
    private JTextField nakalControlTextField;
    private JPanel katodPanel;
    private JPanel highEnergyPanel;
    private JPanel lowEnergyPanel;
    private JTextField katodVoltageTextField;
    private JTextField katodControlTextField;
    private JTextField katodCurrentTextField;
    private JTextField highEnergyVoltageTextField;
    private JTextField highEnergyControlTextField;
    private JTextField highEnergyCurrentTextField;
    private JTextField lowEnergyVoltageTextField;
    private JTextField lowEnergyControlTextField;
    private JTextField lowEnergyCurrentTextField;
    private JButton ВКЛButton;
    private JButton connectionButton;
    private JButton disconnectionButton;
    private JMenuBar menuBar;

    private static SerialPort serialPort;
    private String port;

    public GUI(String ports[]) {
        menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("Настройки");
        JMenuItem connectItem = new JMenuItem("Порт");
        connectItem.addActionListener(new ChooseListener(ports));
        optionsMenu.add(connectItem);
        menuBar.add(optionsMenu);
        setJMenuBar(menuBar);
        connectionButton.addActionListener(new ConnectionButtonListener());
        disconnectionButton.addActionListener(new DisconnectionButtonListener());
        setTitle("Вольтметр 0.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(mainPanel);
        setSize(900, 600);
        setVisible(true);
    }

    public void outValue(int x[]) {
        //double voltage = x / 818.6;// / 201.0;
        //String voltageFormat = String.format("%.3f", voltage);
        nakalVoltageTextField.setText(String.valueOf(x[0]));
        nakalCurrentTextField.setText(String.valueOf(x[1]));
        nakalControlTextField.setText(String.format("%.3f", x[2] / 1612.9));

        katodVoltageTextField.setText(String.valueOf(x[3]));
        katodCurrentTextField.setText(String.valueOf(x[4]));
        katodControlTextField.setText(String.valueOf(x[5]));

        highEnergyVoltageTextField.setText(String.valueOf(x[6]));
        highEnergyCurrentTextField.setText(String.valueOf(x[7]));
        /*highEnergyControlTextField.setText(voltageFormat);

        lowEnergyVoltageTextField.setText(voltageFormat);
        lowEnergyCurrentTextField.setText(voltageFormat);
        lowEnergyControlTextField.setText(voltageFormat);*/
    }
    public void ErrorWindow(String error) {
        JFrame frame = new JFrame("Error");
        JLabel errorLabel = new JLabel(error);
        frame.getContentPane().add(errorLabel);
        frame.setSize(650,200);
        frame.setVisible(true);
    }

    public class ChooseListener implements ActionListener {
        String portNames[];
        public ChooseListener(String port[]){
            portNames = port;
        }
        JComboBox comboBox;
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame chooseFrame = new JFrame("Порт");
            chooseFrame.setSize(300, 250);
            chooseFrame.setVisible(true);
            comboBox = new JComboBox(portNames);
            JButton chooseButton = new JButton("Выбрать");
            chooseButton.addActionListener(new ChooseButtonListener());
            JPanel mainPanel = new JPanel();
            JPanel p = new JPanel();
            p.setLayout(new GridLayout(5, 1));
            p.add(new JPanel());
            p.add(new JLabel("Порт:"));
            p.add(comboBox);
            p.add(new JLabel());
            p.add(chooseButton);
            mainPanel.add(p);
            chooseFrame.getContentPane().add(mainPanel);
        }

        public class ChooseButtonListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                port = String.valueOf(comboBox.getSelectedItem());
            }
        }
    }

    public class  ConnectionButtonListener implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            serialPort = new SerialPort(port);
            try {
                serialPort.openPort();
                Thread.sleep(5000);
                serialPort.setParams(38400, 8, 1, 0);
                serialPort.setEventsMask(SerialPort.MASK_RXCHAR);
                serialPort.addEventListener((SerialPortEvent event) -> {
                    if (event.isRXCHAR()) {
                        try {
                            byte[] b = serialPort.readBytes(16);
                            int value[] = new int[8];
                            if (b != null) {
                                for (int i = 0; i < 8; i++) {
                                    value[i] =  ((b[i*2] & 0xFF) << 8) + (b[i*2 + 1] & 0xFF);
                                }
                                outValue(value);
                            }
                        } catch (SerialPortException ex) {
                            System.out.println(ex);
                        }
                    }
                });
            } catch (SerialPortException ex) {
                ErrorWindow(ex.toString());
                System.out.println(ex);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public class DisconnectionButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                serialPort.closePort();
                nakalVoltageTextField.setText("00.00");
                nakalCurrentTextField.setText("00.00");
                nakalControlTextField.setText("00.00");

                katodVoltageTextField.setText("00.00");
                katodCurrentTextField.setText("00.00");
                katodControlTextField.setText("00.00");

                highEnergyVoltageTextField.setText("00.00");
                highEnergyCurrentTextField.setText("00.00");
                highEnergyControlTextField.setText("00.00");

                lowEnergyVoltageTextField.setText("00.00");
                lowEnergyCurrentTextField.setText("00.00");
                lowEnergyControlTextField.setText("00.00");
            } catch (SerialPortException ex) {
                System.out.println(ex);
            }
        }
    }
}
