package mrtech.com.hslcamera;

/**
 * PTZ控制
 * Created by zdqa1 on 2015/11/28.
 */
public interface HSLCameraController {

    HSLCamera getCurrent();

    void ptzLeft();

    void ptzRight();

    void ptzUp();

    void ptzDown();


}
