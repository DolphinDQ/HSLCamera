package mrtech.smarthome.hslcamera;

/**
 * PTZ控制
 * Created by zdqa1 on 2015/11/28.
 */
public interface HSLController {

    HSLCamera getCurrent();

    /**
     * 云台左
     */
    void ptzLeft();

    void ptzRight();

    void ptzUp();

    void ptzDown();



}
