package activemq.ui;

import activemq.client.Client;
import activemq.client.CustomMessageListener;
import activemq.client.MQConnection;
import sun.management.ManagementFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.TopicSubscriber;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: lucky
 * Date: 12/9/13
 * Time: 12:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainNewUI extends JFrame {

    private static String processId = null;
    private LinkedList<Thread> threads = new LinkedList();
    private JTabbedPane tabbedPane = new JTabbedPane();
    JFileChooser fileChooser = new JFileChooser();
    private JPanel connectionPanel = new JPanel();
    private JPanel sendPanel = new JPanel();
    private JPanel receivePanel = new JPanel();

    //passwordFiled panel
    private String userName = "admin";
    private String password = "admin";
    private JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//    private JLabel userLabel = new JLabel("Username");
    private JLabel userLabel = new JLabel("Usuario");
//    private JLabel passwordL = new JLabel("Password");
    private JLabel passwordL = new JLabel("Contraseña");
    private JTextField user = new JTextField(6);
    private JPasswordField passwordFiled = new JPasswordField(6);
//    private JButton login = new JButton("Login");
    private JButton login = new JButton("Login");
//    private JButton logout = new JButton("Logout");
    private JButton logout = new JButton("Logout");
//    private JButton changePassword = new JButton("Change Password");
    private JButton changePassword = new JButton("Cambiar contraseña");

    private Cipher encCipher = null;
    private Cipher decCipher = null;
    BASE64Decoder decoder = new BASE64Decoder();
    BASE64Encoder encoder = new BASE64Encoder();

    //connection details -UI

    //        private JLabel serverLabel = new JLabel("Server Name/IP");
    private JLabel serverLabel = new JLabel("Nombre del servidor / IP");
    //        private JLabel portLabel = new JLabel("Port");
    private JLabel portLabel = new JLabel("Port");
    //        private JLabel failoverLabel = new JLabel("Is Failover?");
    private JLabel failoverLabel = new JLabel("Is Failover?");
    private JLabel urlLabel = new JLabel("Dirección URL");

    private JTextField serverField = new JTextField("localhost");
    private JTextField portField = new JTextField("61617");
    private JCheckBox IsFailoverBox = new JCheckBox();
    private JTextField connectionUrlField = new JTextField("tcp://localhost:61617");

    //        private JButton startButton = new JButton("Start");
    private JButton startButton = new JButton("iniciar");
    //        private JButton stopBUtton = new JButton("Stop");
    private JButton stopBUtton = new JButton("parar");
    //        private JButton closeButton = new JButton("Close");
    private JButton closeButton = new JButton("cerrar");

    private JPanel buttonPanel = new JPanel(new GridBagLayout());

    private long lastStart = 0;


    // SendPanel -- UI

    //        private JCheckBox checkbox1 = new JCheckBox("Send Messages");
    private JCheckBox checkbox1 = new JCheckBox("Enviar Mensajes");
    //        private JLabel topicNameLabel = new JLabel("Topic Name");
    private JLabel topicNameLabel = new JLabel("Nombre del Topic");
    //        private JLabel sendFolderPathLabel = new JLabel("Folder Path");
    private JLabel sendFolderPathLabel = new JLabel("Directorio");

    private JTextField sendTopicField = new JTextField("stadapter2");
    private JTextField sendFolderPath = new JTextField("C:\\Sistema\\Sincronizador\\Enviar");
    private JButton sendFolderButton = new JButton("..");
    private JPanel filePanel = new JPanel(new BorderLayout());

    // Recv Panel --UI
    //        private JCheckBox checkbox1 = new JCheckBox("Receive Messages");
    private JCheckBox checkbox2 = new JCheckBox("Recibir Mensajes");
    //        private JLabel clientLabel = new JLabel("Client ID");
    private JLabel clientLabel = new JLabel("ID del Cliente");
    //        private JLabel topicNameLabel = new JLabel("Topic Name");
    private JLabel recvTopicNameLabel = new JLabel("Nombre del Topic");
    //        private JLabel sendFolderPathLabel = new JLabel("Folder Path");
    private JLabel folderPathLabel = new JLabel("Directorio");

    private JTextField clientID = new JTextField("infrasistemas");
//    private JTextField recvTopicName = new JTextField("stuadapter");
    private JTextField recvTopicName = new JTextField("stadapter");
    private JTextField recvFolderPath = new JTextField("C:\\Sistema\\Sincronizador\\Recibir");
    private JButton recvButton = new JButton("..");
    private JPanel recvfilePanel = new JPanel(new BorderLayout());


    //Logs UI
    private JPanel logsPanel = new JPanel();
    private JTextArea textArea = new CustomJTextArea();

    //Configuration Panel
    private JPanel configPanel = new JPanel();
    //    private JCheckBox autoStart = new JCheckBox("Auto-Start");
    private JCheckBox autoStart = new JCheckBox("Auto-Iniciar");
    //    private JLabel restartTimeButton = new JLabel("Reiniciar la conexión después de: XX horas");
//    private JTextField restartTime = new JTextField("6",10);
    private JLabel monitoringDirLabel = new JLabel("Directorio de Monitoreo");
    private JTextField monitorDir = new JTextField("C:\\Sistema\\Sincronizador", 20);
    private JButton dirButton = new JButton("..");
    //    private JLabel fileNameLabel = new JLabel("Cerrar Nombre de archivo");
    private JLabel fileNameLabel = new JLabel("Archivo de reinicio");
    //    private JLabel fileNameLabel1 = new JLabel("Resetear Nombre del archivo");
    private JLabel fileNameLabel1 = new JLabel("Archivo de actualizar");
    private JTextField file = new JTextField("reinicio.txt", 10);
//    private JTextField file1 = new JTextField("actualizar.txt", 10);
    private JTextField file1 = new JTextField("reinicio.txt", 10);


    private MQConnection connection = new MQConnection();
    private Client client = null;
    private CustomMessageListener listener = null;
    private TopicSubscriber subscriber = null;

    private JCheckBox restart = new JCheckBox();

    private SystemTray systemTray = null;

    public MainNewUI() throws HeadlessException {
        //setting image icon to the frame
        setIconImage(new ImageIcon("agente.png").getImage());
        setLayout(new BorderLayout());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        createCiphers();
        createUI();
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
        }else{
            systemTray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(new ImageIcon("agente.jpg").getImage(), "Sincronizador Active MQ");
            try {
                systemTray.add(trayIcon);
            } catch (AWTException e) {
                System.out.println("TrayIcon could not be added.");
            }
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() ==2){
                        setVisible(true);
                        setState(Frame.NORMAL);
                    }
                }
            });
        }
        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                if(e.getNewState() == Frame.ICONIFIED){
                    setVisible(false);
                }
            }
        });
    }

    private String decode(String dcdString){
        if(dcdString != null || decCipher != null){
        try {
            byte[] base641 = decoder.decodeBuffer(dcdString);
            return (new String(decCipher.doFinal(base641), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        }
        return dcdString;
    }

    private String encode(String dcdString){
        if(dcdString != null || decCipher != null){
            try {
                byte[] raw = encCipher.doFinal(dcdString.getBytes("utf-8"));
                return encoder.encode(raw);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        return dcdString;
    }

    private void createCiphers() {

        try {
            KeyGenerator generator = KeyGenerator.getInstance("DES");
            generator.init(new SecureRandom("ACTIVEMQ_KENT".getBytes("utf-8")));
            Key key = generator.generateKey();

            encCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            encCipher.init(Cipher.ENCRYPT_MODE, key);
            decCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            decCipher.init(Cipher.DECRYPT_MODE, key);


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void createUI() {

        userPanel.setLayout(new GridBagLayout());
        userPanel.add(userLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 20, 1, 1), 1, 1));
        userPanel.add(user, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        userPanel.add(passwordL, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 20, 1, 1), 1, 1));
        userPanel.add(passwordFiled, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
        JPanel loginBPabel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.add(loginBPabel, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!userName.equals(user.getText()) || !password.equals(passwordFiled.getText())) {
                    JOptionPane.showMessageDialog(((JButton) e.getSource()).getParent(), "Usuario or Contraseña is wrong.");
                } else {
                    tabbedPane.setEnabledAt(1, true);
                    tabbedPane.setEnabledAt(2, true);
                    tabbedPane.setEnabledAt(3, true);
                    tabbedPane.setEnabledAt(4, true);
                    passwordFiled.setText("");
                    user.setText("");
                    logout.setEnabled(true);
                    login.setEnabled(false);
                    changePassword.setEnabled(true);
                    repaint();
                }
            }
        });
        logout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tabbedPane.setEnabledAt(1, false);
                tabbedPane.setEnabledAt(2, false);
                tabbedPane.setEnabledAt(3, false);
                tabbedPane.setEnabledAt(4, false);
                passwordFiled.setText("");
                user.setText("");
                login.setEnabled(true);
                changePassword.setEnabled(false);
                logout.setEnabled(false);
                repaint();
            }
        });
        changePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JPasswordField jpf = new JPasswordField(30);
                JPanel messagePanel = new JPanel();
                JLabel label = new JLabel("Vieja contraseña:");
                messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
