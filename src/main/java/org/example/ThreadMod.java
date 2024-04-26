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
    //우선 언어.json 파일을 만듭니다.
    private void createJson(){
        File folder = new File("./", mod.getModID());
        folder.mkdirs();

        targetFile = new File(folder, "/"+ModsTranslator.getTargetLang()+".lang");
        try {
            //목표하는 파일이 없을 경우 생성합니다.
            if(!targetFile.isFile())
                targetFile.createNewFile();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //모드 언어를 불러오고 번역하는 메서드입니다.
    private void translateLang() throws Exception {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(new InputStreamReader(mod.getModFile().getInputStream(mod.getLangEntry())));//모드 언어 파일을 불러옵니다.
        JsonObject object = element.getAsJsonObject();

        ArrayList<String> keys = new ArrayList<>(object.keySet());//언어 키 값을 뽑아냅니다. 예시 item_stick:막대기 에서 item_stick이 키입니다
        TreeMap<String, String> translatorMap = new TreeMap<>();//번역할 키 값을 담을 맵입니다

        for (String key : keys) {
            //번역하고, 넣습니다. 현재는 번역하는 대신, 키로 얻어온 값에 " - 번역"이란 문자열을 추가합니다.
            translatorMap.put(key, object.get(key).getAsString() + " - 번역");
        }

        write(translatorMap);
    }

    //번역한 파일을 저장합니다.
    private void write(TreeMap<String, String> translatorMap) throws Exception{
        FileWriter fw = new FileWriter(targetFile);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        gson.toJson(translatorMap, fw);
        fw.flush();
        fw.close();
    }

}
