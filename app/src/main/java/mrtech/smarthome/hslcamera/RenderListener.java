package mrtech.smarthome.hslcamera;

/**
 * Created by sphynx on 2015/12/1.
 */
public interface RenderListener {
    void initComplete(int size, int width, int height);

    void takePicture(byte[] imageBuffer, int width, int height);
}
