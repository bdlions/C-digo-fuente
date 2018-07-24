package org.codigo.fuente.client;

import java.net.URI;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.HashMap;
import java.util.Properties;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.apache.activemq.broker.region.QueueSubscription;

/**
 * Created by IntelliJ IDEA. User: poornachand Date: Feb 8, 2013 Time: 5:13:17
 * PM To change this template use File | Settings | File Templates.
 */
public class MQConnection{

    private String url;
    private String username;
    private String password;
    private String connectionFactoryName;
    private String clientID;
    private ExceptionListener expExceptionListener;
    private static final String _brokerName = "messageQBroker";
    private static final String _dataDirectoryName = "data";

    public ExceptionListener getExceptionListener() {
        return expExceptionListener;
    }

    public void setExceptionListener(ExceptionListener expExceptionListener) {
        this.expExceptionListener = expExceptionListener;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getConnectionFactory() {
        return connectionFactoryName;
    }

    public void setConnectionFactory(String connectionFactory) {
        this.connectionFactoryName = connectionFactory;
    }

    public Session getSession() {
        return session;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private Connection connection;
    private Session session;
//    private Context ctx;

    private HashMap properties = new HashMap();

    public void addProperty(String key, String value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }

    }

    // Método para crear la conexión
    public void initialize() throws Exception {
        try {

//            Properties properties = new Properties();
//            //Esta propiedad especifica la conexión con la clase
//            properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
//
//            //Agrego las propiedades
//            properties.put(Context.PROVIDER_URL, url);
//            properties.putAll(this.properties);
//
//            //Creo el contexto inicial
//            ctx = new InitialContext(properties);
            //Creo el ConnectionFactory
//            ActiveMQConnectionFactory connectionFactory = (ActiveMQConnectionFactory) ctx.lookup(connectionFactoryName);
            BrokerService broker = new BrokerService();

            TransportConnector connector = new TransportConnector();
//            connector.setUri(new URI("tcp://0.0.0.0:61617?useJmx=true"));
            connector.setUri(new URI(url));
            
            broker.addConnector(connector);

            broker.setDataDirectory(_dataDirectoryName);
            broker.setBrokerName(_brokerName);
            broker.start();
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

            //Creo la Conexión
            if (username != null) {
                connection = connectionFactory.createConnection(username, password);
            } else {
                connection = connectionFactory.createConnection();
            }

            //Creo el ClientID
            if(clientID != null)
                connection.setClientID(clientID);
            //Seteo una función para las excepciones
            connection.setExceptionListener(expExceptionListener);

            //Comienzo la conexión
            connection.start();

            //Creo la sesión
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        } catch (Exception e) {
            System.out.println("Imposible crear la conexión. ");
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }

    }

    //Método para crear el mensaje de un topic
    public TopicSubscriber createTopicMessageConsumer(String topicName, MessageListener listener) throws Exception {
        try {
            //Asocio el topic
            Topic destination  = session.createTopic(topicName);
//            Topic destination = (Topic) ctx.lookup(topicName);

            //Subscribo al topic especificado utilizando el Client Id ingresado
            TopicSubscriber subscriber = session.createDurableSubscriber(destination, clientID);

            //Asigno la función para escuchar los mensajes, así todos los mensajes que llegan son procesados
            subscriber.setMessageListener(listener);

            return subscriber;
        } catch (JMSException e) {
            System.out.println("Imposible crear el consumidor.");
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //Método para crear el mensaje de un topic
    public MessageConsumer createQMessageConsumer( String queueName, MessageListener listener) throws Exception {
        try {
            //Asocio el topic
            
            Queue destinationQ  = session.createQueue(queueName);
//            Topic destination = (Topic) ctx.lookup(topicName);

            //Subscribo al topic especificado utilizando el Client Id ingresado
            

            //Asigno la función para escuchar los mensajes, así todos los mensajes que llegan son procesados
            MessageConsumer consumer = session.createConsumer(destinationQ);
            consumer.setMessageListener(listener);

            return consumer;
        } catch (JMSException e) {
            System.out.println("Imposible crear el consumidor.");
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();
            throw e;
        }
    }

    //Método para crear el responsable de crear el mensaje de un topic
    public MessageProducer createTopicProducer(String topicName) throws JMSException {

        try {
            //Asocio el topic
            Topic destination = session.createTopic(topicName);
            

            //Creo un MessageProducer de la sesión del topic
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            return producer;
        } catch (JMSException e) {
            System.out.println("Imposible crear el remitente.");
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
    }
    //Método para crear el responsable de crear el mensaje de un topic
    public MessageProducer createQProducer(String queueName) throws JMSException {

        try {
            //Asocio el topic
            Queue destinationQ = session.createQueue(queueName);
            

            //Creo un MessageProducer de la sesión del topic
            MessageProducer producerQ = session.createProducer(destinationQ);
            producerQ.setDeliveryMode(DeliveryMode.PERSISTENT);

            return producerQ;
        } catch (JMSException e) {
            System.out.println("Imposible crear el remitente.");
            System.out.println(e.getLocalizedMessage());
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
    }

    public void close() {
        try {
            if (session != null) //cierro la sesión
            {
                session.close();
            }
        } catch (JMSException e) {
            //
        }
        try {
            if (connection != null) //cierro la conexión
            {
                connection.close();
            }
        } catch (JMSException e) {
            //
        }
    }

}
