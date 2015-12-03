package mrtech.smarthome.router;

import android.util.Log;

import com.stream.NewAllStreamParser;

import java.util.ArrayList;

import mrtech.smarthome.util.Constants;

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
        return mP2PHandle != 0;
    }


    public void init() {
        if (isP2PInitialized()) return;
        final String sevc = Constants.server;
        final int port = Constants.port;
        final String user = "testsdk";
        final String password = "testsdk";
        mP2PHandle = NewAllStreamParser.DNPCreatePortServer(sevc, port, user, password);
        trace("inited....p2p handle :" + mP2PHandle);
    }

    public void destroy() {
        removeAll();
        if (isP2PInitialized()) {
            NewAllStreamParser.DNPDestroyPortServer(mP2PHandle);
        }
    }

    public void addRouter(Router router) {
        if (router == null || getRouter(router.getSN()) != null) return;
        final RouterClient innerRouter = new RouterClient(router, mP2PHandle);
        router.setContext(innerRouter);
        setListener(innerRouter);
        innerRouter.init();
        mRouters.add(router);
        trace("add router :" + router.getSN());
    }

    public void removeRouter(Router router) {
        if (router == null) return;
        ((RouterClient) router.getContext()).destroy();
        mRouters.remove(router);
    }

    public Router getRouter(String sn) {
        for (Router mRouter : mRouters) {
            if (mRouter.getSN() == sn)
                return mRouter;
        }
        return null;
    }


    public Router[] getRouterList() {
        return mRouters.toArray(new Router[mRouters.size()]);
    }

    /**
     * get valid/invalid router list, ps:the valid router that is got authentication
     *
     * @param valid
     * @return
     */
    public Router[] getRouterList(boolean valid) {
        ArrayList<Router> routers = new ArrayList<>();
        for (Router mRouter : mRouters) {
            if (mRouter.getContext().isAuthenticated() == valid) {
                routers.add(mRouter);
            }
        }
        return mRouters.toArray(new Router[routers.size()]);
    }

    public void removeAll() {
        final Router[] routerList = getRouterList();
        for (Router router : routerList) {
            removeRouter(router);
        }
    }

    public void setRouterStatusListener(RouterStatusListener listener) {
        for (Router router : getRouterList()) {
            routerStatusListener = listener;
            setListener((RouterClient) router.getContext());
        }
    }

    private void setListener(RouterClient router) {
        router.setRouterStatusListener(routerStatusListener);
    }
}
