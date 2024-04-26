package org.example;

import com.deepl.api.*;

import java.util.LinkedHashMap;
import java.util.List;

public class DeepLBuilder {
    private DeepL deepL;
    private String modID, key = "DeepL key";//deepL API의 키 값을 넣기.
    private String source = "en", target = "ko";
    private LinkedHashMap<String, GlossaryEntries> glossaryEntriesMap = new LinkedHashMap<>();


    private Translator translator;
    private LinkedHashMap<String, TextTranslationOptions> optionsMap;


    public DeepLBuilder() throws DeepLException, InterruptedException {
        deepL = new DeepL();

        optionsMap.put(modID, new TextTranslationOptions().setGlossary(translator.createGlossary(modID, source, target, glossaryEntriesMap.get(modID))));
    }

    /**
     * 사전 기능을 이용하려면 꼭 설정해야 합니다. (모드 아이디로 사전의 아이디로 설정하기 때문입니다)
     * 필수로 설정해야 하는 건 아닙니다.
     */
    public DeepLBuilder setModID(String modid){
        this.modID = modid;
        glossaryEntriesMap.put(modid, new GlossaryEntries());
        return this;
    }

    public DeepLBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * DeepL이 번역할 언어를 설정합니다(예시, en_us)
     */
    public DeepLBuilder setSourceLang(String source){
        this.source = source;
        return this;
    }

    /**
        DeepL이 출력할 언어를 설정합니다.(예시, ko_KR)
     */
    public DeepLBuilder setTargetLang(String target){
        this.target = target;
        return this;
    }

    /**
     * 특정 단어는 원하는 단어로만 번역하게 할 수 있는 사전 기능입니다
     */
    public DeepLBuilder putGlossary(String modID, String source, String target){
        glossaryEntriesMap.get(modID).put(source, target);
        return this;
    }

    public DeepL build(){
        translator = new Translator(key);
        return deepL;
    }

    public class DeepL {

        public void translate(String text) throws Exception{
            TextResult textResult = translator.translateText(text, source, target, optionsMap.get(modID));
            System.out.println(textResult.getText());
        }

        private void getGlossaryLanguagesExample() throws Exception {
            List<GlossaryLanguagePair> glossaryLanguages =
                    translator.getGlossaryLanguages();
            for (GlossaryLanguagePair glossaryLanguage : glossaryLanguages) {
                System.out.printf("%s to %s\n",
                        glossaryLanguage.getSourceLanguage(),
                        glossaryLanguage.getTargetLanguage());
                // Example: "en to de", "de to en", etc.
            }
        }
    }

}
