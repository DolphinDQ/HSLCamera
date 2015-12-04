package mrtech.com.hslcamera;

import android.app.Application;

import mrtech.smarthome.app.SmartHomeApp;
import mrtech.smarthome.hslcamera.HSLCamera;
import mrtech.smarthome.hslcamera.HSLCameraManager;
import mrtech.smarthome.router.Router;
import mrtech.smarthome.router.RouterManager;

/**
 * Created by zdqa1 on 2015/11/28.
 */
public class MyApp extends Application {


    private RouterManager mRouterManager;

    @Override
    public void onCreate() {
//        getApplicationContext()
        new SmartHomeApp();// initialize any thing
        super.onCreate();
        HSLCameraManager manager = HSLCameraManager.getInstance();
        manager.init();
        manager.setCallbackLoop(getMainLooper());
        manager.addCamera(new HSLCamera(null, "HSL-118486-DLFHB", "admin", ""));
//        manager.addCamera(new HSLCamera(null, "HSL-033860-DWUZF", "admin", ""));
        manager.addCamera(new HSLCamera(null, "HSL-125999-BVHJY", "admin", ""));
//        manager.addCamera(new HSLCamera(null, "HSL-126288-CWMTF", "admin", ""));
        manager.addCamera(new HSLCamera(null, "HSL-126276-EYKNV", "admin", ""));
        manager.addCamera(new HSLCamera(null, "HSL-124419-UBUFY", "admin", ""));
        mRouterManager = RouterManager.getInstance();
//        mRouterManager.init();
//        mRouterManager.addRouter(new Router(null, "SOZGA6-ZCPYSB-IOT83P-P2MLOL-LFY81Z-57F"));
//        mRouterManager.addRouter(new Router(null, "JSKE8Y-X5FLNW-M0IO1S-I4MURT-O7VO79-H7B"));
//        mRouterManager.addRouter(new Router(null, "FKGN77-ALORH3-8BWIYO-640WEW-5MB7YP-18Q"));
    }

    @Override
    public void onTerminate() {
        HSLCameraManager.getInstance().destroy();
        mRouterManager.destroy();
        super.onTerminate();
    }
}
