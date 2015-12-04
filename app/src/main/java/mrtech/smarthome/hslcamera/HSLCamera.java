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
     * user data
     */
    public final Object mSource;
    /**
     *  P2P connection string(deviceId)
     */
    public final String mCamId;
    /**
     * login user
     */
    public final String mUserName;
    /**
     * login password
     */
    public final String mPassword;
    /**
     * camera status
     */
    public HSLCameraStatus mStatus;

    /**
     * HSL camera status description
     * Created by zdqa1 on 2015/11/26.
     */
    public interface HSLCameraStatus {
        /**
         * camera connection handle(userId)
         * @return
         */
        long getHandle();

        /**
         * camera status
         * @return 100 success
         */
        int getStatus();

        /**
         * is camera play
         * @return
         */
        boolean isPlaying();

        /**
         * get reconnecting status
         * @return
         */
        boolean isReconnecting();


    }
}
