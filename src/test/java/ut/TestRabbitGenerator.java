package ut;

import com.google.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.infrastructure.rabbitmq.RabbitMQItemGenerator;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestRabbitGenerator {
    
    @Inject
    RabbitMQItemGenerator generator;

    @Test
    public void shouldHaveLoadedProperties(){
        generator.buildItems(1);
        Assertions.assertNotNull(generator.hostname);   
        Assertions.assertNotNull(generator.port);   
        Assertions.assertNotNull(generator.queueName); 
        Assertions.assertNotNull(generator.username);
              
    }
}
