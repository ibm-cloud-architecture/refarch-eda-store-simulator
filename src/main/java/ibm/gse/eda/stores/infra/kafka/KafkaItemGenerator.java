/*
* (c) Copyright IBM Corporation 2020
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package ibm.gse.eda.stores.infra.kafka;

import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.infra.StoreRepository;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.smallrye.mutiny.Multi;

/**
 * Simulate item sale activities between multiple store. As we do not want to
 * get polluted by kafka connection exceptions while not running in Kafka mode
 * we implement this using the KafkaProducer API and not the microprofile
 * reactive messaging API
 */
@ApplicationScoped
public class KafkaItemGenerator {
    Logger logger = Logger.getLogger(KafkaItemGenerator.class.getName());

    @ConfigProperty(name = "kafka.topic.name", defaultValue = "items")
    public Optional<String> topicName;

    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "localhost:9092")
    public String bootstrapServers;

    @ConfigProperty(name = "kafka.producer.timeout.sec", defaultValue = "5")
    public Optional<Long> producerTimeOut;

    @ConfigProperty(name = "kafka.producer.acks", defaultValue = "1")
    public Optional<String> producerAcks;

    @ConfigProperty(name = "kafka.producer.idempotence", defaultValue = "false")
    public boolean producerIdempotence;

    @ConfigProperty(name = "kafka.security.protocol", defaultValue = "PLAINTEXT")
    public Optional<String> kafkaSecurityProtocol;

    @ConfigProperty(name = "kafka.sasl.mechanism", defaultValue = "PLAIN")
    public Optional<String> kafkaSaslMechanism;

    @ConfigProperty(name = "kafka.sasl.jaas.config", defaultValue = "")
    public Optional<String> saslJaasConfig;

    @ConfigProperty(name = "kafka.ssl.trutstore.file.location", defaultValue = "/deployments/certs/server/ca.p12")
    public Optional<String> trustStoreLocation;

    @ConfigProperty(name = "kafka.ssl.trutstore.password", defaultValue = "")
    public Optional<String> trustStorePwd;

    @ConfigProperty(name = "kafka.ssl.keystore.file.location", defaultValue = "/deployments/certs/user/cuser.p12")
    public Optional<String> keyStoreLocation;

    @ConfigProperty(name = "kafka.ssl.keystore.password", defaultValue = "")
    public Optional<String> keyStorePwd;

    @ConfigProperty(name = "kafka.ssl.protocol", defaultValue = "TLSv1.2")
    public Optional<String> sslProtocol;

    private KafkaProducer<String, Item> kafkaProducer = null;

    @Inject
    public StoreRepository storeRepository;
    
    public KafkaItemGenerator() {
    }

    public KafkaProducer<String, Item> getProducer() {
        if (kafkaProducer == null) {
            kafkaProducer = new KafkaProducer<String, Item>(getProducerProperties());
        }
        return kafkaProducer;
    }

    public void close(){
        kafkaProducer.close();
        kafkaProducer = null;
    }

   

    public List<Item> start(Integer numberOfRecords) {
        List<Item> items = storeRepository.buildItems(numberOfRecords);
        Multi.createFrom().items(items.stream()).subscribe().with(item -> {
            sendToKafka(item);
        }, failure -> System.out.println("Failed with " + failure.getMessage()), () -> close());
        return items;
    }

    private void sendToKafka(Item item) {
        ProducerRecord<String, Item> record = new ProducerRecord<>(topicName.get(), item.storeName, item);
        logger.info("sending to "+ topicName.get() + " item " + item.toString());
        try {
            getProducer().send(record, new Callback() {

            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                if (exception != null) {
                    exception.printStackTrace();
                } else {
                    logger.info("The offset of the record just sent is: " + metadata.offset());

                }
            }
            
        });
        } catch (Exception e) {
            e.printStackTrace();
        }
        //logger.info("Partition:" + resp.partition());
    }

  

    public Properties getProducerProperties() {
        Properties properties = new Properties();
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
       
        if (kafkaSecurityProtocol.get().contains("SASL")) {
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig.get());
            properties.put(SaslConfigs.SASL_MECHANISM, kafkaSaslMechanism.get());
        } 
        properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, kafkaSecurityProtocol.get());
          
        if (kafkaSecurityProtocol.get().equals("SSL")) {
            properties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, keyStoreLocation.get());
            properties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, keyStorePwd.get());
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation.get());
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePwd.get());
        }
         
        if (kafkaSecurityProtocol.get().equals("SASL_SSL")) {
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation.get());
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, trustStorePwd.get());
        }
        if (sslProtocol != null && ! sslProtocol.isEmpty()) {
            properties.put(SslConfigs.SSL_PROTOCOL_CONFIG, sslProtocol.get());
        }

        if (producerAcks != null && ! producerAcks.isEmpty()) {
            properties.put(ProducerConfig.ACKS_CONFIG, producerAcks.get());
        }


        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());
        properties.put(ProducerConfig.CLIENT_ID_CONFIG, "StoreProducer-1");
        
        properties.put(ProducerConfig.RETRIES_CONFIG, 0);
       
        properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerIdempotence);
        properties.forEach((k, v) -> logger.info(k + " : " + v));
        return properties;
    }

    public Optional<String> getTopicName() {
        return topicName;
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

}
