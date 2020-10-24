package ibm.gse.eda.stores.infrastructure.mq;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.TextMessage;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.infrastructure.StoreRepository;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class MQItemGenerator {
    private static Logger logger = Logger.getLogger(MQItemGenerator.class.getName());

    @Inject
    @ConfigProperty(name = "mq.host")
    public String mqHostname;

    @Inject
    @ConfigProperty(name = "mq.port")
    public int mqHostport;

    @Inject
    @ConfigProperty(name = "mq.qmgr", defaultValue = "QM1")
    public String mqQmgr;

    @Inject
    @ConfigProperty(name = "mq.channel", defaultValue = "DEV.APP.SVRCONN")
    public String mqChannel;

    @Inject
    @ConfigProperty(name = "mq.app_user", defaultValue = "app")
    public String mqAppUser;

    @Inject
    @ConfigProperty(name = "mq.app_password", defaultValue = "passw0rd")
    public String mqPassword;

    @Inject
    @ConfigProperty(name = "mq.queue_name", defaultValue = "DEV.QUEUE.1")
    public String mqQueueName;

    @Inject
    @ConfigProperty(name = "app.name", defaultValue = "TestApp")
    public String appName;
    
    @Inject
    public StoreRepository storeRepository;
    
    private Jsonb parser = JsonbBuilder.create();


    protected JMSProducer producer = null;
    private JMSContext jmsContext = null;
    private Destination destination = null;
    private JmsConnectionFactory cf = null;
    protected Jsonb jsonb = null;
    
	public List<Item> start(int numberOfRecords) {
        List<Item> items = storeRepository.buildItems(numberOfRecords);
        try {
            jmsContext = buildJMSConnectionSession();
            producer = jmsContext.createProducer();
    
            Multi.createFrom().items(items.stream()).subscribe().with(item -> {
                sendToMQ(item);
            }, failure -> System.out.println("Failed with " + failure.getMessage()));
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            jmsContext.close();
        }
       
        return items;
	}

    private void sendToMQ(Item item) {
        String msg = parser.toJson(item);
        logger.info("sent to MQ:" + msg);
        TextMessage message = jmsContext.createTextMessage(msg);
        producer.send(destination, message);
    }
    
    private JMSContext buildJMSConnectionSession() throws JMSException {
        JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
        cf = ff.createConnectionFactory();
        cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, this.mqHostname);
        cf.setIntProperty(WMQConstants.WMQ_PORT, this.mqHostport);
        cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, WMQConstants.WMQ_CM_CLIENT);
        cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, this.mqQmgr);
        cf.setStringProperty(WMQConstants.WMQ_APPLICATIONNAME, this.appName);
        cf.setStringProperty(WMQConstants.WMQ_CHANNEL, this.mqChannel);
        cf.setBooleanProperty(WMQConstants.USER_AUTHENTICATION_MQCSP, true);
        cf.setStringProperty(WMQConstants.USERID, this.mqAppUser);
        cf.setStringProperty(WMQConstants.PASSWORD, this.mqPassword);
        // Create JMS objects
        jmsContext = cf.createContext();
        destination = jmsContext.createQueue("queue:///" + this.mqQueueName);
        return jmsContext;	
    }
}
