package ibm.gse.eda.stores.infrastructure.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.infrastructure.StoreRepository;
import io.smallrye.mutiny.Multi;

/**
 * Simulate item sale activities between multiple store
 */
@ApplicationScoped
public class KafkaItemGenerator {
    Logger logger = Logger.getLogger(KafkaItemGenerator.class.getName());

    String[] stores = null;
  
    @Inject
    StoreRepository storeRepository;

    public KafkaItemGenerator() {
    }

    public List<Item> buildItems(int nbItem) {
        logger.warning("buildItems " + nbItem);
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < nbItem; i++) {
            items.add(Item.buildRandomItem(getStores()));
        }
        return items;
    }

    public List<Item> start(Integer numberOfRecords) {
        List<Item> items = buildItems(numberOfRecords);
        Multi.createFrom().items(items.stream()).subscribe().with(item -> {
            logger.warning("send " + item.toString());
           // Message<Item> record = KafkaRecord.of(item.storeName,item);
           // emitter.send(record );
        }, failure -> System.out.println("Failed with " + failure.getMessage()));
        return items;
    }


    private String[] getStores(){
        if (stores == null) {
            stores = storeRepository.getStoreNames();
        }
        return stores;
    }
}
