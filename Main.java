package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Main extends JFrame implements ActionListener {
    private JTextField addressField;
    private JTextField portField;
    private JTextField usernameField;
    private JButton connectButton;

    private JTextArea messageArea;
    private JTextField inputField;
    private JButton sendButton;

    private Client clientThread;

    public Main() {
        super("Client");

        // Create the components
        addressField = new JTextField(15);
        portField = new JTextField(5);
        usernameField = new JTextField(10);
        connectButton = new JButton("Connect");
        connectButton.addActionListener(this);
        connectButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, connectButton.getPreferredSize().height));

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        messageScrollPane.setPreferredSize(new Dimension(500, 300));

        inputField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, sendButton.getPreferredSize().height));
        sendButton.setBackground(new Color(152, 251, 152));
        sendButton.setForeground(new Color(255, 255, 255));

        // Add the components to the frame
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(Box.createVerticalGlue());
        panel.add(createFormPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(createConnectButtonPanel());
        panel.add(Box.createVerticalStrut(20));
        panel.add(messageScrollPane);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createInputPanel());
        panel.add(Box.createVerticalGlue());
        getContentPane().add(panel);

        // Configure the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.LINE_AXIS));
        formPanel.add(new JLabel("Address:"));
        formPanel.add(Box.createHorizontalStrut(5));
        formPanel.add(addressField);
        formPanel.add(Box.createHorizontalStrut(20));
        formPanel.add(new JLabel("Port:"));
        formPanel.add(Box.createHorizontalStrut(5));
        formPanel.add(portField);
        formPanel.add(Box.createHorizontalStrut(20));
        formPanel.add(new JLabel("Username:"));
        formPanel.add(Box.createHorizontalStrut(5));
        formPanel.add(usernameField);
        addressField.setText("localhost");
        portField.setText("4444");
        return formPanel;
    }

    private JPanel createConnectButtonPanel() {
        JPanel connectButtonPanel = new JPanel();
        connectButtonPanel.setLayout(new BorderLayout());
        connectButtonPanel.add(connectButton, BorderLayout.CENTER);
        return connectButtonPanel;
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());

        inputField = new JTextField();
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = inputField.getText().trim();
                    if (!message.isEmpty() && clientThread != null) {
                        clientThread.sendMessage(usernameField.getText() + ": " + message);
                        addMessage(usernameField.getText() + ": " + message);
                        inputField.setText("");
                    }
                }
            }
        });

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        sendButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, sendButton.getPreferredSize().height));
        sendButton.setBackground(new Color(166, 226, 46));
        sendButton.setForeground(new Color(68, 68, 68));

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        return inputPanel;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton) {
            connectButton.setEnabled(false);

            String address = addressField.getText();
            int port = Integer.parseInt(portField.getText());

            new Thread(() -> {
                try {
                    clientThread = new Client(this, address, port);
                    clientThread.start();
                    connectButton.setEnabled(true);
                } catch (Exception ex) {
                    connectButton.setEnabled(true);
                    JOptionPane.showMessageDialog(Main.this,
                            "Failed to connect. Please check your connection and try again.",
                            "Connection Error", JOptionPane.ERROR);
                }
            }).start();
        } else if (e.getSource() == sendButton) {
            String message = usernameField.getText().trim() + ": " + inputField.getText().trim();
            if (!message.isEmpty() && clientThread != null) {
                clientThread.sendMessage(message);
                addMessage(message);
                inputField.setText("");
            }
        }
    }

    public void addMessage(String message) {
        messageArea.append(message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        for (var i = 0; i < 3; ++i) {
            new Main();
        }
    }
}
