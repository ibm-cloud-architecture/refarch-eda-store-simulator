package ut;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ibm.gse.eda.stores.infra.kafka.KafkaItemGenerator;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TestKafkaGenerator {

    @Inject
    KafkaItemGenerator generator;
    
    @Test
    public void shouldReadBootstrapServersConfiguration(){
        System.setProperty("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        generator.getProducer();
        Assertions.assertNotNull(generator.getBootstrapServers());  
       
    }

    @Test
    public void shouldGetSSLConfiguration(){
        // get instance and not proxy
        KafkaItemGenerator generator = new KafkaItemGenerator();
        generator.kafkaSecurityProtocol = Optional.of("SSL");
        generator.bootstrapServers = "localhost:9092";
        generator.keyStoreLocation = Optional.of("/path/to/user/cert");
        generator.keyStorePwd = Optional.of("user_cert_pwd");
        generator.trustStoreLocation = Optional.of("/path/to/ca/cert");
        generator.trustStorePwd = Optional.of("ca_cert_pwd");
        Assertions.assertEquals("SSL",generator.getProducerProperties().get(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG)); 
        Assertions.assertEquals("/path/to/user/cert",generator.getProducerProperties().get(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));  
    }

}
