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
        //�����¼�,�紥����֪ͨ���仯������仯��
        trace("�����¼�,�紥����֪ͨ���仯������仯��");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        //���հ����¼�
        trace("���հ����¼�");
        return super.onKeyEvent(event);

    }

    @Override
    public void onInterrupt() {
        //�����жϣ�����Ȩ�رջ��߽�����ɱ��
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //���ӷ����,һ��������Ȩ�ɹ������յ�
        trace("���ӷ����,һ��������Ȩ�ɹ������յ�");

    }
}
