package org.codigo.fuente.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.TransportConnector;

/**
 *
 * @author Nazmul Hasan
 */
public class MQServer {
    private final String _brokerName = "messageQBroker";
    private final String _dataDirectoryName = "data";
    private BrokerService broker = null;
    private static MQServer _queingServer;

    private MQServer() {
       
    }
    
    public static synchronized MQServer getInstance() {
        if (_queingServer == null) {
            _queingServer = new MQServer();
        }
        return _queingServer;
    }
    
    public synchronized void start(){
        try {
            broker = new BrokerService();

            TransportConnector connector = new TransportConnector();
            connector.setUri(new URI("tcp://0.0.0.0:61616?useJmx=true"));
            broker.addConnector(connector);

            broker.setDataDirectory(_dataDirectoryName);
            broker.setBrokerName(_brokerName);
            broker.start();            

        } 
        catch (URISyntaxException | IOException ex) {
            System.out.println(ex.toString());
        } 
        catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    public synchronized void stop(){
        try {
            if (broker != null) {
                broker.stop();
            }
        } catch (Exception ex) {
            
        }
    }
    
}
