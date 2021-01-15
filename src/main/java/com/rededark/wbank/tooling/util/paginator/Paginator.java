package com.rededark.wbank.tooling.util.paginator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Paginator<T> {

    private final int size;
    private final List<T> collection;

    public Paginator(int size, T... objects) {
        this(size, Arrays.asList(objects));
    }

    public Paginator(int size, List<T> objects) {
        this.size = size; this.collection = objects;
    }

    public int getSize() {
        return size;
    }

    public List<T> getCollection() {
        return collection;
    }

    public int getTotal() {
        return (int) Math.ceil((double) collection.size() / size);
    }

    public boolean exists(int page) {
        if (page < 0) {
            return false;
        }

        if (page > getTotal()) {
            return false;
        }

        List<T> list = getPage(page);

        return list != null && !list.isEmpty();
    }

    public List<T> getPage(int page) {
        if (page < 0 || page >= getTotal()) return null;

        int min = page * size;
        int max = min + size;

        if (max > collection.size()) max = collection.size();

        final List<T> objects = new ArrayList<>();

        for (int i = min; max > i; i++) {
            objects.add(collection.get(i));
        }

        return objects;
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }
}
