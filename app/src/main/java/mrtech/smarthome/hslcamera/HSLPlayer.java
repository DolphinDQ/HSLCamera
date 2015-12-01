package mrtech.smarthome.hslcamera;

/**
 * 实现视频语音播放，实现对讲功能。
 * Created by zdqa1 on 2015/11/26.
 */

/**
 * HSL 摄像头播放器
 * Created by zdqa1 on 2015/11/26.
 */
public interface HSLPlayer {

    /**
     * 播放指定ID的是像头。
     *
     * @param deviceId 摄像头ID。
     */
    void play(String deviceId);

    /**
     * 播放指定摄像头
     *
     * @param cam 指定摄像头
     */
    void play(HSLCamera cam);

    /**
     * 获取当前播放摄像头对象。
     *
     * @return 当前播放摄像头对象。
     */
    HSLCamera getPlaying();

    /**
     * 获取当期播放列表。
     *
     * @return 当期播放列表。
     */
    HSLCamera[] getPlayingList();

    /**
     * 停止播放。
     */
    void stop();

    /**
     * 设置渲染回调
     *
     * @param listener
     */
    void setRenderListener(RenderListener listener);

    /**
     * 开启或关闭视频声音。
     * @param on 开为true
     */
    void setAudio(boolean on);

    void autoPlay();

    /**
     * 获取当前声音状态。
     * @return 开为true
     */
    boolean isAudioOn();

    /**
     * 语音对讲开启或关闭
     * @param on 开启为true
     */
    void setTalk(boolean on);

    /**
     * 获取语音对讲状态。
     * @return 开启返回true
     */
    boolean isTalkOn();
}
