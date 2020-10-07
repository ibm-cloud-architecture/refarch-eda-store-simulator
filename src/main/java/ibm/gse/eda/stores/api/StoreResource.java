package ibm.gse.eda.stores.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.domain.Store;
import ibm.gse.eda.stores.infrastructure.StoreRepository;
import ibm.gse.eda.stores.infrastructure.kafka.KafkaItemGenerator;
import ibm.gse.eda.stores.infrastructure.rabbitmq.RabbitMQItemGenerator;
import io.smallrye.mutiny.Multi;

@Path("/api/v1/stores")
@ApplicationScoped
public class StoreResource {
    public static String RABBITMQ = "rabbitmq";
    public static String IBMTMQ = "ibmmq";
    public static String KAFKA = "kafka";
    @Inject
    @ConfigProperty(name="app.target.messaging")
    String targetMessaging;

    @Inject
    RabbitMQItemGenerator rabbitMQGenerator;

    @Inject
    StoreRepository storeRepository;
    
    @Inject
    KafkaItemGenerator kafkaGenerator;

    @GET
    @Produces("application/json")
    public Multi<Store> getStoresList() {
        return storeRepository.getStores();
    }

    @GET
    @Path("/backend")
    @Produces(MediaType.TEXT_PLAIN)
    public String getBackend() {
        return targetMessaging;
    }

    @GET
    @Path("/names")
    @Produces("application/json")
    public Multi<String> getStoreNamesList() {
        return Multi.createFrom().items(storeRepository.getStoreNameList().stream());
    }


    @POST
    @Path("/start/{records}")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Item> startSendingMessage(@PathParam final int records) {
        if (RABBITMQ.equals(targetMessaging)) {
            return startSendingMessageToRMQ(records);
        } else if (KAFKA.equals(targetMessaging)) {
            return startSendingMessageToKafka(records);
         }
         return Multi.createFrom().empty();
        }
        

    @POST
    @Path("/start/rmq/{records}")
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Item> startSendingMessageToRMQ(@PathParam final int records) {
        if (RABBITMQ.equals(targetMessaging)) {
            return Multi.createFrom().items(rabbitMQGenerator.start(records).stream()); 
        } else {
            return Multi.createFrom().empty();
        }
        
    }

    @POST
    @Path("/start/kafka/{records}")
    public  Multi<Item>  startSendingMessageToKafka(@PathParam final int records){
        return Multi.createFrom().items(kafkaGenerator.start(records).stream());
       
    }
}