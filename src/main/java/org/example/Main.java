package org.example;

public class Main {
    public static void main(String[] args) {
        IPCounter counter = new IPCounter();
        long result = counter.count("/Users/sharon/task_ip_addresses_compressed.zip");
        System.out.println("Кол-во уникальных IP адресов в файле: " + result);
    }
}