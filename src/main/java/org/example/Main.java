package org.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
    public static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args){
        ModsTranslator.init("1.10.2", "en_us", "ko_kr");
        ModsTranslator.loadMods();
    }

}
