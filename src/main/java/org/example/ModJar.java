package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moandjiezana.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModJar {
    private JarFile modFile;
    private JarEntry sourceEntry;
    private String modVersion;
    private String modID;

    private String sourceLang, targetLang;

    public ModJar(JarFile modFile) {
        this.modFile = modFile;
    }

    public void setLang(String sourceLang, String targetLang){
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }

    public boolean canTranslate() throws Exception {
        String modID = getModID();

        JarEntry sourceEntry = modFile.getJarEntry("assets/" + modID + "/lang/" + sourceLang + ".json");
        JarEntry targetEntry = modFile.getJarEntry("assets/" + modID + "/lang/" + targetLang + ".json");

        boolean canTranslate = sourceEntry != null && targetEntry == null;

        if(!canTranslate) {
            if (targetEntry != null) {
                System.out.println(targetEntry.getRealName() + " 모드는 이미 "+targetLang+" 파일이 존재합니다. 번역할 필요가 없는 모드인 것 같습니다. (현재 이 프로그램은 파일 이어서 번역하기는 지원하지 않습니다.)");
                reason = "이미 "+targetLang+" 파일이 존재하는 모드";
            } else if (sourceEntry == null) {
                System.out.println(getModID() + " 모드는 번역할 파일이 존재하지 않습니다. 시작 번역 언어가 잘못됐거나 번역할 내용 자체가 없는 것 같습니다.");
                reason = sourceLang+" 파일이 존재하지 않는 모드";
            }
        }
        return canTranslate;
    }

    private String reason;
    public String getReason(){
        return reason;
    }

    public void translate() throws Exception {
        System.out.println(modID + "translate start!");

        createJson();
        translateLang(modFile, sourceEntry);
    }

    private void createJson(){
        File file = new File("./"+targetLang+".json");
        try {
            if(!file.isFile())
                file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void translateLang(JarFile jarFile, JarEntry jarEntry) throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(jarFile.getInputStream(jarEntry)));
        JsonObject object = element.getAsJsonObject();
        Set<String> keys = object.asMap().keySet();
        HashMap<String, String> translatorMap = new HashMap<>();

        for (String key : keys) {
            translatorMap.put(key, object.get(key).getAsString() + " - 번역");
            System.out.println(object.get(key).getAsString());
        }
    }

    private String modID() throws Exception {
        Toml toml = new Toml().read(new InputStreamReader(modFile.getInputStream(modFile.getJarEntry("META-INF/mods.toml"))));
        Toml forgeTables = toml.getTables("mods").get(0);
        this.modID = forgeTables.getString("modId");
        this.modVersion = forgeTables.getString("version");
        return this.modID;
    }

    public String getModInfo() {
        try {
            return getModID() + modVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getModID() {
        if(modID == null) {
            try {
                modID();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return modID;

    }

    public String getModVersion() {
        try {
            return modVersion;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