//                messagePanel.add(new JLabel("Old Password"));
                messagePanel.add(label);
                messagePanel.add(jpf);

//                String oldPass = JOptionPane.showInputDialog(((JButton)e.getSource()).getParent(), "Old Password:");
//                String oldPass = JOptionPane.showInputDialog(((JButton)e.getSource()).getParent(), "Vieja contraseña:");
                int result = JOptionPane.showConfirmDialog(((JButton)e.getSource()).getParent(), messagePanel, "Vieja contraseña:", JOptionPane.OK_CANCEL_OPTION);
                if(result == JOptionPane.OK_OPTION){
                String oldPass = jpf.getText();
                if(password.equals(oldPass)){
                    jpf.setText("");
                    label.setText("Nueva contraseña:");
//                    String newPass = JOptionPane.showInputDialog(((JButton)e.getSource()).getParent(), "New Password:");
//                    String newPass = JOptionPane.showInputDialog(((JButton)e.getSource()).getParent(), "Nueva contraseña:");
                    result = JOptionPane.showConfirmDialog(((JButton)e.getSource()).getParent(), messagePanel, "Nueva contraseña:", JOptionPane.OK_CANCEL_OPTION);
                    if(result == JOptionPane.OK_OPTION){
                        String newPass = jpf.getText();
                        if(newPass != null){
                        password = newPass;
//                        JOptionPane.showMessageDialog(((JButton)e.getSource()).getParent(), "Password changed successfully");
                        JOptionPane.showMessageDialog(((JButton)e.getSource()).getParent(), "Contraseña cambiada correctamente");
                    }
                    }
                }else{
//                    JOptionPane.showMessageDialog(((JButton)e.getSource()).getParent(), "Wrong Password");
                    JOptionPane.showMessageDialog(((JButton)e.getSource()).getParent(), "Contraseña incorrecta");
                }
                }
            }
        });
        loginBPabel.add(login);
        loginBPabel.add(logout);
        loginBPabel.add(changePassword);
        logout.setEnabled(false);
        changePassword.setEnabled(false);
