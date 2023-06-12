package com.szhyy.relay;

public interface HyyRelay {

    /**
     * 开关开关事件回调监听。 on - 开关按下事件。 off - 开关放开事件
     */
    interface RelaySwitchingEventListener {
        void on();
        void off();
    }
    /**
     * 打开串口，连接串口设备
     * @param relayInputState 输入开关状态回调接口。 on函数监听开，off开关监听关
     * @return true-打开成功，false-打开失败
     */
    boolean open(RelaySwitchingEventListener relayInputState);

    /**
     * 打开继电器，
     * @return true-打开成功，false-打开失败
     */
    boolean relayOn();

    /**
     * 关闭继电器
     * @return true-关闭成功，false-关闭失败
     */
    boolean relayOff();

    /**
     * 打开mos
     * @return true-打开成功，false-打开失败
     */
    boolean mosOn();

    /**
     * 关闭mos
     * @return true-关闭成功，false-关闭失败
     */
    boolean mosOff();

    /**
     * 关闭设备
     * @return true-关闭成功，false-关闭失败
     */
    boolean close();
}
