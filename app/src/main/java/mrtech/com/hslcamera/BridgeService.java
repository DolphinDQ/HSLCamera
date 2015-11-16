/**
 * 
 */
package mrtech.com.hslcamera;


import hsl.p2pipcam.nativecaller.DeviceSDK;
import android.app.Service;
import android.bluetooth.BluetoothClass.Device;
import android.content.Intent;
import android.os.IBinder;

/**
 *@author wang.jingui
 */
public class BridgeService extends Service
{
	//监听回调接口
	private static DeviceStatusListener deviceStatusListener;
	private static PlayListener playListener;
	private static RecorderListener recorderListener;
	private static SettingsListener settingsListener;
	
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		DeviceSDK.initialize("");
		DeviceSDK.setCallback(this);
		DeviceSDK.networkDetect();
	}
	
	@Override
	public void onDestroy() {
		DeviceSDK.unInitSearchDevice();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		return super.onStartCommand(intent, flags, startId);
	}
	
	public static void setDeviceStatusListener(DeviceStatusListener deviceStatusListener) {
		BridgeService.deviceStatusListener = deviceStatusListener;
	}

	public static void setPlayListener(PlayListener playListener) {
		BridgeService.playListener = playListener;
	}


	public void setRecorderListener(RecorderListener recorderListener) {
		BridgeService.recorderListener = recorderListener;
	}
	
	

	//-------------------------------------------------------------------------
	//---------------------------以下是JNI层回调的接口-------------------------------
	//-------------------------------------------------------------------------
	
	public static void setSettingsListener(SettingsListener settingsListener) {
		BridgeService.settingsListener = settingsListener;
	}

	public void CallBack_SnapShot(long UserID, byte[] buff, int len)
	{}
	
	
	public void CallBack_GetParam(long UserID, long nType, String param) 
	{
		if(settingsListener != null)
			settingsListener.callBack_getParam(UserID, nType, param);
	}
	

	public void CallBack_SetParam(long UserID, long nType, int nResult) 
	{
		if(settingsListener != null)
			settingsListener.callBack_setParam(UserID, nType, nResult);
	}
	
	public void CallBack_Event(long UserID, long nType) 
	{
		int status = new Long(nType).intValue();
		if(deviceStatusListener != null)
			deviceStatusListener.receiveDeviceStatus(UserID,status);
	}
	
	public void VideoData(long UserID, byte[] VideoBuf, int h264Data, int nLen, int Width, int Height, int time) 
	{
		
	}
	
	public void callBackAudioData(long nUserID, byte[] pcm, int size)
	{
		if(playListener != null)
			playListener.callBackAudioData(nUserID, pcm, size);
		if(recorderListener != null)
			recorderListener.callBackAudioData(nUserID, pcm, size);
	}
	
	public void CallBack_RecordFileList(long UserID, int filecount, String fname, String strDate, int size)
	{
		if(settingsListener != null)
			settingsListener.recordFileList(UserID, filecount, fname, strDate, size);
	}
	
	public void CallBack_P2PMode(long UserID, int nType)
	{
	}

	public void CallBack_RecordPlayPos(long userid, int pos)
	{}

	public void CallBack_VideoData(long nUserID, byte[] data, int type, int size) {}
	
	public void CallBack_AlarmMessage(long UserID, int nType) 
	{}
	
	public void showNotification(String message, Device device,int nType)
	{}
	
}