//        tabbedPane.addTab("Login Panel", null, userPanel, "Login Panel");
        tabbedPane.addTab("Login", null, userPanel, "Login");

//        tabbedPane.addTab("Connection Details", null, connectionPanel, "Connection Configuration");
        tabbedPane.addTab("Detalles de la Conexión", null, connectionPanel, "Detalles de la Conexión");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);


//        tabbedPane.addTab("Sender Configuration", null, sendPanel, "Sender Configuration");
        tabbedPane.addTab("Configuración para Enviar", null, sendPanel, "Configuración para Enviar");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);


//        tabbedPane.addTab("Receiver Configuration", null, receivePanel, "Receiver Configuration");
        tabbedPane.addTab("Configuración para Recibir", null, receivePanel, "Configuración para Recibir");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

        connectionPanel.setLayout(new GridBagLayout());
        connectionPanel.add(serverLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 20, 1, 1), 1, 1));
        connectionPanel.add(serverField, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        connectionPanel.add(portLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 20, 1, 1), 1, 1));
        connectionPanel.add(portField, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        connectionPanel.add(failoverLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 20, 1, 1), 1, 1));
        connectionPanel.add(IsFailoverBox, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        connectionPanel.add(urlLabel, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(1, 20, 1, 1), 1, 1));
        connectionPanel.add(connectionUrlField, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        buttonPanel.add(startButton, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 1, 1));
        buttonPanel.add(stopBUtton, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 1, 1));
        buttonPanel.add(closeButton, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(1, 20, 1, 1), 1, 1));

        connectionPanel.add(buttonPanel, new GridBagConstraints(0, 4, 2, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(20, 20, 20, 1), 1, 1));


        sendPanel.setLayout(new GridBagLayout());
        sendPanel.add(checkbox1, new GridBagConstraints(0, 0, 2, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));

        sendPanel.add(topicNameLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
        sendPanel.add(sendTopicField, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        sendPanel.add(sendFolderPathLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 100, 1), 1, 1));
        sendPanel.add(filePanel, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 100, 20), 1, 1));

        filePanel.add(sendFolderPath, BorderLayout.CENTER);
        filePanel.add(sendFolderButton, BorderLayout.EAST);

        sendFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(MainNewUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null)
                        sendFolderPath.setText(selectedFile.getAbsolutePath());
                }
            }
        });


        receivePanel.setLayout(new GridBagLayout());
        receivePanel.add(checkbox2, new GridBagConstraints(0, 0, 2, 1, 2, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));

        receivePanel.add(clientLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
        receivePanel.add(clientID, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        receivePanel.add(recvTopicNameLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
        receivePanel.add(recvTopicName, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        receivePanel.add(folderPathLabel, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 70, 1), 1, 1));
        receivePanel.add(recvfilePanel, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 70, 20), 1, 1));

        recvfilePanel.add(recvFolderPath, BorderLayout.CENTER);
        recvfilePanel.add(recvButton, BorderLayout.EAST);

        recvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(MainNewUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null)
                        recvFolderPath.setText(selectedFile.getAbsolutePath());
                }
            }
        });


//        logsPanel.setLayout(new BorderLayout());
//        JScrollPane jScrollPane = new JScrollPane(textArea);
//
//        jScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
//        jScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
//        textArea.setEnabled(false);
//        tabbedPane.addTab("Registros", null, jScrollPane, "Registros");
        configPanel.setLayout(new GridBagLayout());
