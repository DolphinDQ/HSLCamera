package mrtech.com.hslcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import mrtech.smarthome.router.Router;
import mrtech.smarthome.router.RouterManager;
import mrtech.smarthome.router.RouterStatusListener;

public class RouterListActivity extends AppCompatActivity {

    private RouterManager mRouterManager;
    private TextView status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_list);
        mRouterManager = RouterManager.getInstance();
        mRouterManager.setRouterStatusListener(new RouterStatusListener() {
            @Override
            public void StatusChanged(Router router) {
                status.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh();
                    }
                });
            }
        });
        initView();
    }

    private void initView() {
        findViewById(R.id.refresh_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
        status = (TextView) findViewById(R.id.status_view);

    }

    private void refresh() {
        final Router[] routerList = mRouterManager.getRouterList();
        int authentication=0;
        int connect=0;
        int port=0;
        for (Router router : routerList) {
            if (router.getContext().isAuthenticated()){
                authentication++;
            }
            if (router.getContext().getHandle()!=0){
                connect++;
            }
            if (router.getContext().getPort()!=0){
                port++;
            }
        }
        status.setText(String.format("路由器[%s]/端口映射[%s]/建立连接[%s]/通讯授权[%s]",routerList.length,port,connect,authentication));
    }
}
