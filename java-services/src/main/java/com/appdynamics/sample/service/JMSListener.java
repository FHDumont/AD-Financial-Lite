package com.appdynamics.sample.service;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import com.appdynamics.sample.service.JMSListenerCallback;

public class JMSListener implements MessageListener, Runnable {

    private int ackMode = Session.AUTO_ACKNOWLEDGE;
    public String messageQueueName;
    private String messageBrokerUrl = "tcp://activemq-services:61616";

    private Session session;
    private boolean transacted = false;
    private MessageProducer replyProducer;
    private JMSListenerCallback jmsListenerCallback;

    public JMSListener(String queueName, JMSListenerCallback callback) {
        try {
            messageQueueName = queueName;
            jmsListenerCallback = callback;

            BrokerService broker = new BrokerService();
            broker.setPersistent(false);
            broker.setUseJmx(false);
            broker.addConnector(messageBrokerUrl);
            broker.start();
        } catch (Exception e) {
            //Handle the exception appropriately
        }
    }

    public void run() {
        try {
            this.setupMessageQueueConsumer();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    private void setupMessageQueueConsumer() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(messageBrokerUrl);
        Connection connection;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
            this.session = connection.createSession(this.transacted, ackMode);
            Destination adminQueue = this.session.createQueue(messageQueueName);
            MessageConsumer consumer = this.session.createConsumer(adminQueue);
            consumer.setMessageListener(this);
        } catch (JMSException e) {
            //Handle the exception appropriately
        }
    }

    public void onMessage(Message message) {
        try {
            TextMessage response = this.session.createTextMessage();
            String speed = "normal";
            String traffic = "light";

            if (message instanceof TextMessage) {
                TextMessage txtMsg = (TextMessage) message;
                String messageText = txtMsg.getText();
                System.out.println("messageText: " + messageText);

                String[] segments = messageText.split("\\|");

                for (String segment : segments) {
                    if (segment.indexOf("txSpeed") >= 0) {
                        String[] subSegments = segment.split("=");
                        if (subSegments.length > 1) {
                            speed = subSegments[1];
                        }
                    }
                    else if (segment.indexOf("txTraffic") >= 0) {
                        String[] subSegments = segment.split("=");
                        if (subSegments.length > 1) {
                            traffic = subSegments[1];
                        }
                    }
                }
                response.setText("Response: " + messageText);
            }
            jmsListenerCallback.messageReceived(speed, traffic);
        }
        catch (Exception e) {
            //Handle the exception appropriately
        }
    }
}