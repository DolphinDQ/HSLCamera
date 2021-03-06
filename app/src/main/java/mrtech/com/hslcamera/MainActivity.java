package mrtech.com.hslcamera;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.view.View;


import mrtech.smarthome.hslcamera.DeviceStatusListener;
import mrtech.smarthome.hslcamera.HSLCamera;
import mrtech.smarthome.hslcamera.HSLCameraManager;
import mrtech.smarthome.router.Router;
import mrtech.smarthome.router.RouterManager;
import mrtech.smarthome.router.RouterStatusListener;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;


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
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private ListView logList;
    private ArrayAdapter<String> stringArrayAdapter;
    private AsyncTask<Void, Void, Void> testTask;
    private Button btnCamera;
    private HSLCameraManager manager;
    private RouterManager mRouterManager;
    private Button btnRouter;
    private RR rr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        manager = HSLCameraManager.getInstance();
        mRouterManager = RouterManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setDeviceStatusListener(new DeviceStatusListener() {
            @Override
            public void receiveDeviceStatus(long userid, int status) {
                refreshCameraNumber();
            }
        });
        mRouterManager.setRouterStatusListener(new RouterStatusListener() {
            @Override
            public void StatusChanged(Router router) {
                btnRouter.post(new Runnable() {
                    @Override
                    public void run() {
                        refreshRouterNumber();
                    }
                });
            }
        });
        refreshCameraNumber();
        refreshRouterNumber();
    }


    private void refreshRouterNumber() {
        final Router[] routerList = mRouterManager.getRouterList();
        int authentication = 0;
        int connect = 0;
        int port = 0;
        for (Router router : routerList) {
            if (router.getContext().isAuthenticated()) {
                authentication++;
            }
            if (router.getContext().isConnected()) {
                connect++;
            }
            if (router.getContext().isPortValid()) {
                port++;
            }
        }
        btnRouter.setText(String.format("路由器%s/%s/%s/%s", routerList.length, port, connect, authentication));
    }


    private void refreshCameraNumber() {
        int num = 0;
        HSLCamera[] cameraList = manager.getCameraList();
        for (HSLCamera hslCamera : cameraList) {
            if (hslCamera.mStatus.getStatus() == 100) {
                num++;
            }
        }
        btnCamera.setText("摄像头(" + num + "/" + cameraList.length + ")");
    }

    private void initView() {
        logList = (ListView) findViewById(R.id.log_lst);
        findViewById(R.id.test_btn).setOnClickListener(this);
        stringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, new ArrayList<String>());
        logList.setAdapter(stringArrayAdapter);
        findViewById(R.id.navigate_to).setOnClickListener(this);
        btnCamera = (Button) findViewById(R.id.camera_list_btn);
        btnCamera.setOnClickListener(this);
        btnRouter = (Button) findViewById(R.id.router_list_btn);
        btnRouter.setOnClickListener(this);
    }

    @Override
    public void onClick(View arg0) {
        int id = arg0.getId();
        switch (id) {
            case R.id.test_btn:
                startTest(0);
                break;
            case R.id.navigate_to:
                navigate(P2PTestActivity.class);
                break;
            case R.id.camera_list_btn:
                navigate(CameraListActivity.class);
                break;
            case R.id.router_list_btn:

                navigate(RouterListActivity.class);
                break;
        }
    }

    private class RR implements Runnable {
        private Looper looper;

        @Override
        public void run() {
            looper = Looper.myLooper();
            trace("start!!");
            Looper.prepare();
            trace("stop!!!!!!!!!!!");
        }

        public void cancel() {
            new Handler(looper).post(new Runnable() {
                @Override
                public void run() {
                    Looper.loop();
                    trace("cancel~~~");
                }
            });
        }
    }

    private void navigate(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    private static JSONObject mAuthResult;

    private void startTest(final int type) {
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

    private void trace(final String str) {
        Log.d("trace", str);
        logList.post(new Runnable() {
            @Override
            public void run() {
                stringArrayAdapter.add(str);
            }
        });
    }


}
