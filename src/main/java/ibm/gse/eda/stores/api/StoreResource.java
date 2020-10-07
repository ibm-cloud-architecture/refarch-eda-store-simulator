package ibm.gse.eda.stores.api;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import ibm.gse.eda.stores.domain.Store;
import ibm.gse.eda.stores.infrastructure.StoreRepository;
import ibm.gse.eda.stores.infrastructure.kafka.KafkaItemGenerator;
import ibm.gse.eda.stores.infrastructure.rabbitmq.RabbitMQItemGenerator;
import io.smallrye.mutiny.Multi;

@Path("/api/v1/stores")
@ApplicationScoped
public class StoreResource {
   
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

    @POST
    @Path("/start/rmq/{records}")
    public Response startSendingMessage(@PathParam final int records) {
        rabbitMQGenerator.start(records);
        return Response.ok().status(201).build();
    }

    @POST
    @Path("/start/kafka/{records}")
    public Response start(@PathParam final int records){
        kafkaGenerator.sendItems(records);
        return Response.ok().status(201).build();
    }
}