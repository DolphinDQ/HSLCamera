package mrtech.com.hslcamera;

import mrtech.com.hslcamera.BaseActivity;
import mrtech.com.hslcamera.MyRender;


import hsl.p2pipcam.nativecaller.DeviceSDK;
import hsl.p2pipcam.nativecaller.NativeCaller;
import hsl.p2pipcam.util.AudioPlayer;
import hsl.p2pipcam.util.CustomAudioRecorder;
import hsl.p2pipcam.util.CustomAudioRecorder.AudioRecordResult;
import hsl.p2pipcam.util.CustomBuffer;
import hsl.p2pipcam.util.CustomBufferData;
import hsl.p2pipcam.util.CustomBufferHead;
import mrtech.com.hslcamera.PlayListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;

//import com.test.R;

public class DevicePlayActivity extends BaseActivity implements PlayListener,MyRender.RenderListener,AudioRecordResult{
	private GLSurfaceView glSurfaceView;
	private MyRender myRender;
	private LinearLayout progressLayout;
	private Button audioItem;
	private Button recordItem;
	//private EditText nameText;
	private Button settingsBtn;
	private Button up_down_item,left_right_item;

	private long userid;
	private boolean isAudio = true;
	CustomAudioRecorder customAudioRecorder;
	private AudioPlayer audioPlayer;
	private CustomBuffer AudioBuffer ;
	private int value;
	private int value1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_screen);
		BridgeService.setPlayListener(this);
		userid = TestActivity.userid;
		customAudioRecorder = new CustomAudioRecorder(this);
		AudioBuffer = new CustomBuffer();
		audioPlayer = new AudioPlayer(AudioBuffer);
		initView();
		new LoadTask().execute();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DeviceSDK.stopPlayAudio(userid);
		AudioBuffer.ClearAll();
		audioPlayer.AudioPlayStop();

		DeviceSDK.stopPlayStream(userid);
		customAudioRecorder.releaseRecord();
	}
	private void initView()
	{
		glSurfaceView = (GLSurfaceView)findViewById(R.id.glsurfaceview);
		myRender = new MyRender(glSurfaceView);
		myRender.setListener(this);
		glSurfaceView.setRenderer(myRender);
		progressLayout = (LinearLayout)findViewById(R.id.progressLayout);

		audioItem = (Button)findViewById(R.id.audio_item);
		recordItem = (Button)findViewById(R.id.record_item);
		Button up = (Button)findViewById(R.id.up);
		Button down = (Button)findViewById(R.id.down);
		Button left = (Button)findViewById(R.id.left);
		Button right = (Button)findViewById(R.id.right);

		up_down_item = (Button)findViewById(R.id.up_down_item);
		left_right_item = (Button)findViewById(R.id.left_right_item);
		up_down_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(value == 3)
				{
					value =1;
				}
				else
				{
					value =3;
				}
				JSONObject obj = new JSONObject();
				try
				{
					obj.put("param", 5);
					obj.put("value", value);
					NativeCaller.SetParam(userid, 0x2026, obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		left_right_item.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(value1 == 2)
				{
					value1 =0;
				}
				else
				{
					value1 =2;
				}
				JSONObject obj = new JSONObject();
				try
				{
					obj.put("param", 5);
					obj.put("value", value1);
					NativeCaller.SetParam(userid, 0x2026, obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});

		settingsBtn = (Button)findViewById(R.id.button1);
		settingsBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(DevicePlayActivity.this,DeviceSettingsActivity.class);
				startActivity(intent);
			}
		});

		up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DeviceSDK.ptzControl(userid, 0);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				DeviceSDK.ptzControl(userid, 1);
			}
		});
		down.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DeviceSDK.ptzControl(userid, 2);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				DeviceSDK.ptzControl(userid, 3);
			}
		});
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DeviceSDK.ptzControl(userid, 4);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				DeviceSDK.ptzControl(userid, 5);
			}
		});
		right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				DeviceSDK.ptzControl(userid, 6);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				DeviceSDK.ptzControl(userid, 7);
			}
		});


		audioItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openAudio();
			}
		});
		recordItem.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN)
				{
					DeviceSDK.stopPlayAudio(userid);
					AudioBuffer.ClearAll();
					audioPlayer.AudioPlayStop();

					DeviceSDK.startTalk(userid);
					customAudioRecorder.StartRecord();

					DeviceSDK.stopTalk(userid);
					customAudioRecorder.StopRecord();

					DeviceSDK.startTalk(userid);
					customAudioRecorder.StartRecord();
					showToast("¶Ô½²ÖÐ...");
				}
				else if (event.getAction() == MotionEvent.ACTION_UP)
				{

					DeviceSDK.stopTalk(userid);
					customAudioRecorder.StopRecord();
					if(!isAudio)
					{
						AudioBuffer.ClearAll();
						audioPlayer.AudioPlayStart();
						DeviceSDK.startPlayAudio(userid,1);
					}
				}
				return false;
			}
		});

	}

	/**
	 * ¿ªÆô¼àÌý
	 */
	private void openAudio()
	{
		if(isAudio)
		{
			isAudio = false;
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStart();
			DeviceSDK.startPlayAudio(userid,1);
			audioItem.setText("¹Ø±Õ¼àÌý");
		}
		else
		{
			isAudio = true;
			DeviceSDK.stopPlayAudio(userid);
			AudioBuffer.ClearAll();
			audioPlayer.AudioPlayStop();
			audioItem.setText("¿ªÆô¼àÌý");
		}
	}
	private Handler frushHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressLayout.setVisibility(View.GONE);
		}
	};
	@Override
	public void cameraGetParamsResult(long userid, String cameraParams) {
		// TODO Auto-generated method stub

	}
	@Override
	public void callBackAudioData(long userID, byte[] pcm, int size) {
		if(userID == userid)
		{
			CustomBufferHead head = new CustomBufferHead();
			CustomBufferData data = new CustomBufferData();
			head.length = size;
			head.startcode = 0xff00ff;
			data.head = head;
			data.data = pcm;
			if(audioPlayer.isAudioPlaying())
				AudioBuffer.addData(data);
		}

	}
	@Override
	public void callBackVideoData(long userID, byte[] data, int type, int size) {
		// TODO Auto-generated method stub

	}
	@Override
	public void smartAlarmCodeGetParamsResult(long userid, String params) {
		// TODO Auto-generated method stub

	}
	@Override
	public void smartAlarmNotify(long userid, String message) {
		// TODO Auto-generated method stub

	}

	private class LoadTask extends AsyncTask<Void, Void, Void>
	{

		@Override
		protected Void doInBackground(Void... arg0) {
			DeviceSDK.setRender(userid, myRender);
			DeviceSDK.startPlayStream(userid, 10, 1);
			try
			{
				JSONObject obj = new JSONObject();
				obj.put("param", 13);
				obj.put("value", 1024);
				DeviceSDK.setDeviceParam(userid, 0x2026, obj.toString());

				JSONObject obj1 = new JSONObject();
				obj1.put("param", 6);
				obj1.put("value", 15);
				DeviceSDK.setDeviceParam(userid, 0x2026, obj1.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

	@Override
	public void initComplete(int size, int width, int height) {
		// TODO Auto-generated method stub
		frushHandler.sendEmptyMessage(0);
	}

	@Override
	public void takePicture(byte[] imageBuffer, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void AudioRecordData(byte[] data, int len) {
		// TODO Auto-generated method stub
		DeviceSDK.SendTalkData(userid, data, len);
	}
}
