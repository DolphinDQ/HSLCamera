package mrtech.smarthome.router;

import android.os.AsyncTask;
import android.util.Log;

import com.stream.NewAllStreamParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.net.ssl.SSLSocket;

import mrtech.smarthome.app.SmartHomeApp;
import mrtech.smarthome.interf.ResponseThreadListener;
import mrtech.smarthome.router.Router.RouterContext;
import mrtech.smarthome.rpc.Messages;
import mrtech.smarthome.rpc.Messages.Request;
import mrtech.smarthome.rpc.Messages.Response;
import mrtech.smarthome.util.Constants;
import mrtech.smarthome.util.NetUtil;
import mrtech.smarthome.util.NumberUtil;
import mrtech.smarthome.util.RequestUtil;

/**
 * router connection manager
 * Created by sphynx on 2015/12/1.
 */
public class RouterManager {

    private RouterStatusListener routerStatusListener;

    private static void trace(String msg) {
        Log.e(RouterManager.class.getName(), msg);
    }

    private static RouterManager ourInstance = new RouterManager();

    public static RouterManager getInstance() {
        return ourInstance;
    }

    private int mP2PHandle;
    private ArrayList<Router> mRouters;

    private RouterManager() {
        mRouters = new ArrayList<>();
    }

    private boolean isP2PInitialized() {
        return mP2PHandle > 0;
    }


    public void init() {
        if (isP2PInitialized() && isP2PInitialized()) return;
        final String sevc = Constants.server;
        final int port = Constants.port;
        final String user = "testsdk";
        final String password = "testsdk";
        mP2PHandle = NewAllStreamParser.DNPCreatePortServer(sevc, port, user, password);
//        NewAllStreamParser.DNPCheckSrvConnState(mP2PHandle);

        trace("inited....p2p handle :" + mP2PHandle);
    }

    public void destroy() {
        removeAll();
        if (isP2PInitialized()) {
            NewAllStreamParser.DNPDestroyPortServer(mP2PHandle);
        }
    }

    public void addRouter(Router router) {
        if (getRouter(router.getSN()) != null) return;
        final InnerRouter innerRouter = new InnerRouter(router, mP2PHandle);
        router.setContext(innerRouter);
        setListener(innerRouter);
        innerRouter.init();

        mRouters.add(router);
//        checkRouter(router);
        trace("add router :" + router.getSN());
    }

    public void removeRouter(Router router) {
        ((InnerRouter) router.getContext()).destroy();
        mRouters.remove(router);
    }

    public Router getRouter(String sn) {
        for (Router mRouter : mRouters) {
            if (mRouter.getSN() == sn)
                return mRouter;
        }
        return null;
    }

    public Router getRouter(int handle) {
        for (Router mRouter : mRouters) {
            if (mRouter.getContext().getHandle() == handle)
                return mRouter;
        }
        return null;
    }

    public Router[] getRouterList() {
        return mRouters.toArray(new Router[mRouters.size()]);
    }

    public void removeAll() {
        final Router[] routerList = getRouterList();
        for (Router router : routerList) {
            removeRouter(router);
        }
    }

    public void setRouterStatusListener(RouterStatusListener listener){
        for (Router router : getRouterList()) {
            routerStatusListener=listener;
            setListener((InnerRouter)router.getContext());
        }
    }

    private void setListener(InnerRouter roter) {
        roter.setRouterStatusListener(routerStatusListener);
    }
}
