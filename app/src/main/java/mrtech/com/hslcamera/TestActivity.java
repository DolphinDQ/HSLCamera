package mrtech.com.hslcamera;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;


import hsl.p2pipcam.nativecaller.DeviceSDK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

//import com.test.R;

/**
 * @author Administrator
 */
public class TestActivity extends BaseActivity implements View.OnClickListener, DeviceStatusListener {
    private EditText deviceIdItem;
    private TextView statusItem;
    private Button connectItem;
    private Button playItem;
    public static long userId;
    private Button closeItem;
    private TextView connectTime;
    private Calendar startTime;
    private Handler handler = new StateHandler();
    private static final String DEFAULT_CHARSET = "utf-8";
    private ListView logList;
    private ArrayAdapter<String> stringArrayAdapter;
    private AsyncTask<Void, Void, Void> testTask;
    private Button btnCamera;
    private HSLCameraManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_screen);
//        Intent intent = new Intent(this, BridgeService.class);
//        startService(intent);
        initView();

//        BridgeService.setDeviceStatusListener(this);
        manager = HSLCameraManager.getInstance();
        manager.init();
        manager.setDeviceStatusListener(new DeviceStatusListener() {
            @Override
            public void receiveDeviceStatus(long userid, int status) {
                playItem.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshCameraNumber();
                    }
                });
            }
        });
        manager.addCamera(new HSLCamera(null, "HSL-118486-DLFHB", "admin", ""));
        manager.addCamera(new HSLCamera(null, "HSL-033860-DWUZF", "admin", ""));
        manager.addCamera(new HSLCamera(null, "HSL-125999-BVHJY", "admin", ""));
        manager.addCamera(new HSLCamera(null, "HSL-124419-UBUFY", "admin", ""));
        refreshCameraNumber();
    }

    private void refreshCameraNumber() {
        int num=0;
        HSLCamera[] cameraList = manager.getCameraList();
        for (HSLCamera hslCamera : cameraList) {
            if (hslCamera.mStatus.getStatus()==100){
                num++;
            }
        }
        btnCamera.setText("摄像头("+num+"/"+cameraList.length+")");
    }

    private void initView() {
        deviceIdItem = (EditText) findViewById(R.id.device_id_item);
        statusItem = (TextView) findViewById(R.id.status_item);
        connectItem = (Button) findViewById(R.id.connect_btn);
        playItem = (Button) findViewById(R.id.play_btn);
        closeItem = (Button) findViewById(R.id.close_btn);
        connectTime = (TextView) findViewById(R.id.connect_time);
        logList = (ListView) findViewById(R.id.log_lst);
        playItem.setVisibility(View.GONE);
        closeItem.setVisibility(View.GONE);
        connectItem.setOnClickListener(this);
        playItem.setOnClickListener(this);
        closeItem.setOnClickListener(this);
        findViewById(R.id.test_btn).setOnClickListener(this);
        findViewById(R.id.cancel_test_btn).setOnClickListener(this);
        stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, new ArrayList<String>());
        logList.setAdapter(stringArrayAdapter);
        findViewById(R.id.navigate_to).setOnClickListener(this);
        findViewById(R.id.get_data_btn).setOnClickListener(this);
        findViewById(R.id.auth_btn).setOnClickListener(this);
         btnCamera =(Button) findViewById(R.id.camara_list_btn);
        btnCamera.setOnClickListener(this);
