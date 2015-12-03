package mrtech.com.hslcamera;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.TimeoutException;

import mrtech.smarthome.router.Router;
import mrtech.smarthome.router.RouterManager;
import mrtech.smarthome.router.RouterStatusListener;
import mrtech.smarthome.rpc.Messages;
import mrtech.smarthome.util.RequestUtil;

public class RouterListActivity extends BaseActivity {

    private RouterManager mRouterManager;
    private TextView status;
    private int index;
    private TextView routerView;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_router_list);
        mRouterManager = RouterManager.getInstance();

        initView();
    }

    private void initView() {
        btnRefresh = (Button) findViewById(R.id.refresh_btn);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedRouter(0);
            }
        });
        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedRouter(1);
            }
        });
        findViewById(R.id.prov_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changedRouter(-1);
            }
        });

        findViewById(R.id.reset_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
        status = (TextView) findViewById(R.id.status_view);
        routerView = (TextView) findViewById(R.id.router_view);
    }

    @Override
    protected void onResume() {
        setStatus();
        mRouterManager.setRouterStatusListener(new RouterStatusListener() {
            @Override
            public void StatusChanged(Router router) {
                status.post(new Runnable() {
                    @Override
                    public void run() {
                        setStatus();
                    }
                });
            }
        });
        changedRouter(0);
        super.onResume();
    }

    private void changedRouter(final int idx) {
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                routerView.setText("");
            }
        });
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                String info = getRouterInfo(getRouter(idx));
                final String msg = info;
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        routerView.setText(msg);
                    }
                });
                return null;
            }
        }.execute();

    }

    private Router getRouter(int idx) {
        final Router[] routerList = mRouterManager.getRouterList(true);
        int max = routerList.length;
        if (max == 0) {
            showToast("nothing!");
            return null;
        }
        int i = index + idx;
        if (i < 0) i = 0;
        if (i >= max) i = max - 1;
        index = i;
        new Handler(getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                btnRefresh.setText("刷新" + (index + 1) + "/" + routerList.length);
            }
        });
        return routerList[i];
    }

    private String getRouterInfo(Router router) {
        try {
            if (router == null) return "路由器为空";
            final Messages.Response response = router.getContext().addRequestSync(RequestUtil.getSysConfig());
            final Messages.GetSystemConfigurationResponse configurationResponse = response.getExtension(Messages.GetSystemConfigurationResponse.response);
            return String.format("当前设备名称：%s \n%s", configurationResponse.getConfiguration().getDeviceName(), response.toString());
        } catch (TimeoutException e) {
            e.printStackTrace();
            return "请求超时。";
        }
    }

    private void setStatus() {
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
        status.setText(String.format("路由器[%s]/端口映射[%s]/建立连接[%s]/通讯授权[%s]", routerList.length, port, connect, authentication));
    }

    private void delete() {
        try {
            mRouterManager.removeRouter(getRouter(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        changedRouter(0);
    }

    private void reset() {
        mRouterManager.addRouter(new Router(null, "SOZGA6-ZCPYSB-IOT83P-P2MLOL-LFY81Z-57F"));
        mRouterManager.addRouter(new Router(null, "JSKE8Y-X5FLNW-M0IO1S-I4MURT-O7VO79-H7B"));
        mRouterManager.addRouter(new Router(null, "FKGN77-ALORH3-8BWIYO-640WEW-5MB7YP-18Q"));
    }

}
