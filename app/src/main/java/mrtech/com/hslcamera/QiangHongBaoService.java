package mrtech.com.hslcamera;

import android.accessibilityservice.AccessibilityService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;

public class QiangHongBaoService extends AccessibilityService {

    private void trace(String message) {
        Log.d(this.getClass().getName(), message);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //接收事件,如触发了通知栏变化、界面变化等
        trace("接收事件,如触发了通知栏变化、界面变化等");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        //接收按键事件
        trace("接收按键事件");
        return super.onKeyEvent(event);

    }

    @Override
    public void onInterrupt() {
        //服务中断，如授权关闭或者将服务杀死
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //连接服务后,一般是在授权成功后会接收到
        trace("连接服务后,一般是在授权成功后会接收到");

    }
}
