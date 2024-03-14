package org.example;

import com.deepl.api.*;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModsTranslator {
    private String from = "en_us", to= "ko_kr", minecraftVersion;
    private Translator translator;
    private GlossaryInfo myGlossary;
    private String modID, modVersion;
    private GlossaryEntries glossaryEntries;
    public ArrayList<String> skipMods = new ArrayList<>();
    public File skipFile = new File("./skipMods.json");

    public ModsTranslator(String mcVersion, String from, String to){
        this.from = from;
        this.to = to;
        this.minecraftVersion = mcVersion;
    }
    public void modsLoad(){
        try {
            File modsFolder = new File("./mods"); // 프로그램 설치 폴더에 있는 mods 폴더
            File versionFolder = new File(modsFolder, minecraftVersion);//mods 폴더에 혹시 1.20.4, 1.19.2 처럼 모드가 버전별로 분리되어 있을 때를 대비해서
            loadSkipMods();
            if (versionFolder.isDirectory()) {
                modsFolder = versionFolder;
            }
            modsFolder.mkdirs();
            for (File jarFile : modsFolder.listFiles()) {
                if (jarFile.getName().contains(".jar")) {
                    readJar(new JarFile(jarFile.getAbsoluteFile()));
                }
            }

            saveSkipMods();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void loadSkipMods() throws Exception{
        Gson gson = new Gson();
        if(!skipFile.isFile()) {
            skipFile.createNewFile();
        }
        else{
            FileReader fr = new FileReader(skipFile);
            skipMods = gson.fromJson(fr, ArrayList.class);
        }
    }

    public void saveSkipMods() throws Exception{
        Gson gson = new Gson();
        if(!skipFile.isFile()) {
            skipFile.createNewFile();
        }

        FileWriter fw = new FileWriter(skipFile);
        gson.toJson(skipMods, fw);
        fw.flush();
        fw.close();

    }

    public void readJar(JarFile jarFile) throws Exception {
        String modFileName = jarFile.getName();
        String modid = getModID(jarFile);

        if(skipMods.contains(modid+modVersion)) {
            System.out.println(modid+ "의 "+ modVersion+"은 번역이 스킵 됨");
            return;
        }

        System.out.println(modid + " - "+ modFileName); // 모드의 ID와 모드 파일 이름을 출력
        JarEntry enEntry = jarFile.getJarEntry("assets/" + modid + "/lang/"+from+".json");
        JarEntry koEntry = jarFile.getJarEntry("assets/" + modid + "/lang/"+to+".json");

        /*
         * 만약 enUS.lang가 있고 ko_kr.json가 없다면 true
         * 조건에 해당되지 않는다면 이미 번역된 모드이거나 번역할 게 없는 경우
         */
        boolean canTranslate = enEntry != null && koEntry == null;

        if (canTranslate) {
            System.out.println(modid + "translate start!");
            createJson();
            readLang(jarFile, enEntry);
        } else {
            //이미 번역 되어 있거나 혹은 번역할 파일이 없다면 이 모드는 이후부터 스킵 하게 됨.
            System.out.println(modid+ " Skipped. " + enEntry + " - "+ koEntry);
            skipMods.add(modID+modVersion);
        }
    }

    private String getModID(JarFile jarFile) throws Exception{
        Toml toml = new Toml().read(new InputStreamReader(jarFile.getInputStream(jarFile.getJarEntry("META-INF/mods.toml"))));
        Toml forgeTables = toml.getTables("mods").get(0);
        this.modID = forgeTables.getString("modId");
        this.modVersion = forgeTables.getString("version");
        return this.modID;
    }

    private void createJson() throws Exception{
        File jsonFile = new File("./"+to+".json");
        jsonFile.createNewFile();
    }

    private void readLang(JarFile jarFile, JarEntry jarEntry)throws Exception{
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(jarFile.getInputStream(jarEntry)));
        JsonObject object = element.getAsJsonObject();
        Set<String> keys = object.asMap().keySet();
        HashMap<String, String> translatorMap = new HashMap<>();

        for(String key : keys){
            translatorMap.put(key, object.get(key).getAsString() + " - 번역");
        }
    }

    private void deeplTranslate(String key) throws Exception{
        translator = new Translator("df0150e8-22ba-47b2-8a8d-c83346adbde1:fx");
        createGlossary();

        TextTranslationOptions options = new TextTranslationOptions().setGlossary(myGlossary);
        TextResult textResult = translator.translateText(key, "en", "ko", options);
        System.out.println(textResult.getText());
    }

    private void createGlossary() throws Exception{
        glossaryEntries = new GlossaryEntries();
        GlossaryInfo myGlossary = translator.createGlossary(modID, "en", "ko", glossaryEntries);
        this.myGlossary = myGlossary;
    }
    
    private void putGlossary(String source, String target){
        glossaryEntries.put(source, target);
    }

    public void getGlossaryLanguagesExample() throws Exception {
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
