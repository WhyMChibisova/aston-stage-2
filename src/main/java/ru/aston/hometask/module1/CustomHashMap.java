package ru.aston.hometask.module1;

import java.util.Objects;

public class CustomHashMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int capacity;
    private final float loadFactor;
    private int size;

    private Entry<K, V>[] table;

    public CustomHashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public CustomHashMap(int capacity, float loadFactor) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        if (loadFactor <= 0 || loadFactor > 1) {
            throw new IllegalArgumentException("Load factor must be between 0 and 1");
        }
        this.table = new Entry[capacity];
        this.capacity = capacity;
        this.loadFactor = loadFactor;
    }

    static class Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            Entry<K, V> temp = this;
            StringBuilder stringBuilder = new StringBuilder();
            while (temp != null) {
                stringBuilder.append(temp.key + " -> " + temp.value);
                if (temp.next != null) {
                    stringBuilder.append(", ");
                }
                temp = temp.next;
            }

            return stringBuilder.toString();
        }
    }

    private int hash(K key) {
        return (key == null) ? 0 : Math.abs(key.hashCode()) % capacity;
    }

    private boolean keysEquals(K key1, K key2) {
        return Objects.equals(key1, key2);
    }

    private void ensureCapacity() {
        if (size >= capacity * loadFactor) {
            int newCapacity = 2 * capacity;
            Entry<K, V>[] newTable = new Entry[newCapacity];

            for (Entry<K, V> entry : table) {
                while (entry != null) {
                    Entry<K, V> nextEntry = entry.next;
                    int newIndex = (entry.getKey() == null)
                            ? 0
                            : Math.abs(entry.getKey().hashCode()) % newCapacity;
                    entry.next = newTable[newIndex];
                    newTable[newIndex] = entry;
                    entry = nextEntry;
                }
            }

            table = newTable;
            capacity = newCapacity;
        }
    }

    public V put(K key, V value) {
        ensureCapacity();

        int index = hash(key);
        Entry<K, V> current = table[index];

        if (current == null) {
            table[index] = new Entry<K, V>(key, value);
            size++;
            return null;
        }

        Entry<K, V> prev = null;
        while (current != null) {
            if (keysEquals(current.getKey(), key)) {
                V oldValue = current.getValue();
                current.setValue(value);
                return oldValue;
            }

            prev = current;
            current = current.next;
        }

        prev.next = new Entry<>(key, value);
        size++;
        return null;
    }

    public V get(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];

        while (current != null) {
            if (keysEquals(current.getKey(), key)) {
                return current.getValue();
            }
            current = current.next;
        }

        return null;
    }

    public V remove(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (keysEquals(current.getKey(), key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return current.getValue();
            }
            prev = current;
            current = current.next;
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < capacity; i++) {
            stringBuilder.append(i + " ");
            if (table[i] != null) {
                stringBuilder.append(table[i]);
            } else {
                stringBuilder.append("null");
            }
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }
}