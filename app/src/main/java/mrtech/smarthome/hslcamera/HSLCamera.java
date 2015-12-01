package mrtech.smarthome.hslcamera;

import java.util.Objects;

/**
 * HSL camera object
 * Created by zdqa1 on 2015/11/25.
 */
public class HSLCamera {
    public HSLCamera(Object source, String camId, String userName, String password) {
        mSource = source;
        mCamId = camId;
        mUserName = userName;
        mPassword = password;
    }

    /**
     * 摄像头数据，保留
     */
    public final Object mSource;
    /**
     * P2P连接ID
     */
    public final String mCamId;
    /**
     * 登录用户
     */
    public final String mUserName;
    /**
     * 登录密码
     */
    public final String mPassword;
    /**
     * 设备状态
     */
    public HSLCameraStatus mStatus;

    /**
     * HSL camera status description
     * Created by zdqa1 on 2015/11/26.
     */
    public interface HSLCameraStatus {
        /**
         * 摄像头连接句柄，对应文档userId
         * @return
         */
        long getHandle();

        /**
         * 摄像头状态
         * @return 100是成功
         */
        int getStatus();

        /**
         * 是否处于播放中
         * @return
         */
        boolean isPlaying();


    }
}
