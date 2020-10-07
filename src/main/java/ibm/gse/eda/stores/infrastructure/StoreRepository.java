package ibm.gse.eda.stores.infrastructure;

import java.util.HashMap;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import ibm.gse.eda.stores.domain.Store;
import io.smallrye.mutiny.Multi;

@ApplicationScoped
public class StoreRepository {
    
    HashMap<String,Store> stores;

    public StoreRepository(){
        stores = new HashMap<String,Store>();
        Store s = new Store("Store_1","Santa Clara", "CA", "95051");
        stores.put(s.name, s);
        s = new Store("Store_2","Ithaca", "NY", "14850");
        stores.put(s.name, s);
        s = new Store("Store_3","San Diego", "CA", "91932");
        stores.put(s.name, s);
        s = new Store("Store_4","Los Angeles", "CA", "90001");
        stores.put(s.name, s);
        s = new Store("Store_5","San Francisco", "CA", "94016");
        stores.put(s.name, s);
    }

    public Multi<Store> getStores() {
        return Multi.createFrom().items(stores.values().stream());
    }

    public String[] getStoreNames(){
        Set<String> keys = stores.keySet();
        return keys.toArray(new String[keys.size()]);
    }
}
