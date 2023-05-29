package com.szhyy.relay;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ConfigEditDialog extends Dialog {

    private EditText etInstruct, etValue;
    private Spinner spType;

    private Button btnSubmit, btnCancel;

    private View.OnClickListener onSubmitClickListener, onCancelClickListener;

    private ConfigEntity configEntity;

    private String [] typeItems = new String[]{"Open WebView", "Open Application"};

    public ConfigEditDialog(@NonNull Context context) {
        super(context);
    }

    public ConfigEditDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfigEditDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public ConfigEditDialog setOnSubmitClickListener(View.OnClickListener onSubmitClickListener) {
        this.onSubmitClickListener = onSubmitClickListener;
        return this;
    }

    public ConfigEditDialog setOnCancelClickListener(View.OnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_config_edit);

        configEntity = new ConfigEntity();
        configEntity.setType(typeItems[0]);
        configEntity.setCheck(false);

        etInstruct = findViewById(R.id.et_instruct);
        etValue = findViewById(R.id.et_value);
        spType = findViewById(R.id.sp_type);
        btnSubmit = findViewById(R.id.btn_submit);
        btnCancel = findViewById(R.id.btn_cancel);

        btnSubmit.setOnClickListener(v->{
            if(this.onSubmitClickListener!=null){

                String instruct = etInstruct.getText().toString();
                if(TextUtils.isEmpty(instruct)){
                    Toast.makeText(getContext(), "Input Instruct", Toast.LENGTH_SHORT).show();
                    return;
                }
                String value = etValue.getText().toString();

                if(TextUtils.isEmpty(value)){
                    Toast.makeText(getContext(), "Input Operate Value", Toast.LENGTH_SHORT).show();
                    return;
                }

                configEntity.setInstruct(instruct);
                configEntity.setValue(value);
                v.setTag(configEntity);
                this.onSubmitClickListener.onClick(v);
            }
        });

        btnCancel.setOnClickListener(v->{
            if(this.onCancelClickListener!=null){
                this.onCancelClickListener.onClick(v);
            }
        });

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, typeItems);
        spType.setAdapter(itemsAdapter);

        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = typeItems[position];
                configEntity.setType(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
