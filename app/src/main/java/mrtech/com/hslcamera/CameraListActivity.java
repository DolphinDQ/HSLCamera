package mrtech.com.hslcamera;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CameraListActivity extends AppCompatActivity implements View.OnClickListener {

    private GLSurfaceView glSurfaceView;
    private MyRender myRender;
    private final HSLCameraManager camManager;
    private int index;
    private HSLCamera[] cameraList;
    private HSLPlayer cameraPlayer;
    private TextView viewCamera;

    private static void trace(String msg) {
        Log.e(CameraListActivity.class.getName(), msg);
    }

    public CameraListActivity() {
        camManager = HSLCameraManager.getInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        camManager.init();
        cameraList = camManager.getCameraList();
        initView();
    }

    private void initView() {
        ListeningClick(R.id.prov_btn);
        ListeningClick(R.id.next_btn);
        ListeningClick(R.id.replay_btn);
        ListeningClick(R.id.close_btn);
        viewCamera = (TextView) findViewById(R.id.cam_num_view);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.view_gls);
        cameraPlayer = camManager.createCameraPlayer(glSurfaceView);
//        myRender = new MyRender(glSurfaceView);
//        myRender.setListener(this);
//        glSurfaceView.setRenderer(myRender);
    }

    private void ListeningClick(int id) {
        findViewById(id).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prov_btn:
                play(-1);
                break;
            case R.id.next_btn:
                play(1);
                break;
            case R.id.replay_btn:
                play(0);
                break;
            case R.id.close_btn:
                cameraPlayer.stop();
                break;
        }

    }

    private void play(int idx) {
        HSLCamera[] playingList = cameraPlayer.getPlayingList();
        int max = playingList.length;
        if (max == 0) {
            trace("play nothing!");
            return;
        }
        int i = index + idx;        if (i < 0) i = 0;
        if (i >= max) i = max - 1;
        index = i;
        viewCamera.setText("playing :" + (i + 1) + "/" + max);
        cameraPlayer.play(playingList[i]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        camManager.setDeviceStatusListener(new DeviceStatusListener() {
            @Override
            public void receiveDeviceStatus(long userid, int status) {
                play(0);
            }
        });
        play(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPlayer.stop();
    }
}
