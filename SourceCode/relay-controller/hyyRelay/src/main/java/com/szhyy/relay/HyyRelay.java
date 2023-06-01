package com.szhyy.relay;

public interface HyyRelay {

    interface RelayInputState {
        void on();
        void off();
    }
    /**
     * 打开设备
     * @param relayInputState 输入开关状态回调接口。 on函数监听开，off开关监听关
     * @return true-打开成功，false-打开失败
     */
    boolean open(RelayInputState relayInputState);

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
