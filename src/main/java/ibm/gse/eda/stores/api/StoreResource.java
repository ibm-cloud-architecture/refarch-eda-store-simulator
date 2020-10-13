package ibm.gse.eda.stores.api;

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

import ibm.gse.eda.stores.api.dto.SimulationControl;
import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.domain.Store;
import ibm.gse.eda.stores.infrastructure.StoreRepository;
import ibm.gse.eda.stores.infrastructure.kafka.KafkaItemGenerator;
import ibm.gse.eda.stores.infrastructure.mq.MQItemGenerator;
import ibm.gse.eda.stores.infrastructure.rabbitmq.RabbitMQItemGenerator;
import io.smallrye.mutiny.Multi;

@Path("/api/v1/stores")
@ApplicationScoped
public class StoreResource {
    public static String RABBITMQ = "RabbitMQ";
    public static String IBMMQ = "IBMMQ";
    public static String KAFKA = "Kafka";
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
        if (RABBITMQ.equals(control.backend)) {
            return startSendingMessageToRMQ(control.records);
        } else if (KAFKA.equals(control.backend)) {
            return startSendingMessageToKafka(control.records);
         } else if (IBMMQ.equals(control.backend)) {
            return startSendingMessageToMQ(control.records);
         }
         return Multi.createFrom().empty();
        }
        

    @POST
    @Path("/start/rmq/{records}")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Item> startSendingMessageToRMQ(@PathParam final int records) {
            return Multi.createFrom().items(rabbitMQGenerator.start(records).stream()); 
             
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start/kafka/{records}")
    public  Multi<Item>  startSendingMessageToKafka(@PathParam final int records){
        return Multi.createFrom().items(kafkaGenerator.start(records).stream());
       
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/start/mq/{records}")
    public  Multi<Item>  startSendingMessageToMQ(@PathParam final int records){
        return Multi.createFrom().items(mqGenerator.start(records).stream());
       
    }
}