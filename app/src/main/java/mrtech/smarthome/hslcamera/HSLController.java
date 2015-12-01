package mrtech.smarthome.hslcamera;

/**
 * 开关控制，参数设置
 * Created by zdqa1 on 2015/11/28.
 */
public interface HSLController {

    HSLCamera getCurrent();

    void ptzLeft();

    void ptzRight();

    void ptzUp();

    void ptzDown();

}
