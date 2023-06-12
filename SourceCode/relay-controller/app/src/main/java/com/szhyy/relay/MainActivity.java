package com.szhyy.relay;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView rvConfigs;

    private Button btnAddConfig, btnRemoveConfig;

    private ConfigRVAdapter configRVAdapter;

    private List<ConfigEntity> configEntityList = null;

    private ConfigPrefUtils configPrefUtils;

    private Button btnRelayOn,btnRelayOff,btnMoseOn,btnMoseOff;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        char pressedKey = (char) event.getUnicodeChar();
        Log.d("owenstar", "onKeyDown: keyCode "+ keyCode +" pressedKey "+pressedKey );
        ConfigEntity configEntity = ConfigPrefUtils.getInstance(this).getConfig(String.valueOf(pressedKey));
        if(configEntity!=null){
            Log.d("owenstar", "config " + configEntity );
            String type = configEntity.getType();
            switch (type){
                case "Open WebView": {
                    Intent intent = new Intent(this, InnerWebViewActivity.class);
                    intent.putExtra("url", configEntity.getValue());
                    startActivity(intent);
                    break;
                }

                case "Open Application": {
                    String packageName = configEntity.getValue();
                    launchApp(packageName);
                    break;
                }

            }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void launchApp(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        if(resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i=new Intent(Intent.ACTION_MAIN);

            i.setFlags(FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            startActivity(i);
        } else {
            Toast.makeText(this, packageName+" 未安装", Toast.LENGTH_SHORT).show();
        }
    }

    private List<ConfigEntity> getCheckConfigList(){
        List<ConfigEntity> entities = new ArrayList<>();
        for(ConfigEntity config: configEntityList){
            if(config.isCheck()){
                entities.add(config);
            }
        }
        return entities;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.startRelayControllerService(this);

        configPrefUtils = ConfigPrefUtils.getInstance(this);
        configEntityList = configPrefUtils.listConfig();
        btnAddConfig = findViewById(R.id.btn_add_config);
        btnRemoveConfig = findViewById(R.id.btn_delete_config);

        btnRelayOn = findViewById(R.id.btn_relay_on);
        btnRelayOff = findViewById(R.id.btn_relay_off);
        btnMoseOn=findViewById(R.id.btn_mos_on);
        btnMoseOff=findViewById(R.id.btn_mos_off);

        btnRelayOn.setOnClickListener(this);
        btnRelayOff.setOnClickListener(this);
        btnMoseOn.setOnClickListener(this);
        btnMoseOff.setOnClickListener(this);
        intTtyUSB0();

        btnRemoveConfig.setOnClickListener((v)->{
            List<ConfigEntity> checkList = getCheckConfigList();
            if(checkList.size()==0){
                Toast.makeText(this, "Select Config To Remove", Toast.LENGTH_SHORT).show();
                return;
            }

            for(ConfigEntity config: checkList){
                if(config.isCheck()){
                    configPrefUtils.deleteConfig(config.getInstruct());
                }
            }
            List<ConfigEntity> tmpList = configPrefUtils.listConfig();
            configEntityList.clear();
            if(tmpList.size()>0){
                configEntityList.addAll(tmpList);
            }
            configRVAdapter.notifyDataSetChanged();
        });
        btnAddConfig.setOnClickListener((v)->{
            ConfigEditDialog configEditDialog = new ConfigEditDialog(this);
            configEditDialog.setOnSubmitClickListener(view -> {

                ConfigEntity config = (ConfigEntity) view.getTag();
                Log.d("smallstar", config.toString());

                if(!containsConfig(config)) {
                    configPrefUtils.addConfig(config);
                    configEntityList.clear();
                    configEntityList.addAll(configPrefUtils.listConfig());
                    configRVAdapter.notifyDataSetChanged();
                }
                configEditDialog.dismiss();
            }).setOnCancelClickListener(view->{
                configEditDialog.dismiss();
            }).show();
        });
        rvConfigs = findViewById(R.id.rv_configs);
        rvConfigs.setLayoutManager(new LinearLayoutManager(this));
        configRVAdapter = new ConfigRVAdapter();
        rvConfigs.setAdapter(configRVAdapter);
    }

    private boolean containsConfig(ConfigEntity config){
        for(ConfigEntity configEntity : configEntityList){
            if(Objects.equals(config.getInstruct(), configEntity.getInstruct())){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        HyyRelayCtl.instance().close();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_relay_on:
                HyyRelayCtl.instance().relayOn();
                break;
            case R.id.btn_relay_off:
                HyyRelayCtl.instance().relayOff();
                break;
            case R.id.btn_mos_on:
                HyyRelayCtl.instance().mosOn();
                break;
            case R.id.btn_mos_off:
                HyyRelayCtl.instance().mosOff();
                break;
        }
    }

    private void intTtyUSB0() {

        HyyRelayCtl hyyRelayCtl = HyyRelayCtl.instance();
        boolean opened = hyyRelayCtl.open(new HyyRelay.RelaySwitchingEventListener() {
            @Override
            public void on() {
                hyyRelayCtl.relayOn();
            }

            @Override
            public void off() {
                hyyRelayCtl.relayOff();
            }
        });

        Intent intent = new Intent();
        intent.setAction("com.szhyy.broadcaster.USB_STATE");
        if(opened){
            intent.putExtra("connect", "success");
        } else {
            intent.putExtra("connect", "failed");
        }
        sendBroadcast(intent);
    }

    private class ConfigRVAdapter extends RecyclerView.Adapter<ConfigVH> {

        @NonNull
        @Override
        public ConfigVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.config_item, null);
            return new ConfigVH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ConfigVH holder, int position) {
            ConfigEntity config = configEntityList.get(position);
            holder.tvInstruct.setText(config.getInstruct());
            holder.tvType.setText(config.getType());
            holder.tvValue.setText(config.getValue());
            holder.llItem.setOnClickListener((v->{
                config.setCheck(!config.isCheck());
                configRVAdapter.notifyDataSetChanged();
            }));
            holder.checkBox.setChecked(config.isCheck());
        }

        @Override
        public int getItemCount() {
            return configEntityList.size();
        }
    }

    private class ConfigVH extends RecyclerView.ViewHolder {

        private TextView tvInstruct, tvType, tvValue;

        private LinearLayout llItem;

        private CheckBox checkBox;

        public ConfigVH(@NonNull View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.ll_item);
            tvInstruct = itemView.findViewById(R.id.tv_instruct);
            tvType = itemView.findViewById(R.id.tv_type);
            tvValue = itemView.findViewById(R.id.tv_value);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

}