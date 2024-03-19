package org.example;

import com.deepl.api.*;

import java.util.List;

public class DeepLBuilder {
    private DeepL deepL;
    private String modID, key = "df0150e8-22ba-47b2-8a8d-c83346adbde1:fx";
    private String source = "en", target = "ko";
    private GlossaryEntries glossaryEntries;


    private Translator translator;
    private TextTranslationOptions options;


    public DeepLBuilder() throws DeepLException, InterruptedException {
        deepL = new DeepL();
        glossaryEntries = new GlossaryEntries();
        options = new TextTranslationOptions().setGlossary(translator.createGlossary(modID, source, target, glossaryEntries));
    }

    public DeepLBuilder setModID(String modid){
        this.modID = modid;
        return this;
    }

    public DeepLBuilder setKey(String key) {
        this.key = key;
        return this;
    }

    public DeepLBuilder setSourceLang(String source){
        this.source = source;
        return this;
    }

    public DeepLBuilder setTargetLang(String target){
        this.target = target;
        return this;
    }

    public DeepLBuilder putGlossary(String source, String target){
        glossaryEntries.put(source, target);
        return this;
    }

    public DeepL build(){
        translator = new Translator(key);
        return deepL;
    }

    public class DeepL {

        public void translate(String text) throws Exception{
            TextResult textResult = translator.translateText(text, source, target, options);
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
