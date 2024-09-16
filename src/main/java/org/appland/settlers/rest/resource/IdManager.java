package org.appland.settlers.rest.resource;

import java.util.HashMap;

public class IdManager {

    private final HashMap<Object, Integer> objectToId;
    private int ids;
    private final HashMap<Integer, Object> idToObject;
    final static IdManager idManager = new IdManager();

    public IdManager() {
        objectToId = new HashMap<>();
        idToObject = new HashMap<>();
        ids = 0;
    }

    public String getId(Object o) {
        synchronized (objectToId) {
            if (!objectToId.containsKey(o)) {
                ids++;

                objectToId.put(o, ids);
                idToObject.put(ids, o);
            }
        }

        return Integer.toString(objectToId.get(o));
    }

    public Object getObject(String id) {
        int numberId = Integer.parseInt(id);

        return idToObject.get(numberId);
    }

    Object getObject(int id) {
        return idToObject.get(id);
    }

    void remove(Object gameObject) {
        int id = objectToId.get(gameObject);
        objectToId.remove(gameObject);
        idToObject.remove(id);
    }

    public void updateObject(Object oldObject, Object updatedObject) {
        int id = objectToId.get(oldObject);

        objectToId.remove(oldObject);
        idToObject.remove(id);

        idToObject.put(id, updatedObject);
        objectToId.put(updatedObject, id);
    }
}
