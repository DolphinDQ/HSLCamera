package mrtech.smarthome.hslcamera;

/**
 * ý�崦����ʵ����Ƶ�������ţ�ʵ�ֶԽ����ܡ�
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
     * �Զ����š�
     */
    void autoPlay();
    /**
     * ��ȡ��ǰ����״̬��
     * @return ��Ϊtrue
     */
    boolean isAudioOn();
    /**
     * �����Խ�������ر�
     * @param on ����Ϊtrue
     */
    void setTalk(boolean on);
    /**
     * ��ȡ�����Խ�״̬��
     * @return ��������true
     */
    boolean isTalkOn();
    /**
     * ������Ⱦ�ص�
     *
     * @param listener
     */
    void setRenderListener(RenderListener listener);
    /**
     * ������ر���Ƶ������
     * @param on ��Ϊtrue
     */
    void setAudio(boolean on);
}
