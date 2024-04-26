package org.example;

import com.moandjiezana.toml.Toml;

import java.io.InputStreamReader;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class ModJar {
    private JarFile modFile;
    private JarEntry sourceEntry;
    private String modVersion;
    private String modID;


    /**
     * 한 모드 파일당 하나의 클래스
     */
    public ModJar(JarFile modFile) {
        this.modFile = modFile;
    }



    public boolean canTranslate() {
        //번역할 언어
        JarEntry sourceEntry = modFile.getJarEntry("assets/" + modID + "/lang/" + ModsTranslator.getSourceLang() + ".json");
        //번역 될 언어
        JarEntry targetEntry = modFile.getJarEntry("assets/" + modID + "/lang/" + ModsTranslator.getTargetLang() + ".json");

        this.sourceEntry = sourceEntry;

        //혹시 문제가 없는지 검사합니다.
        return checkReason(Objects.isNull(sourceEntry), Objects.nonNull(sourceEntry) && Objects.nonNull(targetEntry));
    }

    /**
     * @param isSourceNull 번역할 소스 파일 자체가 없는지 확인합니다.
     * @param isTargetNull 번역하고자 하는 언어 파일이 이미 생성 되어 있는지 확인합니다.
     * @return 확인 후 번역이 불가능하면 false를, 가능하면 true를 반환합니다.
     */
    private boolean checkReason(boolean isSourceNull, boolean isTargetNull){
        if(isSourceNull || isTargetNull) {
            makeReason(isTargetNull); //번역할 수 없으므로 만들 수 없는 이유를 생성합니다
            return false;
        }
        return true;
    }

    private void makeReason(boolean canTarget){
        StringBuffer reasonBuffer = new StringBuffer();
        if (canTarget) {
            Main.logger.info("이름의 {} 모드는 이미 {} 파일이 존재합니다. 번역할 필요가 없는 모드인 것 같습니다. (현재 이 프로그램은 파일 이어서 번역하기는 지원하지 않습니다.)", modID, ModsTranslator.getTargetLang());
            reason = reasonBuffer.append("이미").append(" ").append(ModsTranslator.getTargetLang()).append(" 파일이 존재합니다.").toString();
        } else {
            Main.logger.info("{} 모드는 번역할 파일이 존재하지 않습니다. 시작 번역 언어가 잘못됐거나 번역할 내용 자체가 없는 것 같습니다.", getModID());
            reason = reasonBuffer.append(ModsTranslator.getSourceLang()).append(" 파일이 존재하지 않는 모드").toString();
        }
    }

    private String reason;

    public String getReason(){
        return reason;
    }

    private String modID() throws Exception {
        //Toml 파일에 mods에 대한 정보가 들어있습니다. 예를 들어서 모드의 버전이나, 모드의 아이디, 등등.
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

    public JarEntry getLangEntry() {
        return sourceEntry;
    }

    public JarFile getModFile() {
        return modFile;
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
