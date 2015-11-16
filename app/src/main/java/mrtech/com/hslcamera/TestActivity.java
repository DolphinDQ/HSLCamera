/**
 *
 */
package mrtech.com.hslcamera;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import mrtech.com.hslcamera.BaseActivity;
import mrtech.com.hslcamera.BridgeService;
import mrtech.com.hslcamera.DeviceStatusListener;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.test.R;

/**
 * @author Administrator
 *
 */
public class TestActivity extends BaseActivity implements OnClickListener,DeviceStatusListener
{
	private EditText deviceIdItem;
	private TextView statusItem;
	private Button connectItem;
	private Button playItem;
	public static long userid;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_screen);
		Intent intent = new Intent(this,BridgeService.class);
		startService(intent);
		initView();

		BridgeService.setDeviceStatusListener(this);
	}

	private void initView()
	{
		deviceIdItem = (EditText)findViewById(R.id.device_id_item);
		statusItem = (TextView)findViewById(R.id.status_item);
		connectItem = (Button)findViewById(R.id.connect_btn);
		playItem = (Button)findViewById(R.id.play_btn);
		playItem.setVisibility(View.GONE);
		connectItem.setOnClickListener(this);
		playItem.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		if(arg0.getId() == R.id.connect_btn)
		{
			String did = deviceIdItem.getText().toString().trim();
			userid = DeviceSDK.createDevice("admin", "", "", 0, did, 1);
			if(userid > 0 )
			{
				int start = DeviceSDK.openDevice(userid);
			}
		}
		else if(arg0.getId() == R.id.play_btn)
		{
			Intent intent = new Intent(this,DevicePlayActivity.class);
			startActivity(intent);
		}
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what ==0)
			{
				if(msg.arg1 == 0)
				{
					statusItem.setText("连接中...");
				}
				else if(msg.arg1 == 100)
				{
					statusItem.setText("在线");
					playItem.setVisibility(View.VISIBLE);
				}
				else if(msg.arg1 ==101)
				{
					statusItem.setText("连接错误");
				}
				else if(msg.arg1 ==10)
				{
					statusItem.setText("连接超时");
				}
				else if(msg.arg1 ==9)
				{
					statusItem.setText("不在线");
				}
				else if(msg.arg1 ==5)
				{
					statusItem.setText("无效ID");
				}
				else if(msg.arg1 ==11)
				{
					statusItem.setText("断开");
				}
				else if(msg.arg1 ==1)
				{
					statusItem.setText("用户名密码错误");
				}
			}
		}

	};
	@Override
	public void receiveDeviceStatus(long userid, int status) {
		if(this.userid == userid)
		{
			Message msg = handler.obtainMessage(0, status, 0);
			handler.sendMessage(msg);
		}
	}

}
