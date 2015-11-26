package mrtech.com.hslcamera;

/**
 * Created by zdqa1 on 2015/11/26.
 */

/**
 * HSL ����ͷ������
 * Created by zdqa1 on 2015/11/26.
 */
public interface HSLPlayer {

    /**
     * ����ָ��ID������ͷ��
     *
     * @param deviceId ����ͷID��
     */
    void play(String deviceId);

    /**
     * ����ָ������ͷ
     *
     * @param cam ָ������ͷ
     */
    void play(HSLCamera cam);

    /**
     * ��ȡ��ǰ��������ͷ����
     *
     * @return ��ǰ��������ͷ����
     */
    HSLCamera getPlaying();

    /**
     * ��ȡ���ڲ����б�
     *
     * @return ���ڲ����б�
     */
    HSLCamera[] getPlayingList();

    /**
     * ֹͣ���š�
     */
    void stop();

    /**
     * ������Ⱦ�ص�
     *
     * @param listener
     */
    void setRanderListener(HSLCameraManager.RenderListener listener);
}