//        findViewById(R.id.btnStart).setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        switch (id) {
            case R.id.connect_btn:
                String did = deviceIdItem.getText().toString().trim();
                if (userId == 0)
                    userId = DeviceSDK.createDevice("admin", "", "", 0, did, 1);
                trace("摄像头连接ID:" + userId);
                if (userId > 0) {
                    startTime = Calendar.getInstance();
                    int start = DeviceSDK.openDevice(userId);
                }
                break;
            case R.id.play_btn:
                Intent intent = new Intent(this, DevicePlayActivity.class);
                intent.setAction("");
                startActivity(intent);
                break;
            case R.id.close_btn:
                if (DeviceSDK.closeDevice(userId) == 1) {
                    Message msg = handler.obtainMessage(0, 11, 0);
                    handler.sendMessage(msg);
                }
                break;
            case R.id.test_btn:
                startTest(0);
                break;
            case R.id.auth_btn:
                startTest(1);
                break;
            case R.id.get_data_btn:
                startTest(2);
                break;
            case R.id.cancel_test_btn:
                cancelTest();
                break;
            case R.id.navigate_to:
                naviget(ListTestActivity.class);
                break;
            case R.id.camara_list_btn:
                naviget(CameraListActivity.class);
                break;
        }
    }

    private void naviget(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private static JSONObject mAuthResult;

    private void startTest(final int type) {
//        new AuthTask().execute();
        stringArrayAdapter.clear();
        testTask = new AsyncTask<Void, Void, Void>() {
            private Calendar start;

            @Override
            protected Void doInBackground(Void... params) {

                try {
                    switch (type) {
                        default:
                            JSONObject authResult = doAuth();
                            if (authResult != null && authResult.getBoolean("success")) {
                                String token = authResult.getString("access_token");
                                String tokenType = authResult.getString("token_type");
                                doGetData(token, tokenType);
//                        doGetPaging(token, tokenType);
                            }
                            break;
                        case 1:
                            mAuthResult = doAuth();
                            if (mAuthResult != null && mAuthResult.getBoolean("success")) {
                                trace("登录成功");
                            } else {
                                trace("登录失败");
                            }
                            break;
                        case 2:
                            if (mAuthResult != null && mAuthResult.getBoolean("success")) {
                                String token = mAuthResult.getString("access_token");
                                String tokenType = mAuthResult.getString("token_type");
                                doGetData(token, tokenType);
                            } else {
                                trace("未验证");
                            }
                            break;
                    }


                } catch (Exception e) {
                    trace(e.getMessage());
                }
                return null;
            }

            private JSONObject doAuth() {
                HttpClient client = new HttpClient();
                client.getHostConfiguration().setHost("connect.hzmr-tech.com", 443, "https");
                PostMethod post = new PostMethod("/connect/token");
                post.addParameter("grant_type", "password");
                post.addParameter("username", "admin");
                post.addParameter("password", "admin");
                post.addParameter("scope", "api");
                post.addParameter("client_id", "route_app");
                post.addParameter("client_secret", "74C84CDC-24A2-42BE-AF7C-A2D6C3855251");
                return jsonResponse(client, post);
            }

            private JSONObject doGetData(String token, String tokentype) {
                HttpClient client = new HttpClient();
                client.getHostConfiguration().setHost("user.hzmr-tech.com", 443, "https");
                GetMethod get = new GetMethod("/api/routeruser/GetSelfDetail");
                get.addRequestHeader("Authorization", tokentype + " " + token);
                return jsonResponse(client, get);
            }

            private JSONObject doGetPaging(String token, String tokentype) {
                HttpClient client = new HttpClient();
                client.getHostConfiguration().setHost("user.hzmr-tech.com", 443, "https");
                PostMethod get = new PostMethod("/api/RouterConfiguration/QueryPaging");
                get.setRequestBody("{\"Page\":1,\"Size\":100,\"Condition\":{\"RouterUserId\":1}}");
                get.addRequestHeader("Authorization", tokentype + " " + token);
                get.setRequestHeader("Content-Type", "application/json");
                return jsonResponse(client, get);
            }

            private JSONObject jsonResponse(HttpClient client, HttpMethod method) {
                try {
                    start = Calendar.getInstance();
                    int code = client.executeMethod(method);
                    trace("请求结果 : " + (code == 200 ? "成功" : "失败") + " " + code);
                    String json = method.getResponseBodyAsString();
                    JSONObject resp = new JSONObject(json);
                    // trace(json);
                    resp.put("success", code == 200);
                    trace("请求处理时间 :" + (Calendar.getInstance().getTimeInMillis() - start.getTimeInMillis()) + "ms");
                    return resp;
                } catch (Exception e) {
                    trace(e.getMessage());
                }
                return null;
            }
        };
        testTask.execute();
    }

//    private  static  class StringUtils{
//        public static boolean isEmpty(String str){
//            return  str==null||str=="";
//        }
//    }
//    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
//        String charset = getResponseCharset(conn.getContentType());
//        InputStream es = conn.getErrorStream();
//        if (es == null) {
//            return getStreamAsString(conn.getInputStream(), charset);
//        } else {
//            String msg = getStreamAsString(es, charset);
//            if (StringUtils.isEmpty(msg)) {
//                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
//            } else {
//                throw new IOException(msg);
//            }
//        }
//    }
//
//    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
//            StringWriter writer = new StringWriter();
//
//            char[] chars = new char[256];
//            int count = 0;
//            while ((count = reader.read(chars)) > 0) {
//                writer.write(chars, 0, count);
//            }
//
//            return writer.toString();
//        } finally {
//            if (stream != null) {
//                stream.close();
//            }
//        }
//    }
//
//    private static String getResponseCharset(String ctype) {
//        String charset = DEFAULT_CHARSET;
//
//        if (!StringUtils.isEmpty(ctype)) {
//            String[] params = ctype.split(";");
//            for (String param : params) {
//                param = param.trim();
//                if (param.startsWith("charset")) {
//                    String[] pair = param.split("=", 2);
//                    if (pair.length == 2) {
//                        if (!StringUtils.isEmpty(pair[1])) {
//                            charset = pair[1].trim();
//                        }
//                    }
//                    break;
//                }
//            }
//        }
//
//        return charset;
//    }
//

    private void trace(final String str) {
        Log.d("trace", str);
        logList.post(new Runnable() {
            @Override
            public void run() {
                stringArrayAdapter.add(str);
            }
        });
    }

    private void cancelTest() {
        testTask.cancel(true);
    }

    private class StateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (msg.arg1 == 0) {
                    statusItem.setText("连接中...");
                } else if (msg.arg1 == 100) {
                    statusItem.setText("在线");
                    playItem.setVisibility(View.VISIBLE);
                    closeItem.setVisibility(View.VISIBLE);
                    if (startTime != null) {
                        connectTime.setText("连接耗时:" + (Calendar.getInstance().getTimeInMillis() - startTime.getTimeInMillis()) + "ms");
                        startTime = null;
                    }
                    return;
                } else if (msg.arg1 == 101) {
                    statusItem.setText("连接错误");
                } else if (msg.arg1 == 10) {
                    statusItem.setText("连接超时");
                } else if (msg.arg1 == 9) {
                    statusItem.setText("不在线");
                } else if (msg.arg1 == 5) {
                    statusItem.setText("无效ID");
                } else if (msg.arg1 == 11) {
                    statusItem.setText("断开");
                } else if (msg.arg1 == 1) {
                    statusItem.setText("用户名密码错误");
                }
                playItem.setVisibility(View.GONE);
                closeItem.setVisibility(View.GONE);
                connectTime.setText("");
                return;
            }
        }

    }

    @Override
    public void receiveDeviceStatus(long userid, int status) {
        if (this.userId == userid) {
            Message msg = handler.obtainMessage(0, status, 0);
            handler.sendMessage(msg);
        }
    }

//    private class AuthTask extends AsyncTask<Void,Void,Void>{
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//
//                // Tell the URLConnection to use a SocketFactory from our SSLContext
//                URL url = new URL("https://connect.hzmr-tech.com/connect/token");
//                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
////            urlConnection.setSSLSocketFactory(context.getSocketFactory());
//                urlConnection.setHostnameVerifier(new HostnameVerifier() {
//                    @Override
//                    public boolean verify(String hostname, SSLSession session) {
//                        return true;
//                    }
//                });
//                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//                urlConnection.setDoOutput(true);
//                urlConnection.setDoInput(true);
//                OutputStream outputStream = urlConnection.getOutputStream();
//                String content = "grant_type=password&username=admin&password=admin&scope=api&client_id=route_app&client_secret=74C84CDC-24A2-42BE-AF7C-A2D6C3855251";
//                outputStream.write(content.getBytes());
//                String resp = getResponseAsString(urlConnection);
//                Log.d("https_debug",resp);
//            } catch (Exception e) {
//                Log.d("https_debug","error",e);
//            }
//
////        copyInputStreamToOutputStream(in, System.out);
//
//
//            return null;
//        }
//    }

}
