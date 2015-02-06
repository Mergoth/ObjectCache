package ru.vlad.test.cache;


    import java.util.ConcurrentModificationException;
    import java.util.concurrent.ConcurrentHashMap;

    /**
     * Created by Vladislav on 22.12.2014.
     */
    public class ObjectCache {

        private static ConcurrentHashMap<String,String> cacheMap = new ConcurrentHashMap<String, String>();

        public static String get(String key) {
            key = key.intern();
            synchronized (key) {
                String val = null;
                while (val == null || val.equals("")) {
                    if (!cacheMap.containsKey(key)) {
                        cacheMap.put(key, "");
                        return null;
                    } else if (cacheMap.get(key).equals("")) {
                        try {
                            key.wait();
                        } catch (InterruptedException e) {
                            /** TODO: What to do if wait is interrupted?**/
                        }
                    } else {
                        val = cacheMap.get(key);
                        return val;
                    }
                }
                return val;
            }
        }

        public static void put(String key, String value) {
            key = key.intern();
            synchronized (key) {
                if (cacheMap.containsKey(key)&&cacheMap.get(key).equals("")) {
                    cacheMap.put(key, value);
                    key.notifyAll();
                }else {
                    throw new ConcurrentModificationException("Trying to put already existing key "+key+", value: "+value);
                }
            }
        }

        public static void clear() {
            cacheMap.clear();
        }
    }

}
