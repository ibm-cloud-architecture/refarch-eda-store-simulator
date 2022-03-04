package ibm.gse.eda.stores.infra.api;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.domain.Store;
import ibm.gse.eda.stores.infra.StoreRepository;
import ibm.gse.eda.stores.infra.api.dto.SimulationControl;
import ibm.gse.eda.stores.infra.kafka.KafkaItemGenerator;
import ibm.gse.eda.stores.infra.mq.MQItemGenerator;
import ibm.gse.eda.stores.infra.rabbitmq.RabbitMQItemGenerator;
import io.smallrye.mutiny.Multi;

@Path("/api/stores/v1")
@ApplicationScoped
public class StoreResource {
    private static Logger logger = Logger.getLogger(StoreResource.class.getName());
    public static String RABBITMQ = "RABBITMQ";
    public static String IBMMQ = "IBMMQ";
    public static String KAFKA = "KAFKA";
    @Inject
    @ConfigProperty(name="app.target.messaging")
    String[] targetMessaging;

    @Inject
    RabbitMQItemGenerator rabbitMQGenerator;

    @Inject
    StoreRepository storeRepository;
    
    @Inject
    KafkaItemGenerator kafkaGenerator;

    @Inject
    MQItemGenerator mqGenerator;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Store> getStoresList() {
        return storeRepository.getStores();
    }

    @GET
    @Path("/backends")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getBackends() {
        return targetMessaging;
    }

    @GET
    @Path("/names")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> getStoreNamesList() {
        return Multi.createFrom().items(storeRepository.getStoreNameList().stream());
    }


    @POST
    @Path("/start/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Item> startSendingMessage( SimulationControl control) {
        control.backend = control.backend.toUpperCase();
        logger.info("Received in /start " + control.toString());
        if (RABBITMQ.equals(control.backend)) {
            return startSendingMessageToRMQ(control.records,true);
        } else if (KAFKA.equals(control.backend)) {
            return startSendingMessageToKafka(control.records,true);
         } else if (IBMMQ.equals(control.backend)) {
            return startSendingMessageToMQ(control.records,true);
         }
         return Multi.createFrom().empty();
    }
        
    @POST
    @Path("/startControlled/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Item> startSendingControlledMessage( SimulationControl control) {
        control.backend = control.backend.toUpperCase();
        logger.info("Received in /start controlled " + control.toString());
        if (RABBITMQ.equals(control.backend)) {
            return startSendingMessageToRMQ(control.records,false);
        } else if (KAFKA.equals(control.backend)) {
            return startSendingMessageToKafka(control.records,false);
         } else if (IBMMQ.equals(control.backend)) {
            return startSendingMessageToMQ(control.records,false);
         }
         return Multi.createFrom().empty();
    }

    @POST
    @Path("/start/rmq/{records}")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Item> startSendingMessageToRMQOperation(@PathParam final int records){
        return startSendingMessageToRMQ(records,true);
    }

    public Multi<Item> startSendingMessageToRMQ(@PathParam final int records,boolean randomIt) {
            return Multi.createFrom().items(rabbitMQGenerator.start(records,randomIt).stream()); 
             
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start/kafka/{records}")
    public Multi<Item> startSendingMessageToKafkaOperation(@PathParam final int records){
        return startSendingMessageToKafka(records,true);
    }

    public  Multi<Item>  startSendingMessageToKafka(@PathParam final int records,boolean randomIt){
        return Multi.createFrom().items(kafkaGenerator.start(records,randomIt).stream());
       
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start/mq/{records}")
    public  Multi<Item>  startSendingMessageToMQ(@PathParam final int records,boolean randomIt){
        return Multi.createFrom().items(mqGenerator.start(records,randomIt).stream());
       
    }
}