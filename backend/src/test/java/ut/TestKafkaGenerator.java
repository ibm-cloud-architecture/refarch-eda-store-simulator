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
    
   

}
