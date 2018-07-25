package org.codigo.fuente.client;

import javax.jms.*;
import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: poornachand
 * Date: Feb 8, 2013
 * Time: 5:11:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class Client {

    private Session session;
    private MessageProducer producer;
    private MQConnection connection;
    private String topicName;
    private String qName;

    public Client(MQConnection mqConnection, String topicName, String qName) throws JMSException {
        this.connection = mqConnection;
        this.topicName = topicName;
        this.qName = qName;
        initialize();
    }

    //Creamos la sesión
    public void initialize() throws JMSException {
        session = connection.getSession();
        if(qName != null && !qName.isEmpty()){
            producer = connection.createQProducer(qName);
        }else if(topicName != null && !topicName.isEmpty()){
            producer = connection.createTopicProducer(topicName);
        }
    }

    public void sendMessage(String text) throws JMSException {

        TextMessage message = null;
        try {
            // Crea y setea el mensaje de texto a enviar
            message = session.createTextMessage(text);
            // Tell the producer to send the message
            producer.send(message);
        } catch (JMSException e) {
            System.out.println("Imposible enviar el mensaje: " + text);
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw e;
        }
    }

    private boolean stopped = true;

    public void close(){

        if(th != null && th.isAlive()){
            th.stop();
            while (th.isAlive());
        }
        if(producer != null)
            try {
                //Stops producer
                producer.close();
            } catch (JMSException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }finally {
                producer = null;
            }
    }

    Thread th = null;

    public void start(File file, JTextArea textArea){
        //Chequea si el archivo existe o si es un directorio valido
        if(file == null || !file.exists() || !file.isDirectory())
            return;
        //Comienzo a monitorear el directorio
        th  = new Thread(new CustomRunnable(file, textArea));
        System.out.println(formatter.format(new Date()) + " Starting ");
        th.start();
    }


    public void setStop(boolean stop) {
        this.stop = stop;
    }

    private boolean stop = false;
    private static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    //This class is responsible to monitor the directory and reads teh files form that
    class CustomRunnable implements Runnable{

        private int noOfMsgs = 1;
        private File file;
        private boolean isRunning = false;
        private JTextArea textArea;

        public CustomRunnable(File f, JTextArea textArea){
            file = f;
            this.textArea = textArea;
        }

        public void run() {
            if(file == null || !file.exists() || !file.isDirectory()){
                return;
            }

            while(!stop){

                //List all teh files but not teh directories
                File[] files = file.listFiles(new FileFilter(){

                    public boolean accept(File pathname) {
                        return !pathname.isDirectory();  
                    }
                });

                if(files != null && files.length != 0){
                    //iterate each and evert file
                    for(final File t : files){
                        //If STOP command comes from UI stop this tread
                        if(stop)
                            break;

                        boolean sent = false;
                        // Reading teh file
                        BufferedReader reader = null;
                        try {
                            System.out.println(formatter.format(new Date())+ " Reading file : " + t.getName());
                            reader = new BufferedReader(new FileReader(t));
                            StringBuffer buf = new StringBuffer();
                            String str = null;
                            while((str = reader.readLine()) != null){
                                buf.append(str);
                            }

                            try {
                                //Sending file content to topic
                                System.out.println(formatter.format(new Date())+ " Sending");
                                sendMessage(buf.toString());
                                sent = true;
//                                textArea.append("Número de mensajes enviados : " + noOfMsgs++);
                            } catch (JMSException e) {
//                                System.out.println("Unable to send Message in the file : " + t.getName());
                                System.out.println("Imposible enviar el mensaje del archivo : " + t.getName());
//                                textArea.append("Imposible enviar el mensaje del archivo : " + t.getName());
                                System.out.println(e.getLocalizedMessage());
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                sent = false;
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            sent = false;
                        } catch (IOException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            sent = false;
                        } finally {
                            if(reader != null){
                                try {
                                    reader.close();
                                } catch (IOException e) {
                                    //
                                }
                            }
                            if(sent){
                                //IF file content is sent, delete the file
                                t.delete();
                            }
                        }
                        System.out.println(formatter.format(new Date())+ " Processed file : " + t.getName());
                    }
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    //
                }
            }
            stopped = true; 
        }
    }

    private  void log(String msg, Throwable e){
        System.out.println(msg);
        if(e != null)
            e.printStackTrace();
    }
}
