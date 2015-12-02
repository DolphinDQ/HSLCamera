package mrtech.smarthome.hslcamera;

/**
 * camera media player
 * Created by zdqa1 on 2015/11/26.
 */
public interface HSLPlayer {

    void play(String deviceId);


    void play(HSLCamera cam);


    HSLCamera getPlaying();


    HSLCamera[] getPlayingList();


    void stop();


    void autoPlay();


    boolean isAudioOn();


    void setTalk(boolean on);


    boolean isTalkOn();


    void setRenderListener(RenderListener listener);


    void setAudio(boolean on);
}
