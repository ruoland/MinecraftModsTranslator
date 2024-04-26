package org.example;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class Main {
    public static final Logger logger = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args){
        //마인크래프트 버전과 시작 언어, 번역할 언어를 알려 초기화 합니다. 아래의 값은 테스트를 위해 임의적으로 설정했습니다.
        logger.info("번역 시작...");
        ModsTranslator.init("1.10.2", "en_us", "ko_kr");

        ModsTranslator.loadMods();
    }

}