//        configPanel.setSize(200,300);
        configPanel.add(autoStart, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));

        configPanel.add(monitoringDirLabel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
        configPanel.add(monitorDir, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
        configPanel.add(dirButton, new GridBagConstraints(2, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
        configPanel.add(fileNameLabel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
//        configPanel.add(file, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
        configPanel.add(file1, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
//        configPanel.add(fileNameLabel1, new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
//        configPanel.add(file1, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        dirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(MainNewUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null)
                        monitorDir.setText(selectedFile.getAbsolutePath());
                }
            }
        });


        //dummy label to look UI good
        configPanel.add(new Label(), new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
        configPanel.add(new Label(), new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));
        configPanel.add(new Label(), new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 20, 1, 1), 1, 1));
        configPanel.add(new Label(), new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 20), 1, 1));

        tabbedPane.addTab("Configuración", null, configPanel, "Configuración");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);

        autoStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (autoStart.isSelected())
                    startButtonActionPerformed(e);
            }
        });
        add(tabbedPane);


        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//        setTitle("Active MQ Client");
        setTitle("Sincronizador Active MQ");
        serverField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                updateURL();
            }

            public void removeUpdate(DocumentEvent e) {
                updateURL();
            }

            public void changedUpdate(DocumentEvent e) {
                updateURL();
            }
        });
        portField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                updateURL();
            }

            public void removeUpdate(DocumentEvent e) {
                updateURL();
            }

            public void changedUpdate(DocumentEvent e) {
                updateURL();
            }
        });

        IsFailoverBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateURL();
            }
        });
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });


        stopBUtton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
                //write last configuration to file.
                writeConf();
                dispose();
                System.exit(0);
            }
        });

        setResizable(false);
        setPreferredSize(new Dimension(500, 300));
        // Read previous configuration from configuration file
        readConf();
        pack();
        connectionUrlField.setEditable(false);
        connectionUrlField.setEnabled(false);
        setVisible(false);
        tabbedPane.setEnabledAt(1, false);
        tabbedPane.setEnabledAt(2, false);
        tabbedPane.setEnabledAt(3, false);
        tabbedPane.setEnabledAt(4, false);

        if (autoStart.isSelected()) {
            startButtonActionPerformed("test");
        }


        restart.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {

                String fileParent = monitorDir.getText();
//                if (fileParent != null && fileParent.trim().length() != 0 && file.getText() != null && file.getText().length() != 0) {
//
//                    File file1 = new File(fileParent + File.separator + file.getText());
//                    if (file1.exists()) {
//                        try {
//                            file1.delete();
//                        } catch (Exception ee) {
//                        }
//                        stopButtonActionPerformed(null);
//                        writeConf();
//                        dispose();
//                        System.exit(0);
//                    }
//                }
                if (fileParent != null && fileParent.trim().length() != 0 && file1.getText() != null && file1.getText().length() != 0) {

                    File fileR = new File(fileParent + File.separator + file1.getText());
                    if (fileR.exists()) {
                        try {
                            fileR.delete();
                        } catch (Exception ee) {
                        }
                        stopButtonActionPerformed(null);
                        startButtonActionPerformed(null);
                    }
                }


//                if (restart.isSelected()) {
//                    stopButtonActionPerformed(null);
//
//                    System.exit(-1);
////                    try {
////                        Thread.sleep(2 * 60 * 1000);
////                    } catch (InterruptedException e1) {
////                        //
////                    }
////                    startButtonActionPerformed(null);
//                }
            }
        });

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
//                        Thread.sleep( 60 * 1000);
                        Thread.sleep(7 * 1000);
                    } catch (InterruptedException e) {
                        //
                    }
                    String fileParent = monitorDir.getText();
                    if (fileParent == null || fileParent.trim().length() == 0)
                        continue;
//                    File fileC = new File(fileParent.trim() + File.separator + file.getText().trim());
                    File fileR = new File(fileParent.trim() + File.separator + file1.getText().trim());
