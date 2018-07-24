/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.codigo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

/**
 *
 * @author alamgir
 */
public class ActiveMQServerTest implements Runnable, ExceptionListener {

    private static final String _brokerName = "messageQBroker";
    private static final String _dataDirectoryName = "data";
    public static boolean running = false;

    public void run() {
        try {

            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61617");

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

            connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("TEST.FOO");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            while (running) {
                // Wait for a message
                Message message = consumer.receive();

                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    String text = textMessage.getText();
                    System.out.println("Received: " + text);
                } else {
                    System.out.println("Received: " + message);
                }
            }

            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }

    public static void main(String[] args) {
        try {
            running = true;
            BrokerService broker = new BrokerService();

            TransportConnector connector = new TransportConnector();
            connector.setUri(new URI("tcp://0.0.0.0:61617?useJmx=true"));
            broker.addConnector(connector);

            broker.setDataDirectory(_dataDirectoryName);
            broker.setBrokerName(_brokerName);
            broker.start();

            ActiveMQServerTest act = new ActiveMQServerTest();
            new Thread(act).start();
        } catch (URISyntaxException ex) {
            Logger.getLogger(ActiveMQServerTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(ActiveMQServerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
