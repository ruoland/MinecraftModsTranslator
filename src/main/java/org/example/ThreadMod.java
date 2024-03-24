package org.example;

import com.google.gson.*;
import com.sun.source.tree.Tree;

import java.io.*;
import java.util.*;

public class ThreadMod extends Thread{
    private ModJar mod;
    private File targetFile;
    protected ThreadMod(ModJar modJar){
        this.mod = modJar;
    }

    @Override
    public synchronized void start() {
        super.start();

        try {
            createJson();
            translateLang();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    private void createJson(){
        File folder = new File("./", mod.getModID());
        folder.mkdirs();

        targetFile = new File(folder, "/"+ModsTranslator.getTargetLang()+".lang");
        try {
            if(!targetFile.isFile())
                targetFile.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void translateLang() throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(mod.getModFile().getInputStream(mod.getLangEntry())));
        JsonObject object = element.getAsJsonObject();

        ArrayList<String> keys = new ArrayList<>(object.keySet());
        TreeMap<String, String> translatorMap = new TreeMap<>();

        for (String key : keys) {
            translatorMap.put(key, object.get(key).getAsString() + " - 번역");
        }

        write(translatorMap);
    }

    private void write(TreeMap<String, String> translatorMap) throws Exception{
        FileWriter fw = new FileWriter(targetFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(translatorMap, fw);
        fw.flush();
        fw.close();
    }

}
