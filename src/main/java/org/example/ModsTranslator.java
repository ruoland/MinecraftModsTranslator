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
    private DeepLBuilder.DeepL deepL;
    private String minecraftVersion;

    private HashMap<String, String> skipMods = new HashMap<>();
    private File skipFile = new File("./skipMods.json");

    private String sourceLang, targetLang;
    public ModsTranslator(String mcVersion, String sourceLang, String targetLang){
        this.minecraftVersion = mcVersion;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
    }
    public void loadMods(){
        try {
            File modsFolder = new File("./mods"); // 프로그램 설치 폴더에 있는 mods 폴더
            File versionFolder = new File(modsFolder, minecraftVersion);//mods 폴더에 혹시 1.20.4, 1.19.2 처럼 모드가 버전별로 분리되어 있을 때를 대비해서
            loadSkipMods();

            if (versionFolder.isDirectory())
                modsFolder = versionFolder;
            else
                modsFolder.mkdirs();

            for (File jarFile : modsFolder.listFiles()) {
                if (jarFile.getName().contains(".jar")) {
                    createModJar(jarFile);
                }
            }

            saveSkipMods();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createModJar(File jarFile) throws Exception{
        ModJar modJar = new ModJar(new JarFile(jarFile.getAbsoluteFile()));
        modJar.setLang(sourceLang, targetLang);

        if(!skipMods.keySet().contains(modJar.getModInfo())) {
            boolean canTranslate = modJar.canTranslate();

            if(canTranslate) {
                modJar.translate();
                return;
            }
            else
                skipMods.put(modJar.getModInfo(), modJar.getReason());
        }
        System.out.println(modJar.getModID() + "의 " +modJar.getModVersion()+ " 버전은 번역을 하지 않음. 이유:" + skipMods.get(modJar.getModInfo()));

    }

    public void loadSkipMods() throws Exception{
        Gson gson = new Gson();
        if(!skipFile.isFile()) {
            skipFile.createNewFile();
        }
        else{
            FileReader fr = new FileReader(skipFile);
            skipMods = gson.fromJson(fr, HashMap.class);
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


}
