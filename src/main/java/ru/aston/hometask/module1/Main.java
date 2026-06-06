package ru.aston.hometask.module1;

public class Main {
    public static void main(String[] args) {
        CustomHashMap<String, String> customHashMap = new CustomHashMap<String, String>();
        customHashMap.put("a", "b");
        customHashMap.put("c", "d");
        customHashMap.put("e", "f");
        customHashMap.put("1", "1");
        customHashMap.put("1", "2");
        customHashMap.put(null, null);
        customHashMap.put(null, "1");

        System.out.println(customHashMap);
        System.out.println(customHashMap.get("1"));
        System.out.println(customHashMap.remove("a"));
        System.out.println(customHashMap);
    }
}