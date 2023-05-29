package com.szhyy.relay;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigPrefUtils {

    private SharedPreferences sharedPreferences;
    private static ConfigPrefUtils ConfigPrefUtils;

    public static ConfigPrefUtils getInstance(Context context){
        if(ConfigPrefUtils==null) {
            ConfigPrefUtils = new ConfigPrefUtils();
            ConfigPrefUtils.sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return ConfigPrefUtils;
    }

    private boolean containsConfig( List<ConfigEntity> configEntityList, ConfigEntity configEntity){
        if(configEntityList.size()==0){
            return false;
        }
        for(ConfigEntity entity: configEntityList){
            if(Objects.equals(entity.getInstruct(), configEntity.getInstruct())){
                return true;
            }
        }
        return false;
    }


    public void addConfig(ConfigEntity configEntity){

        List<ConfigEntity> configEntityList = listConfig();
        if(!containsConfig(configEntityList, configEntity)){
            configEntityList.add(configEntity);
            sharedPreferences.edit().putString("config", JSON.toJSONString(configEntityList)).apply();
        }
    }

    public ConfigEntity getConfig(String instruct){

        List<ConfigEntity> configEntityList = listConfig();
        for(ConfigEntity config : configEntityList){
            if(Objects.equals(config.getInstruct(), instruct)){
                return config;
            }
        }
        return null;
    }

    public List<ConfigEntity> listConfig(){
        String configStr = sharedPreferences.getString("config", "");
        if(TextUtils.isEmpty(configStr)){
            return new ArrayList<>();
        }
        return JSON.parseArray(configStr, ConfigEntity.class);
    }

    public void deleteConfig(String instruct){
        List<ConfigEntity> configEntityList = listConfig();
        List<ConfigEntity> tmpList = new ArrayList<>();
        for(ConfigEntity config : configEntityList){
            if(!Objects.equals(config.getInstruct(), instruct)){
                tmpList.add(config);
            }
        }
        sharedPreferences.edit().putString("config", JSON.toJSONString(tmpList)).apply();
    }

    public void clear(){
        sharedPreferences.edit().putString("config", "").apply();
    }

}
