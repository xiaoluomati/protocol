package cn.edu.nju.writer;

import java.util.HashMap;

public class DataCache {

    private volatile static DataCache dataCache;

    private HashMap<String, StringBuilder> cache;

    public static synchronized DataCache getInstance() {
        if(dataCache == null){
            synchronized (DataCache.class){
                if (dataCache ==null) {
                    dataCache = new DataCache();
                }
            }
        }
        return dataCache;
    }

    private DataCache() {
        this.clearAll();

        this.cache.put("Temperature", new StringBuilder());
        this.cache.put("Light", new StringBuilder());
    }

    public String get(String key){
        synchronized (this){
            return cache.get(key).toString();
        }
    }

    public void append(String key, String value){
        synchronized (this){
            if(this.cache.containsKey(key))
                this.cache.get(key).append(value);
            else
                this.cache.put(key, new StringBuilder(value));
        }
    }

    public void clearAll(){
        this.cache = new HashMap<>();
    }

    public void clear(String key){
        synchronized (this){
            this.cache.get(key).setLength(0);
        }
    }

}
