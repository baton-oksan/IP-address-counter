package org.example;

public class Main {
    public static void main(String[] args) {
        IPCounter counter = new IPCounter("/Users/sharon/task_ip_addresses_compressed.zip");
        long result = counter.count();
        System.out.println("Кол-во уникальных IP адресов в файле: " + result);
    }
}