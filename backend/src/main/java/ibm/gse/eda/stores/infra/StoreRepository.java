package ibm.gse.eda.stores.infra;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import ibm.gse.eda.stores.domain.Item;
import ibm.gse.eda.stores.domain.Store;
import io.smallrye.mutiny.Multi;

@Singleton
public class StoreRepository  {
    static Logger logger = Logger.getLogger("StoreRepository");
    
    static HashMap<String,Store> stores =new HashMap<String,Store>();

    public StoreRepository(){
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

    public Collection<String> getStoreNameList(){
        return stores.keySet();
    }

    public List<Item> buildRandomItems(int nbItem) {
        List<Item> items = new ArrayList<Item>();
        for (int i = 0; i < nbItem; i++) {
            items.add(Item.buildRandomItem(i,getStoreNames()));
            logger.log(Level.FINE,items.toString());
        }
        return items;
    }

    public List<Item>  buildControlledItems() {
        List<Item> items = new ArrayList<Item>();
        items.add(new Item("Store_1","Item_1",Item.RESTOCK,10,30));
        items.add(new Item("Store_1","Item_2",Item.RESTOCK,5,20));
        items.add(new Item("Store_1","Item_3",Item.RESTOCK,15,10));
        items.add(new Item("Store_2","Item_1",Item.RESTOCK,10,30));
        items.add(new Item("Store_2","Item_2",Item.RESTOCK,10,20));
        items.add(new Item("Store_2","Item_3",Item.RESTOCK,20,10));
        logger.log(Level.FINE,"Build controlledItems");
        return items;
    }
}
