package com.szhyy.relay;


import android.text.TextUtils;


import com.vi.vioserial.NormalSerial;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HyyRelayCtl implements HyyRelay {

    private boolean isOpenSerial = false;

    private static HyyRelayCtl instance =new HyyRelayCtl();


    private ExecutorService executor = Executors.newFixedThreadPool(1);

    public static HyyRelayCtl instance(){
        return instance;
    }

    @Override
    public boolean open(RelayInputState relayInputState) {
        String ck = "/dev/ttyUSB0";
        int btl = 115200;
        if (!isOpenSerial) {
            // 【第1步：打开串口 Open serial port 】
            int openStatus = NormalSerial.instance().open(ck, btl);
            if (openStatus != 0) {
                return false;
            }
            //【添加数据接收回调 Add data receive callback】
            NormalSerial.instance().addDataListener(hexData -> {
                //Log.d("owenstar", "data back " + hexData);
                if(!TextUtils.isEmpty(hexData)){
                    int len = hexData.length();
                    String command = hexData.substring(len-8, len);

                    switch (command){
                        case "A10101A3": {   // A10101A3 打开开关
                            relayInputState.on();
                            break;
                        }
                        case "A10100A2": {  // A10100A2 关闭开关
                            relayInputState.off();
                            break;
                        }
                    }
                }
            });
            isOpenSerial = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean relayOn() {
        if(!NormalSerial.instance().isOpen()) {
            return false;
        }
        executor.submit(()-> NormalSerial.instance().sendHex("A00103A4"));
        return true;
    }

    @Override
    public boolean relayOff() {
        if(!NormalSerial.instance().isOpen()) {
            return false;
        }
        executor.submit(()-> NormalSerial.instance().sendHex("A00102A3"));
        return true;
    }

    @Override
    public boolean mosOn() {
        return false;
    }

    @Override
    public boolean mosOff() {
        return false;
    }

    @Override
    public boolean close() {
        isOpenSerial= false;
        executor.shutdownNow();
        NormalSerial.instance().close();
        return true;
    }
}
