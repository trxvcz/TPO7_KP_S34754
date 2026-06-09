package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class ChatGui extends JFrame {
    private JPanel contentPane;
    private CardLayout cardLayout;

    private JTextField nicknameField;
    private JTextField roomField;
    private JButton joinButton;

    private JTextArea chatArea;
    private JList<String> userList;
    private DefaultListModel<String> listModel;
    private JTextField inputField;
    private JButton sendButton;

    private ChatBackend backend;

    public ChatGui() {
        this.backend = new ChatBackend();

        setTitle("Chat");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600,600);
        setLocationRelativeTo(null);
        cardLayout = new CardLayout();
        contentPane = new JPanel();

        contentPane.setLayout(cardLayout);

        buildLoginPanel();
        buildChatPanel();

        add(contentPane);
        setupListeners();
    }

    private void setupListeners() {
        joinButton.addActionListener(e -> {
            String nickname = nicknameField.getText().trim();
            String room = roomField.getText().trim();

            if (!nickname.isEmpty() && !room.isEmpty()) {
                showChatPanel();

                Consumer<String> messageConsumer = message -> {
                    chatArea.append(message + "\n");
                };

                Consumer<String> presenceConsumer = presence -> {
                    if (presence.startsWith("JOIN:")) {
                        String nick = presence.substring(presence.indexOf(":") + 1).trim();
                        if (!listModel.contains(nick)) {
                            listModel.addElement(nick);
                            try {
                                backend.sendPresence("HERE:"+nickname);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                    } else if (presence.startsWith("LEAVE:")) {
                        String nick = presence.substring(presence.indexOf(":") + 1).trim();
                        listModel.removeElement(nick);
                    }else if (presence.startsWith("HERE:")){
                        String nick = presence.substring(presence.indexOf(":") + 1).trim();
                        if (!listModel.contains(nick) && !nick.equals(nickname)) {
                            listModel.addElement(nick);
                        }
                    }
                };
                try {
                    backend.Connect(nickname, room, messageConsumer, presenceConsumer);
                } catch (IOException | TimeoutException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                JOptionPane.showMessageDialog(contentPane, "Please enter a nickname and a room name.");
            }
        });

        ActionListener  sendAction = e -> {
            String text = inputField.getText().trim();

            if (!text.isEmpty()) {
                try {
                    backend.sendMessage(text);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                inputField.setText("");
            }
        };

        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);


        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (backend != null) {
                    backend.disconnect();
                }
                System.exit(0);
            }
        });
    }


    private void buildLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        JPanel formPanel = new JPanel(new GridLayout(3,2,10,10));

        formPanel.add(new JLabel("Nickname:"));
        nicknameField = new JTextField(15);
        formPanel.add(nicknameField);

        formPanel.add(new JLabel("Room:"));
        roomField = new JTextField(15);
        formPanel.add(roomField);

        formPanel.add(new JLabel(""));
        joinButton = new JButton("Dołącz");
        formPanel.add(joinButton);

        loginPanel.add(formPanel);

        contentPane.add(loginPanel, "LOGIN");

    }

    private void buildChatPanel() {
        JPanel chatPanel = new JPanel(new BorderLayout(5,5));
        chatPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatPanel.add(chatScroll, BorderLayout.CENTER);

        listModel = new DefaultListModel<>();
        userList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(userList);
        listScrollPane.setPreferredSize(new Dimension(150,0));


        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(new JLabel("Obecni w pokoju:"),BorderLayout.NORTH);
        rightPanel.add(listScrollPane, BorderLayout.CENTER);
        chatPanel.add(rightPanel, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
        inputField = new JTextField();
        sendButton = new JButton("Wyślij");

        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(bottomPanel, BorderLayout.SOUTH);

        contentPane.add(chatPanel,"CHAT");


    }

    public void showChatPanel() {
        cardLayout.show(contentPane, "CHAT");
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(()->{
            new ChatGui().setVisible(true);
        });
    }
}
