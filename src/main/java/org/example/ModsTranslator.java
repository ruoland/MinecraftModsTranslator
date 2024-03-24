package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.Level;

import java.io.*;
import java.util.HashMap;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModsTranslator {
    private static HashMap<String, String> skipMods = new HashMap<>();
    private static File skipFile = new File("./skipMods.json");
    private static String sourceLang, targetLang, minecraftVersion;

    private ModsTranslator(){
        throw new IllegalStateException("Utility");
    }
    protected static void init(String mcVersion, String sourceLang, String targetLang){
        ModsTranslator.minecraftVersion = mcVersion;
        ModsTranslator.sourceLang = sourceLang;
        ModsTranslator.targetLang = targetLang;
    }
    public static void loadMods(){
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

    private static void createModJar(File jarFile) throws Exception{
        ModJar modJar = new ModJar(new JarFile(jarFile.getAbsoluteFile()));

        if(!skipMods.containsKey(modJar.getModInfo())) {
            boolean canTranslate = modJar.canTranslate();

            //번역이 가능한지 불가능한지 확인
            if(canTranslate) {
                translate(modJar);
                return;
            }
            else
                skipMods.put(modJar.getModInfo(), modJar.getReason());
        }
        Main.logger.info("{}의 {} 버전은 번역을 하지 않음. 이유:{}", modJar.getModID(), modJar.getModVersion(), ModsTranslator.skipMods.get(modJar.getModInfo()));

    }

    public static void translate(ModJar modJar) throws Exception {
        Main.logger.info("번역 시작! {}", modJar);

        Thread thread = new ThreadMod(modJar);
        thread.start();

    }

    public static String getSourceLang() {
        return sourceLang;
    }

    public static String getTargetLang() {
        return targetLang;
    }

    public static void loadSkipMods() throws Exception{
        Gson gson = new Gson();
        if(!skipFile.isFile()) {
            skipFile.createNewFile();
        }
        else{
            FileReader fr = new FileReader(skipFile);
            HashMap loadMap = gson.fromJson(fr, HashMap.class);
            if(loadMap != null)
                skipMods = loadMap;
        }
    }

    public static void saveSkipMods() throws Exception{
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
