package org.example;

public class Main {

    public static void main(String[] args){
        ModsTranslator modsTranslator = new ModsTranslator("1.10.2", "en_us", "ko_kr");
        modsTranslator.modsLoad();
    }

}
