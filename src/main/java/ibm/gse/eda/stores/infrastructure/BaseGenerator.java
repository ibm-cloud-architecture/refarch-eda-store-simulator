package ibm.gse.eda.stores.infrastructure;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import ibm.gse.eda.stores.domain.Item;

public class BaseGenerator {
    private String[] stores = null;
  
    @Inject
    public StoreRepository storeRepository;
    
    public List<Item> buildItems(int nbItem) {
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < nbItem; i++) {
            items.add(Item.buildRandomItem(i,getStores()));
        }
        return items;
    }

    private String[] getStores() {
        if (stores == null) {
            stores = storeRepository.getStoreNames();
        }
        return stores;
    }
}
