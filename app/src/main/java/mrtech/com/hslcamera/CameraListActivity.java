package mrtech.com.hslcamera;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import mrtech.smarthome.hslcamera.DeviceStatusListener;
import mrtech.smarthome.hslcamera.HSLCamera;
import mrtech.smarthome.hslcamera.HSLController;
import mrtech.smarthome.hslcamera.HSLCameraManager;
import mrtech.smarthome.hslcamera.HSLPlayer;

public class CameraListActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private HSLCameraManager camManager;
    private int index;
    private HSLCamera[] cameraList;
    private HSLPlayer cameraPlayer;
    private TextView viewCamera;

    private static void trace(String msg) {
        Log.e(CameraListActivity.class.getName(), msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_list);
        camManager = HSLCameraManager.getInstance();
        camManager.init();
        cameraList = camManager.getCameraList();

        camManager.setDeviceStatusListener(new DeviceStatusListener() {
            @Override
            public void receiveDeviceStatus(long userid, int status) {

            }
        });

        initView();
    }

    private void initView() {
        ListeningClick(R.id.prov_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(-1);
            }
        });
        ListeningClick(R.id.next_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(1);
            }
        });
        ListeningClick(R.id.replay_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(0);
            }
        });
        ListeningClick(R.id.delete_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCamera();
            }
        });
        ListeningClick(R.id.add_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCamera();
            }
        });
        ListeningClick(R.id.close_btn, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camManager.removeCamera(cameraPlayer.getPlaying());
            }
        });
        viewCamera = (TextView) findViewById(R.id.cam_num_view);
        glSurfaceView = (GLSurfaceView) findViewById(R.id.view_gls);
        final GestureDetector gestureDetector = new GestureDetector(this, new PtzGestureListener());
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        cameraPlayer = camManager.createCameraPlayer(glSurfaceView);
        ListeningClick(R.id.audio_switch, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPlayer.setAudio(((Switch) v).isChecked());
            }
        });
        findViewById(R.id.start_talk_btn).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int act = event.getAction();
                if (act == MotionEvent.ACTION_DOWN) {
                    cameraPlayer.setTalk(true);
                }
                if (act == MotionEvent.ACTION_UP) {
                    cameraPlayer.setTalk(false);
                }
                return false;
            }
        });
    }

    private void addCamera() {
        camManager.addCamera(new HSLCamera(null, "HSL-118486-DLFHB", "admin", ""));
        cameraList=camManager.getCameraList();
        play(0);
    }

    private void deleteCamera() {
        camManager.removeCamera("HSL-118486-DLFHB");
        cameraList=camManager.getCameraList();
        play(0);
    }

    private class PtzGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            HSLController controller = camManager.createController(cameraPlayer.getPlaying());
            float vx = Math.abs(velocityX);
            float vy = Math.abs(velocityY);
            if (vx > vy) {
                float forward = e2.getRawX() - e1.getRawX();
                if (Math.abs(forward) < 100) return false;
                // x
                if (forward > 0) {
                    controller.ptzLeft();
                    //right
                } else {
                    //left
                    controller.ptzRight();
                }
            } else {
                float forward = e2.getRawY() - e1.getRawY();
                if (Math.abs(forward) < 100) return false;
                // y
                if (forward > 0) {
                    controller.ptzDown();
                    //down
                } else {
                    //up
                    controller.ptzUp();
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void ListeningClick(int id, View.OnClickListener listener) {
        findViewById(id).setOnClickListener(listener);
    }

    private void play(int idx) {
        HSLCamera[] playingList = cameraPlayer.getPlayingList();
        int max = playingList.length;
        if (max == 0) {
            trace("play nothing!");
            viewCamera.setText("nothing!");
            return;
        }
        trace("play ...."+idx);
        int i = index + idx;
        if (i < 0) i = 0;
        if (i >= max) i = max - 1;
        index = i;
        viewCamera.setText("playing :" + (i + 1) + "/" + max + "/" + cameraList.length);
        if (playingList[i].equals(cameraPlayer.getPlaying())) return;
        cameraPlayer.play(playingList[i]);
        ((Switch)findViewById(R.id.audio_switch)).setChecked(false);
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
