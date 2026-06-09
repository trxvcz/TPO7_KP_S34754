package org.example;


import com.rabbitmq.client.*;
import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

public class ChatBackend {
    private Connection connection;
    private Channel channel;
    private String nickname;
    private String roomName;

    private static final String CHAT_EXCHANGE = "chat.exchange";
    private static final String PRESENCE_EXCHANGE = "presence.topic.exchange";

    public void Connect(String nickname, String roomName, Consumer<String> onMessageReceived, Consumer<String> onPresenceReceived) throws IOException, TimeoutException {
        this.nickname = nickname;
        this.roomName = roomName;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(CHAT_EXCHANGE, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(PRESENCE_EXCHANGE, BuiltinExchangeType.TOPIC);

        String queue = channel.queueDeclare().getQueue();

        channel.queueBind(queue, CHAT_EXCHANGE, "room." + roomName);
        channel.queueBind(queue, CHAT_EXCHANGE, "private." + nickname);

        channel.queueBind(queue,PRESENCE_EXCHANGE , "presence." + roomName);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            String exchange = delivery.getEnvelope().getExchange();

            SwingUtilities.invokeLater(() -> {
                if (PRESENCE_EXCHANGE.equals(exchange)) {
                    onPresenceReceived.accept(message);
                } else {
                    onMessageReceived.accept(message);
                }
            });

        };

        channel.basicConsume(queue, true, deliverCallback, consumerTag -> {});
        sendPresence("JOIN:" + nickname);
    }

    public void sendMessage(String message) throws IOException {
        if (message == null || message.trim().isEmpty()) return;

        if (message.startsWith("/msg")){
            String[] parts = message.split(" ",3);
            if (parts.length >= 3){
                String target = parts[1];
                String content = "(prywatnie) " + nickname + " : " + parts[2];
                channel.basicPublish(CHAT_EXCHANGE, "private." + target, null, content.getBytes(StandardCharsets.UTF_8));
            }
        }else{
            String content = nickname + " : " + message;
            channel.basicPublish(CHAT_EXCHANGE, "room." + roomName, null, content.getBytes(StandardCharsets.UTF_8));
        }

    }

    public void sendPresence(String status) throws IOException {
        if (channel != null && channel.isOpen()) {
            channel.basicPublish(PRESENCE_EXCHANGE, "presence." + roomName, null, status.getBytes(StandardCharsets.UTF_8));
        }
    }


    public void disconnect() {
        try {
            if (channel != null && channel.isOpen()) {
                sendPresence("LEAVE:" + nickname);
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        } catch (IOException | TimeoutException e) {
            System.err.println("Disconnect failed");
        }
    }



}
