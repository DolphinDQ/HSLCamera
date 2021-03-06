package mrtech.smarthome.router;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.stream.NewAllStreamParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.prefs.InvalidPreferencesFormatException;

import javax.net.ssl.SSLSocket;

import mrtech.smarthome.app.SmartHomeApp;
import mrtech.smarthome.interf.ResponseThreadListener;
import mrtech.smarthome.rpc.Messages;
import mrtech.smarthome.util.NetUtil;
import mrtech.smarthome.util.NumberUtil;
import mrtech.smarthome.util.RequestUtil;

/**
 * Created by sphynx on 2015/12/2.
 */
class RouterClient implements Router.RouterContext {
    private static final int ROUTER_REQUEST_TIMEOUT = 2000;
    private static final int ROUTER_KEEP_ALIVE_DELAY = 20000;
    private static final int ROUTER_READ_INTERVAL = 1000;
    private RouterStatusListener routerStatusListener;

    private static void trace(String msg) {
        Log.e(RouterClient.class.getName(), msg);
    }

    private final Router mRouter;
    private final RouterManager mManager;
    private final HashMap<Integer, Messages.Request> mSubscribeMap;
    private final ConcurrentHashMap<Integer, Messages.Response> mResponseMap;
    private final String mSN;
    private int mP2PHandle;
    private SocketListeningTask readSocketTask;
    private String p2pSN;
    private String apiKey;
    private boolean invalidSN;
    private int port;
    private SSLSocket socket;
    private boolean authenticated;
    private ResponseThreadListener mResponseListener;
    private boolean destroyed = false;


    public RouterClient(Router router, int p2pHandle) {
        mManager = RouterManager.getInstance();
        mSN = router.getSN();
        mP2PHandle = p2pHandle;
        mRouter = router;
        mSubscribeMap = new HashMap<>();
        mResponseMap = new ConcurrentHashMap<>();

    }

