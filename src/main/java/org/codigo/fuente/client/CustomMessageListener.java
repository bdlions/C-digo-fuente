package org.codigo.fuente.client;

import javax.jms.*;
import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: poornachand
 * Date: Feb 9, 2013
 * Time: 5:55:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomMessageListener implements MessageListener {

    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss") ;

    private File file;
    private JTextArea textArea;
    private int noOfMsgs = 1;

    public CustomMessageListener(JTextArea textArea){
        this.textArea = textArea;
    }

    //El mensaje entra aqui
    @Override
    public void onMessage(Message message) {
        System.out.println(formatter.format(new Date()) + " Received Msg ");
        try {
            //Ignora todos los mensajes excepto los de texto
            if (message instanceof TextMessage) {
                //Dejo constancia en el panel de Log
//                textArea.append("Número de mensajes recibidos : " + noOfMsgs++);
                TextMessage txtMsg = (TextMessage) message;
                //Llamo al procedimiento de procesar el mensaje
                processMessage(txtMsg.getText());
            }
        } catch (Exception exp) {
            System.out.println("Imposible obtener el mensaje del archivo.");
            System.out.println(exp.getLocalizedMessage());
            exp.printStackTrace();
        }

    }


    //Procesa el mensaje recibido y lo guarda en un archivo
    public void processMessage(String text) {
        //Graba texto en el archivo en formato .xml
        String fileName = System.nanoTime() + ".xml";
        File nFile = new File(file, fileName);
        FileWriter writer = null;
        try {
            System.out.println(formatter.format(new Date()) + " Writing file : " + fileName);
            writer = new FileWriter(nFile);
            writer.write(text);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(formatter.format(new Date()) + " Completed Writing file : " + fileName);
//        System.out.println("Received : " + text);
    }

    public void setFile(File f){
        file = f;
    }

    public static void main(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        System.out.println(formatter.format(new Date()));
    }

}