//                    if (!fileC.exists() && !fileR.exists())
                    if (!fileR.exists())
                        continue;

                    if (!startButton.isEnabled()) {
//                            if ((Integer.parseInt(restartTime.getText().trim()) *  60 * 1000) <= (new Date().getTime() - lastStart)) {
                        restart.setSelected(false);
                        restart.setSelected(true);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            //
                        }
                    }
                }
            }
        }).start();

    }

    private synchronized void updateURL() {
        StringBuffer url = new StringBuffer();
        if (IsFailoverBox.isSelected()) {
            url.append("failover://");
        }
        url.append("tcp://");
        if (!isEmpty(serverField.getText())) {
            url.append(serverField.getText().trim());
        }
        if (!isEmpty(portField.getText())) {
            url.append(":" + portField.getText().trim());
        }
        connectionUrlField.setText(url.toString());
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        // Creates log file with system time append to its name, The detailed logs will appear in this file.
        File logs = new File("logs_" + System.currentTimeMillis() + ".txt");
        if (!logs.exists()) {
            try {
                logs.createNewFile();
                // redirecting all the print streams to log file
                System.setErr(new PrintStream(new FileOutputStream(logs)));
                System.setOut(new PrintStream(new FileOutputStream(logs)));
            } catch (IOException e) {
                //
            }
        }

        if(logs.exists()){
            File parent = new File(logs.getAbsolutePath()).getParentFile();
            if(parent != null){
                File[] list = parent.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt") && name.startsWith("logs_");
                    }
                });
                long days = 5 * 24 * 60 * 60 * 1000l;
                for(File file : list){
                    if(file.isFile()){
                        try{
                            String name = file.getName().replace("logs_","").replace(".txt","");
                            Long value = Long.parseLong(name.trim());
                            if(value < (System.currentTimeMillis() - days)){
                                file.delete();
                            }
                        }catch (Exception e){}
                    }
                }
            }
        }


        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MainNewUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MainNewUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MainNewUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MainNewUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainNewUI();
            }
        });
    }

    // This method is responsible to disable all the fields
    public void disableAll() {
        serverField.setEnabled(false);
        portField.setEnabled(false);
        IsFailoverBox.setEnabled(false);
        checkbox1.setEnabled(false);
        sendTopicField.setEnabled(false);
        sendFolderPath.setEnabled(false);
        sendFolderButton.setEnabled(false);
        checkbox2.setEnabled(false);
        recvTopicName.setEnabled(false);
        recvFolderPath.setEnabled(false);
        recvButton.setEnabled(false);
        clientID.setEnabled(false);
        autoStart.setEnabled(false);
//        restartTime.setEnabled(false);
        monitorDir.setEnabled(false);
        dirButton.setEnabled(false);
        file.setEnabled(false);
        file1.setEnabled(false);
    }

    // This method is responsible to enable all the fields
    public void enableAll() {
        serverField.setEnabled(true);
        portField.setEnabled(true);
        IsFailoverBox.setEnabled(true);
        checkbox1.setEnabled(true);
        sendTopicField.setEnabled(true);
        sendFolderPath.setEnabled(true);
        sendFolderButton.setEnabled(true);
        checkbox2.setEnabled(true);
        recvTopicName.setEnabled(true);
        recvFolderPath.setEnabled(true);
        recvButton.setEnabled(true);
        clientID.setEnabled(true);
        autoStart.setEnabled(true);
//        restartTime.setEnabled(true);
        monitorDir.setEnabled(true);
        dirButton.setEnabled(true);
        file.setEnabled(true);
        file1.setEnabled(true);
    }


    //START Action
    private void startButtonActionPerformed(Object evt) {
        //Already started
        if (!startButton.isEnabled()) {
            return;
        }
        System.out.println("Starting");
        //disable all the fileds, so that user cannot edit the values while running
        disableAll();
        //disabling start button
        startButton.setEnabled(false);
        //enabling stop button
        stopBUtton.setEnabled(true);
        try {
            //validating data
            validateData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
            startButton.setEnabled(true);
            stopBUtton.setEnabled(false);
            enableAll();
            return;
        }

        //Populating all the filed values to connection object

        connection.setUrl(connectionUrlField.getText());
        connection.setConnectionFactory("ConnectionFactory");
        connection.setExceptionListener(new CustomExceptionListener());

        try {
            if (checkbox1.isSelected()) {
                // Adding topic name property. This is needed for ActiveMQ server to lookup/create the topic
                connection.addProperty("topic." + sendTopicField.getText().trim(), sendTopicField.getText().trim());
            } else {
//                textArea.append("Configuración del remitente no está configurado");
            }
            if (checkbox2.isSelected()) {
                // Adding topic name property. This is needed for ActiveMQ server to lookup/create the topic
                connection.addProperty("topic." + recvTopicName.getText().trim(), recvTopicName.getText().trim());
                //Setting client IT to connection object
                connection.setClientID(clientID.getText().trim());
            } else {
//                textArea.append("Configuración del receptor no está configurado");
            }
            //initializing /starting the connection
            connection.initialize();
            lastStart = new Date().getTime();
        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Unable to create MQ connection. Reason : " + e.getMessage());
            if (evt == null) {
//                try {
//                    Thread.sleep(1*60*1000);
//                } catch (InterruptedException e1) {
//                    //
//                }
//                startButtonActionPerformed(null);
                System.exit(-1);
            }
            JOptionPane.showMessageDialog(this, "Imposible crear la Conexión. Razón : " + e.getLocalizedMessage());
//            textArea.append("Imposible crear la Conexión. Razón : " + e.getLocalizedMessage());
            e.printStackTrace();
            stopButtonActionPerformed(null);
            startButton.setEnabled(true);
            stopBUtton.setEnabled(false);
            enableAll();
            return;
        }

        if (checkbox1.isSelected()) {
            try {
                //Creating client which will send messages to topic
                client = new Client(connection, sendTopicField.getText().trim());
                File f = new File(sendFolderPath.getText().trim());
                if (f.exists() && f.isDirectory())
                    client.start(f, textArea);
            } catch (JMSException e) {
//                JOptionPane.showMessageDialog(this, "Unable to create Publisher on " + sendTopicField.getText().trim() +
//                        ". Reason : " + e.getLocalizedMessage());
                JOptionPane.showMessageDialog(this, "No se puede crear en el Editor " + sendTopicField.getText().trim() +
                        ". Razón : " + e.getLocalizedMessage());
//                textArea.append("No se puede crear en el Editor " + sendTopicField.getText().trim() +
//                        ". Razón : " + e.getLocalizedMessage());
                e.printStackTrace();
                stopButtonActionPerformed(null);
            }

        }

        if (checkbox2.isSelected()) {
            try {
                //Creating listener which will recv messages to topic
                listener = new CustomMessageListener(textArea);
                listener.setFile(new File(recvFolderPath.getText().trim()));
                subscriber = connection.createMessageConsumer(recvTopicName.getText().trim(), listener);
            } catch (Exception e) {
//                JOptionPane.showMessageDialog(this, "Unable to create Consumer on " + recvTopicName.getText().trim() +
//                        ". Reason : " + e.getLocalizedMessage());
//                JOptionPane.showMessageDialog(this, "No se puede crear en el consumidor " + recvTopicName.getText().trim() +
//                        ". Razón : " + e.getLocalizedMessage());
//                textArea.append("No se puede crear en el consumidor " + recvTopicName.getText().trim() +
//                        ". Razón : " + e.getLocalizedMessage());
                e.printStackTrace();
                stopButtonActionPerformed(null);
            }

        }
        System.out.println("Started");
    }


    private void restart() {
        if (client != null) {
            //closing the client
            client.close();
        }
        if (subscriber != null) {
            try {
                //closing the subscriber
                subscriber.close();
            } catch (JMSException e) {
                //
            }
        }
        if (connection != null) {
            //closing the connection
            connection.close();
        }

        connection.setUrl(connectionUrlField.getText());
        connection.setConnectionFactory("ConnectionFactory");
        connection.setExceptionListener(new CustomExceptionListener());

        try {
            if (checkbox1.isSelected()) {
                // Adding topic name property. This is needed for ActiveMQ server to lookup/create the topic
                connection.addProperty("topic." + sendTopicField.getText().trim(), sendTopicField.getText().trim());
            } else {
//                textArea.append("Configuración del remitente no está configurado");
            }
            if (checkbox2.isSelected()) {
                // Adding topic name property. This is needed for ActiveMQ server to lookup/create the topic
                connection.addProperty("topic." + recvTopicName.getText().trim(), recvTopicName.getText().trim());
                //Setting client IT to connection object
                connection.setClientID(clientID.getText().trim());
            } else {
//                textArea.append("Configuración del receptor no está configurado");
            }
            //initializing /starting the connection
            connection.initialize();
            lastStart = new Date().getTime();

        } catch (Exception e) {
//            JOptionPane.showMessageDialog(this, "Unable to create MQ connection. Reason : " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Imposible crear la Conexión. Razón : " + e.getLocalizedMessage());
//            textArea.append("Imposible crear la Conexión. Razón : " + e.getLocalizedMessage());
            e.printStackTrace();
//            stopButtonActionPerformed(null);
//            startButton.setEnabled(true);
//            stopBUtton.setEnabled(false);
//            enableAll();
//            return;
            restart();
        }

        if (checkbox1.isSelected()) {
            try {
                //Creating client which will send messages to topic
                client = new Client(connection, sendTopicField.getText().trim());
                File f = new File(sendFolderPath.getText().trim());
                if (f.exists() && f.isDirectory())
                    client.start(f, textArea);
            } catch (JMSException e) {
//                JOptionPane.showMessageDialog(this, "Unable to create Publisher on " + sendTopicField.getText().trim() +
//                        ". Reason : " + e.getLocalizedMessage());
                JOptionPane.showMessageDialog(this, "No se puede crear en el Editor " + sendTopicField.getText().trim() +
                        ". Razón : " + e.getLocalizedMessage());
//                textArea.append("No se puede crear en el Editor " + sendTopicField.getText().trim() +
//                        ". Razón : " + e.getLocalizedMessage());
                e.printStackTrace();
                stopButtonActionPerformed(null);
            }

        }

        if (checkbox2.isSelected()) {
            try {
                //Creating listener which will recv messages to topic
                listener = new CustomMessageListener(textArea);
                listener.setFile(new File(recvFolderPath.getText().trim()));
                subscriber = connection.createMessageConsumer(recvTopicName.getText().trim(), listener);
            } catch (Exception e) {
//                JOptionPane.showMessageDialog(this, "Unable to create Consumer on " + recvTopicName.getText().trim() +
//                        ". Reason : " + e.getLocalizedMessage());
//                JOptionPane.showMessageDialog(this, "No se puede crear en el consumidor " + recvTopicName.getText().trim() +
//                        ". Razón : " + e.getLocalizedMessage());
//                textArea.append("No se puede crear en el consumidor " + recvTopicName.getText().trim() +
//                        ". Razón : " + e.getLocalizedMessage());
                e.printStackTrace();
                stopButtonActionPerformed(null);
            }

        }


    }

    //STOP Action
    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {
        startButton.setEnabled(true);
        stopBUtton.setEnabled(false);
        System.out.println("Stoping");
        //Enable all fileds in UI
        enableAll();
        if (client != null) {
            //closing the client
            client.close();
        }
        if (subscriber != null) {
            try {
                //closing the subscriber
                subscriber.close();
            } catch (JMSException e) {
                //
            }
        }
        if (connection != null) {
            //closing the connection
            connection.close();
        }
        System.out.println("Stopped");
    }

    public boolean isEmpty(String s) {
        return (s == null || s.trim().length() == 0);
    }

    // This method is resposible to validate teh data
    public void validateData() throws Exception {
        if (isEmpty(serverField.getText())) {
//            throw new Exception("Connection URL is empty. Please fill the field");
            throw new Exception("La dirección URL está vacía. Por favor complete los datos");
        }
        if (isEmpty(connectionUrlField.getText())) {
//            throw new Exception("Connection Factory Name is empty. Please fill the field");
            throw new Exception("El nombre ConnectionFactory está vacío. Por favor complete los datos");
        }
        if (isEmpty(portField.getText())) {
//            throw new Exception("Port is empty. Please fill the field");
            throw new Exception("El Port está vacío. Por favor complete los datos");
        } else {
            try {
                Integer.parseInt(portField.getText().trim());
            } catch (Exception e) {
//            throw new Exception("Port value should be a valid integer");
                throw new Exception("Valor de puerto debe ser un número entero válido");
            }
        }

        if (checkbox1.isSelected()) {
            if (isEmpty(sendTopicField.getText())) {
//                throw new Exception("Topic Name to send messages is empty. Please fill the field");
                throw new Exception("El nombre del Topic está vacío. Por favor complete los datos");
            }
            if (isEmpty(sendFolderPath.getText())) {
//                throw new Exception("Sender Folder Path is empty. Please fill the field");
                throw new Exception("El directorio para Enviar está vacío. Por favor complete los datos");
            }
            File f = new File(sendFolderPath.getText());
            if (!f.exists() || !f.isDirectory()) {
//                throw new Exception("Sender Folder Path doesn't exists. Please create a folder");
                throw new Exception("El directorio para Enviar no existe. Por favor verifique.");
            }
        }

        if (checkbox2.isSelected()) {
            if (isEmpty(clientID.getText())) {
//                throw new Exception("ClientID is empty. Please fill the field");
                throw new Exception("El ID del Cliente no puede estar vacío. Por favor complete los datos");
            }
            if (isEmpty(recvTopicName.getText())) {
//                throw new Exception("Topic Name to receive messages is empty. Please fill the field");
                throw new Exception("El nombre del Topic está vacío. Por favor complete los datos");
            }
            if (isEmpty(recvFolderPath.getText())) {
//                throw new Exception("Receiver Folder Path is empty. Please fill the field");
                throw new Exception("El directorio para Recibir está vacío. Por favor complete los datos");
            }
            File f = new File(recvFolderPath.getText());
            if (!f.exists() || !f.isDirectory()) {
//                throw new Exception("Receiver Folder Path doesn't exists. Please create a folder");
                throw new Exception("El directorio para Recibir no existe. Por favor verifique");
            }
        }
    }

    // This class listens all the exceptions on Active MQ connection
    class CustomExceptionListener implements ExceptionListener {


        public void onException(JMSException e) {
//            System.out.println("Exception on JMS Connection. " + e.getLocalizedMessage());
            System.out.println("Error en la conexión JMS. " + e.getLocalizedMessage());
            e.printStackTrace();

        }
    }

    // This method is responsible to write the last configuration to the configuration file
    private void writeConf() {
//        File configFile = new File(".config");
        File configFile = new File("Configuración.config");
        if (!configFile.exists()) {
            // If configuration file doesn't exists create  new one.
            try {
                if (!configFile.createNewFile()) ;
                System.out.println("No se puede crear el archivo de configuración");
            } catch (IOException e) {
                System.out.println("No se puede crear el archivo de configuración");
                System.out.println(e.getLocalizedMessage());
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                return;
            }
        }

        FileWriter out = null;

        try {
            out = new FileWriter(configFile);

            //Write each property in each line

//            out.write(serverField.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(portField.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(IsFailoverBox.isSelected() ? "true" : "false");
//            out.write(System.getProperty("line.separator"));
//            out.write(connectionUrlField.getText());
//            out.write(System.getProperty("line.separator"));
//
//            out.write(checkbox1.isSelected() ? "true" : "false");
//            out.write(System.getProperty("line.separator"));
//            out.write(sendTopicField.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(sendFolderPath.getText());
//            out.write(System.getProperty("line.separator"));
//
//            out.write(checkbox2.isSelected() ? "true" : "false");
//            out.write(System.getProperty("line.separator"));
//            out.write(recvTopicName.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(recvFolderPath.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(clientID.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(autoStart.isSelected() ? "true" : "false");
//            out.write(System.getProperty("line.separator"));
////            out.write(restartTime.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(monitorDir.getText());
//            out.write(System.getProperty("line.separator"));
//            out.write(file.getText());
//            out.write(System.getProperty("line.separator"));

            out.write("enc");
            out.write(System.getProperty("line.separator"));
            out.write(encode(serverField.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(portField.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(IsFailoverBox.isSelected() ? "true" : "false"));
            out.write(System.getProperty("line.separator"));
            out.write(encode(connectionUrlField.getText()));
            out.write(System.getProperty("line.separator"));

            out.write(encode(checkbox1.isSelected() ? "true" : "false"));
            out.write(System.getProperty("line.separator"));
            out.write(encode(sendTopicField.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(sendFolderPath.getText()));
            out.write(System.getProperty("line.separator"));

            out.write(encode(checkbox2.isSelected() ? "true" : "false"));
            out.write(System.getProperty("line.separator"));
            out.write(encode(recvTopicName.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(recvFolderPath.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(clientID.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(autoStart.isSelected() ? "true" : "false"));
            out.write(System.getProperty("line.separator"));
//            out.write(encode(restartTime.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(monitorDir.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(file.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(file1.getText()));
            out.write(System.getProperty("line.separator"));
            out.write(encode(userName));
            out.write(System.getProperty("line.separator"));
            out.write(encode(password));
            out.write(System.getProperty("line.separator"));

        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To cha"nge body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    //
                }
            }
        }

    }

    // This methos is responsible to read the previous configuration from configuration file
    private void readConf() {
        File configFile = new File("Configuración.config");
//        File configFile = new File(".config");

        // if configuration file doesn't exists do nothing
        if (!configFile.exists()) {
            return;
        }

        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(configFile));

            // read each and every line and set to appropriate field
            String firstLine = in.readLine();
            if (!"enc".equalsIgnoreCase(firstLine)) {
                serverField.setText(firstLine);
                portField.setText(in.readLine());
                IsFailoverBox.setSelected("true".equalsIgnoreCase(in.readLine()) ? true : false);
                connectionUrlField.setText(in.readLine());

                checkbox1.setSelected("true".equalsIgnoreCase(in.readLine()) ? true : false);
                sendTopicField.setText(in.readLine());
                sendFolderPath.setText(in.readLine());

                checkbox2.setSelected("true".equalsIgnoreCase(in.readLine()) ? true : false);
                recvTopicName.setText(in.readLine());
                recvFolderPath.setText(in.readLine());
                clientID.setText(in.readLine());
                autoStart.setSelected("true".equalsIgnoreCase(in.readLine()));
//            restartTime.setText(in.readLine());
                in.readLine();
                monitorDir.setText(in.readLine());
                file.setText(in.readLine());
                file1.setText(in.readLine());
                userName = in.readLine();
                password = in.readLine();
            }else{
                serverField.setText(decode(in.readLine()));
                portField.setText(decode(in.readLine()));
                IsFailoverBox.setSelected("true".equalsIgnoreCase(decode(in.readLine())) ? true : false);
                connectionUrlField.setText(decode(in.readLine()));

                checkbox1.setSelected("true".equalsIgnoreCase(decode(in.readLine())) ? true : false);
                sendTopicField.setText(decode(in.readLine()));
                sendFolderPath.setText(decode(in.readLine()));

                checkbox2.setSelected("true".equalsIgnoreCase(decode(in.readLine())) ? true : false);
                recvTopicName.setText(decode(in.readLine()));
                recvFolderPath.setText(decode(in.readLine()));
                clientID.setText(decode(in.readLine()));
                autoStart.setSelected("true".equalsIgnoreCase(decode(in.readLine())));
//            restartTime.setText(in.readLine());
                in.readLine();
                monitorDir.setText(decode(in.readLine()));
                file.setText(decode(in.readLine()));
                file1.setText(decode(in.readLine()));
                userName = decode(in.readLine());
                password = decode(in.readLine());
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //
                }
            }
        }
//        if(restartTime.getText() == null || restartTime.getText().trim().length() == 0)
//            restartTime.setText("6");
        if (file.getText() == null || file.getText().trim().length() == 0)
            file.setText("close.txt");
        if (file1.getText() == null || file1.getText().trim().length() == 0)
            file1.setText("reset.txt");
        if (userName == null || userName.trim().length() == 0)
            userName = "admin";
        if (password == null || password.trim().length() == 0)
            password = "admin";
    }

    // This is responsible to customize the log statements logged in Logs panel
    class CustomJTextArea extends JTextArea {

        //Date formatter to append the time to log statement
        private SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        //Mark this as synchronized, since client and listener logs into logs panel simultaneously
        public synchronized void append(String str) {
            super.append(formatter.format(new Date()) + "   ");
            super.append(str);
            //append line separator, so that next log will come into new line
            super.append(System.getProperty("line.separator"));
        }

    }


    private static String getProcessId(final String fallback) {
        // Note: may fail in some JVM implementations
        // therefore fallback has to be provided

        // something like '<pid>@<hostname>', at least in SUN / Oracle JVMs
        final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        final int index = jvmName.indexOf('@');

        if (index < 1) {
            // part before '@' empty (index = 0) / '@' not found (index = -1)
            return fallback;
        }

        try {
            return Long.toString(Long.parseLong(jvmName.substring(0, index)));
        } catch (NumberFormatException e) {
            // ignore
        }
        return fallback;
    }

}
