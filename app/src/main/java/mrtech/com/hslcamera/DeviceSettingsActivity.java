package mrtech.com.hslcamera;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import hsl.p2pipcam.nativecaller.NativeCaller;
import mrtech.smarthome.hslcamera.SettingsListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import com.test.R;

public class DeviceSettingsActivity extends Activity implements SettingsListener,OnClickListener
{
	private EditText info_item;
	private Button wifi_get_item,wifi_set_item;
	private Button ftp_get_item,ftp_set_item;
	private Button user_get_item,user_set_item;
	private Button alarm_get_item,alarm_set_item;
	private Button mail_get_item,mail_set_item;
	private Button sdcard_get_item,sdcard_set_item;
	private Button ap_get_item,ap_set_item;
	private Button ddns_get_item,ddns_set_item;
	private Button alias_get_item,alias_set_item;
	private Button time_get_item,time_set_item;
	private Button format_set_item;
	private Button wifi_list_item;
	private Button record_list_get_item;
	
	private long userid;
	private String params;
	private int type = 0;
	private StringBuffer recordSb = new StringBuffer();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_screen);
		BridgeService.setSettingsListener(this);
		userid = TestActivity.userId;
		initView();
	}
	
	private void initView()
	{
		info_item = (EditText)findViewById(R.id.info_item);
		wifi_get_item = (Button)findViewById(R.id.wifi_get_item);
		wifi_set_item = (Button)findViewById(R.id.wifi_set_item);
		wifi_get_item.setOnClickListener(this);
		wifi_set_item.setOnClickListener(this);
		
		ftp_get_item = (Button)findViewById(R.id.ftp_get_item);
		ftp_set_item = (Button)findViewById(R.id.ftp_set_item);
		ftp_get_item.setOnClickListener(this);
		ftp_set_item.setOnClickListener(this);
		
		user_get_item = (Button)findViewById(R.id.user_get_item);
		user_set_item = (Button)findViewById(R.id.user_set_item);
		user_get_item.setOnClickListener(this);
		user_set_item.setOnClickListener(this);
		
		alarm_get_item = (Button)findViewById(R.id.alarm_get_item);
		alarm_set_item = (Button)findViewById(R.id.alarm_set_item);
		alarm_get_item.setOnClickListener(this);
		alarm_set_item.setOnClickListener(this);
		
		mail_get_item = (Button)findViewById(R.id.mail_get_item);
		mail_set_item = (Button)findViewById(R.id.mail_set_item);
		mail_get_item.setOnClickListener(this);
		mail_set_item.setOnClickListener(this);
		
		sdcard_get_item = (Button)findViewById(R.id.sdcard_get_item);
		sdcard_set_item = (Button)findViewById(R.id.sdcard_set_item);
		sdcard_get_item.setOnClickListener(this);
		sdcard_set_item.setOnClickListener(this);
		
		ap_get_item = (Button)findViewById(R.id.ap_get_item);
		ap_set_item = (Button)findViewById(R.id.ap_set_item);
		ap_get_item.setOnClickListener(this);
		ap_set_item.setOnClickListener(this);
		
		ddns_get_item = (Button)findViewById(R.id.ddns_get_item);
		ddns_set_item = (Button)findViewById(R.id.ddns_set_item);
		ddns_get_item.setOnClickListener(this);
		ddns_set_item.setOnClickListener(this);
		 
		alias_get_item = (Button)findViewById(R.id.alias_get_item);
		alias_set_item = (Button)findViewById(R.id.alias_set_item);
		alias_get_item.setOnClickListener(this);
		alias_set_item.setOnClickListener(this);
		
		time_get_item = (Button)findViewById(R.id.time_get_item);
		time_set_item = (Button)findViewById(R.id.time_set_item);
		time_get_item.setOnClickListener(this);
		time_set_item.setOnClickListener(this);
		
		format_set_item = (Button)findViewById(R.id.format_set_item);
		format_set_item.setOnClickListener(this);
		
		wifi_list_item = (Button)findViewById(R.id.wifi_list_get_item);
		wifi_list_item.setOnClickListener(this);
		
		record_list_get_item = (Button)findViewById(R.id.record_list_get_item);
		record_list_get_item.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) 
	{
		int id = arg0.getId();
		if(id == R.id.wifi_get_item)
		{
			type = 0x2013;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.wifi_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2012, params);	
		}
		else if(id == R.id.ftp_get_item)
		{
			type = 0x2007;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.ftp_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2006, params);	
		}
		else if(id == R.id.user_get_item)
		{
			type = 0x2003;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.user_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2002, params);
		}
		else if(id == R.id.alarm_get_item)
		{
			type = 0x2018;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.alarm_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2017, params);
		}
		else if(id == R.id.mail_get_item)
		{
			type = 0x2009;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.mail_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2008, params);
		}
		else if(id == R.id.sdcard_get_item)
		{
			type = 0x2021;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.sdcard_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2022, params);
		}
		else if(id == R.id.ap_get_item)
		{
			type = 0x2703;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.ap_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			NativeCaller.SetParam(userid, 0x2704, params);
		}
		else if(id == R.id.ddns_get_item)
		{
			type = 0x2005;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.ddns_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			try 
			{
				JSONObject ob = new JSONObject(params);
				JSONObject obj = new JSONObject();
				obj.put("service", ob.get("service"));
				obj.put("user", ob.get("user"));
				obj.put("pwd", ob.get("pwd"));
				obj.put("host", ob.get("host"));
				obj.put("proxy_svr", ob.get("proxy_svr"));
				obj.put("proxy_port", ob.get("proxy_port"));
				NativeCaller.SetParam(userid, 0x2004, obj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if(id == R.id.alias_get_item)
		{
			type = 0x2701;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.alias_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			try 
			{
				JSONObject ob = new JSONObject(params);
				JSONObject obj = new JSONObject();
				obj.put("alias", ob.get("alias"));
				NativeCaller.SetParam(userid, 0x2702, obj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if(id == R.id.time_get_item)
		{
			type = 0x2016;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.time_set_item)
		{
			if(TextUtils.isEmpty(params))
				return;
			try 
			{
				JSONObject ob = new JSONObject(params);
				JSONObject obj = new JSONObject();
				obj.put("now", 0);
				obj.put("timezone", ob.get("timezone"));
				obj.put("ntp_enable", ob.get("ntp_enable"));
				obj.put("ntp_svr", ob.get("ntp_svr"));
				NativeCaller.SetParam(userid, 0x2015, obj.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		else if(id == R.id.format_set_item)
		{
			NativeCaller.SetParam(userid, 0x2024, "");
		}
		else if(id == R.id.wifi_list_get_item)
		{
			type = 0x2014;
			NativeCaller.GetParam(userid, type);
		}
		else if(id == R.id.record_list_get_item)
		{
			Calendar eCalendar = Calendar.getInstance();
			int eYear = eCalendar.get(Calendar.YEAR);
			int eMon = eCalendar.get(Calendar.MONTH)+1;
			int eDay = eCalendar.get(Calendar.DAY_OF_MONTH);
			NativeCaller.SearchRecordFile(userid, eYear, eMon,eDay, 0, 0, 0, eYear, eMon, eDay, 23, 59, 59);
		}
	}
	
	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what ==0)
			{
				params = (String) msg.obj;
				info_item.setText(params);
			}
			else if(msg.what ==1)
			{
				if(msg.arg1 ==1)
					Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(getBaseContext(), "faild", Toast.LENGTH_SHORT).show();
			}
			else if(msg.what ==2)
			{
				info_item.setText(recordSb.toString());
			}
		}
	};
	
	@Override
	public void callBack_getParam(long UserID, long nType, String param)
	{
		if(UserID == userid)
		{
			if(nType == type)
			{
				Message msg = handler.obtainMessage(0, 0, 0, param);
				handler.sendMessage(msg);
			}
			/*else if(nType == 0x2007)
			{
				Message msg = handler.obtainMessage(0, 0, 0, param);
				handler.sendMessage(msg);
			}
			else if(nType == 0x2003)
			{
				Message msg = handler.obtainMessage(0, 0, 0, param);
				handler.sendMessage(msg);
			}*/
			/*Message msg = handler.obtainMessage(0, 0, 0, param);
			handler.sendMessage(msg);*/
		}
	}

	@Override
	public void callBack_setParam(long UserID, long nType, int nResult) {
		if(UserID == userid)
		{
			Message msg = handler.obtainMessage(1, nResult, 0);
			handler.sendMessage(msg);
		}
	}

	@Override
	public void recordFileList(long UserID, int filecount, String fname,String strDate, int size) {
		recordSb.append(fname).append("/r/n");
		handler.sendEmptyMessage(2);
	}
}