    private boolean decodeSN() {
        if (p2pSN == null && apiKey == null) {
            try {
                trace("decoding sn:" + mSN);
                final String code = NumberUtil.decodeQRCode(mSN);
                trace("decoded sn:" + mSN + " -> " + code);
                final String[] strings = code.split("@");
                if (strings.length == 2) {
                    apiKey = strings[0];
                    p2pSN = strings[1];
                    return true;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                invalidSN = true;
                trace("decoded sn:" + this + "failed...");
            }
        }
        return false;
    }

    public void init() {
        decodeSN();
        new Thread(new RouterConnectionTask()).start();
    }

    @Override
    public String toString() {
        return "RouterClient:" + mSN;
    }

    //====================================================================================
    private void disconnect() {
        if (socket != null) {
            if (!socket.isClosed()) {
                try {
                    readSocketTask.cancel();
                    socket.close();
                    socket = null;
                    authenticated = false;
                    callback(new Runnable() {
                        @Override
                        public void run() {
                            routerStatusListener.StatusChanged(mRouter);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    trace(this + "socket close failed..");
                }
            }
            socket = null;
        }
    }

    private boolean connect() {
        disconnect();
        trace(this + " creating ssl socket.");
        try {
            SSLSocket tempSocket = NetUtil.createSocket("localhost", port);
            if (tempSocket == null || !tempSocket.getSession().isValid())
                return false;
            socket = tempSocket;
            readSocketTask = new SocketListeningTask();
            new Thread(readSocketTask).start();
            callback(new Runnable() {
                @Override
                public void run() {
                    routerStatusListener.StatusChanged(mRouter);
                }
            });
            trace(this + " created ssl socket." + isConnected());
        } catch (SocketException e) {
            trace(this + "SocketException.!!!!removePort" + e.getMessage());
            removePort();
        } catch (Exception e) {
            e.printStackTrace();
            trace(this + "create ssl socket error." + e.getMessage());
            disconnect();
        }
        return isConnected();
    }
//====================================================================================

    private boolean addPort() {
        synchronized (mManager) {
            try {
                removePort();
                trace(this + " adding port...");
                if (NewAllStreamParser.DNPCheckSrvConnState(mP2PHandle) != 2) return false;
                port = NewAllStreamParser.DNPAddPort(mP2PHandle, p2pSN);
                callback(new Runnable() {
                    @Override
                    public void run() {
                        routerStatusListener.StatusChanged(mRouter);
                    }
                });
                trace(this + " p2p port:" + port);
            } catch (Exception ex) {
                ex.printStackTrace();
                port = 0;
                trace(this + "add port failed..");
            }
            return port != 0;
        }
    }


    private void removePort() {
        if (port == 0) return;
        disconnect();
        try {
            trace(this + " remove port...");
            NewAllStreamParser.DNPDelPort(mP2PHandle, port);
            port = 0;
            callback(new Runnable() {
                @Override
                public void run() {
                    routerStatusListener.StatusChanged(mRouter);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            trace(this + " remove port failed...");
        }
        return;
    }
//====================================================================================

    private boolean doAuth() {
        if (!isConnected()) return false;
        try {
            trace(RouterClient.this + " authentication!!!");
            Messages.Response resp = addRequestSync(RequestUtil.getAuthRequest(apiKey));
            final Messages.Response.ErrorCode code = resp.getErrorCode();
            authenticated =
                    code == Messages.Response.ErrorCode.SUCCESS ||
                            code == Messages.Response.ErrorCode.ALREADY_AUTHENTICATED;
            callback(new Runnable() {
                @Override
                public void run() {
                    routerStatusListener.StatusChanged(mRouter);
                }
            });
            trace(RouterClient.this + " authentication result :" + code);
        } catch (TimeoutException e) {
            e.printStackTrace();
            trace("auth time out");
        }
        return authenticated;
    }

    private void keepAlive() {
        if (!isAuthenticated()) return;
        trace(RouterClient.this + " keep alive..");
        try {
            addRequestSync(RequestUtil.getKeepAliveRequest());
        } catch (TimeoutException e) {
//            e.printStackTrace();
            trace("keep alive failed!!");
            disconnect();
        }
    }

    public void destroy() {
        if (destroyed) return;
        destroyed = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                removePort();
            }
        }).start();
    }

    @Override
    public void addRequest(final Messages.Request request) {
        if (request != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (request != null) {
                        try {
                            postRequest(socket, request);
                        } catch (IOException e) {
                            e.printStackTrace();
                            trace(RouterClient.this + " socket IO exception ,socket will be reset..");
                            disconnect();
                            if (mResponseListener != null) {
                                mResponseListener.onRequestFailure(e.getMessage(), e);
                            }
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    public Messages.Response addRequestSync(Messages.Request request, final int timeout) throws TimeoutException {
        if (request == null && isConnected()) return null;
        mSubscribeMap.put(request.getRequestId(), request);
        addRequest(request);
        try {
            int requestId = request.getRequestId();
            int delay = 100;
            int retryTimes = timeout / delay;
            do {
                if (mResponseMap.containsKey(requestId)) {
                    return mResponseMap.remove(requestId);
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (retryTimes-- > 0);
            throw new TimeoutException(this + "request " + requestId + " timeout...");
        } finally {
            mSubscribeMap.remove(request.getRequestId());
        }
    }

    @Override
    public Messages.Response addRequestSync(Messages.Request request) throws TimeoutException {
        return addRequestSync(request, ROUTER_REQUEST_TIMEOUT);
    }

    @Override
    public void setResponseListener(ResponseThreadListener listener) {
        mResponseListener = listener;
    }

    @Override
    public boolean isConnected() {
        return socket != null && socket.getSession().isValid();
    }

    @Override
    public boolean isSNValid() {
        return !invalidSN;
    }

    @Override
    public boolean isPortValid() {
        return port != 0;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    private void postRequest(SSLSocket sslSocket, Messages.Request request) throws IOException {
        OutputStream os = null;
        if (isConnected()) {
            try {
                os = sslSocket.getOutputStream();
                int requestLength = request.getSerializedSize();
                byte heightLevelBit = (byte) ((requestLength & 0xff00) >> 8);
                byte lowLevelBit = (byte) (requestLength & 0x00ff);
                os.write(heightLevelBit);
                os.write(lowLevelBit);
                request.writeTo(os);
            } catch (IOException e) {
                //TODO 发送一个错误广播，通知界面取消loading状态。
                e.printStackTrace();
                throw e;
            } finally {
                try {
                    os.close();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } else {
            //TODO 发送一个错误广播，通知界面取消loading状态。
//            throw new IllegalArgumentException("socket is null");
            trace("socket is null");
        }
    }

    private void callback(Runnable runnable) {
        try {
            if (routerStatusListener != null)
                runnable.run();
        } catch (NullPointerException e) {
        }
    }

    private Messages.Callback pullCallback(final SSLSocket sslSocket) throws IOException, InvalidPreferencesFormatException {
        InputStream in = null;
        in = sslSocket.getInputStream();
        byte[] prefix = new byte[2];
        int received = 0;
        in.read(prefix);
        if (prefix[0] == 0 && prefix[1] == 0)
            throw new InvalidPreferencesFormatException("invalid package header..");
        else {
            int length = ((prefix[0] & 0xff) << 8) + (prefix[1] & 0xff);
            received = 0;
            byte[] buffer = new byte[length];
            while (received < length) {
                received += in.read(buffer, received, length - received);
            }
            return Messages.Callback.parseFrom(buffer, SmartHomeApp.registry);
        }
    }

    private void checkRouterStatus() {
        try {
            if (!isSNValid()) return;
            if (!isPortValid()) {
                if (!addPort()) return;
            }
            if (!isConnected()) {
                if (!connect()) return;
            }
            if (!isAuthenticated()) {
                doAuth();
                return;
            }
            keepAlive();
        } catch (Exception ex) {
            ex.printStackTrace();
            trace(this + " check status failed.." + ex.getMessage());
        }
    }

    public void setRouterStatusListener(RouterStatusListener listener) {
        routerStatusListener = listener;
    }

    private class RouterConnectionTask implements Runnable {
        @Override
        public void run() {
            do {
                try {
                    checkRouterStatus();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        Thread.sleep(ROUTER_KEEP_ALIVE_DELAY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while (!destroyed);
        }
    }

    private class SocketListeningTask implements Runnable {
        private boolean cancel;

        public boolean isCancelled() {
            return cancel;
        }

        public void cancel() {
            cancel = true;
        }

        @Override
        public void run() {
            final SSLSocket sslSocket = socket;

            while (!isCancelled()) {
                if (isConnected()) {
                    Messages.Callback callback = null;
                    try {
                        callback = pullCallback(sslSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                        trace(RouterClient.this + " read stream error");
                        if (mResponseListener != null) {
                            mResponseListener.onRequestFailure(e.getMessage(), e);
                        }
                        disconnect();
                        continue;
                    } catch (InvalidPreferencesFormatException e) {
                        continue;
                    }

                    if (callback == null) continue;
                    processCallback(callback);
                } else {
                    try {
                        trace("waiting for connective...");
                        Thread.sleep(ROUTER_READ_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void processCallback(Messages.Callback callback) {
            trace(RouterClient.this + " callback packet :" + callback);
            if (callback.getType() == Messages.Callback.CallbackType.RESPONSE) {
                final Messages.Response response = callback.getExtension(Messages.Response.callback);
                if (response == null) {
                    trace(RouterClient.this + " unknown response data..");
                    return;
                }
                final Messages.Response.ErrorCode errorCode = response.getErrorCode();
//                trace(RouterClient.this + " received package requestId:" + response.getRequestId() + " code " + response.getErrorCode());
                if (mSubscribeMap.containsKey(response.getRequestId())) {
                    mResponseMap.put(response.getRequestId(), response);
                }
                try {
                    if (mResponseListener != null) {
                        if (errorCode == Messages.Response.ErrorCode.SUCCESS) {
                            mResponseListener.onRequestSuccess(callback);
                        } else {
                            mResponseListener.onRequestFailure(SmartHomeApp.getInstance().getErrorMessage(errorCode), null);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    trace(RouterClient.this + "callback failed");
                }
            }
        }
    }


}
