package mrtech.com.hslcamera;

import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import hsl.p2pipcam.nativecaller.DeviceSDK;
import mrtech.com.hslcamera.MyRender.RenderListener;

public class MainActivity extends AppCompatActivity implements RenderListener {

    private GLSurfaceView glSurfaceView;
    private MyRender myRender;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.btnInit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSdk();
            }
        });
        findViewById(R.id.btnNetDetect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkDetect();
            }
        });
        glSurfaceView = (GLSurfaceView)findViewById(R.id.glsurfaceview);
        myRender = new MyRender(glSurfaceView);
        myRender.setListener(this);
        glSurfaceView.setRenderer(myRender);
        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LoadTask().execute();
            }
        });
    }
    private class LoadTask extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... arg0) {
            DeviceSDK.setRender(userId, myRender);
            trace("play result :"+DeviceSDK.startPlayStream(userId, 0, 1));  ;
            try
            {
                JSONObject obj = new JSONObject();
                obj.put("param", 13);
                obj.put("value", 1024);
                DeviceSDK.setDeviceParam(userId, 0x2026, obj.toString());

                JSONObject obj1 = new JSONObject();
                obj1.put("param", 6);
                obj1.put("value", 15);
                DeviceSDK.setDeviceParam(userId, 0x2026, obj1.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    private void initSdk() {
        trace("init sdk");
        try {
            trace("open sdk reuslt :" + DeviceSDK.initialize(""));
//            trace("set callback result :"+DeviceSDK.setCallback(););
            String p2p= "HSL-126288-CWMTF";
            userId=  DeviceSDK.createDevice("admin","","",81,p2p,1);
            trace("create device handler:"+userId);
            trace("start result :"+DeviceSDK.openDevice(userId));
        } catch (Exception e) {
            e.printStackTrace();
            trace("init sdk error!!");
        }
    }

    private  void networkDetect(){
        trace("check net work!!");
        DeviceSDK.networkDetect();
    }

    private void trace(String message) {
        Log.d(this.getLocalClassName(),message);
    }

    @Override
    public void initComplete(int size, int width, int height) {
        trace("rander init complete!!!");
    }

    @Override
    public void takePicture(byte[] imageBuffer, int width, int height) {
        trace("take picture!!!");
    }
}
