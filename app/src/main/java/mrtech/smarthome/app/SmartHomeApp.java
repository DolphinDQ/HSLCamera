/**
 *
 */
package mrtech.smarthome.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mrtech.com.hslcamera.BuildConfig;
import mrtech.smarthome.rpc.Messages;
import mrtech.smarthome.rpc.Messages.Response.ErrorCode;
import mrtech.smarthome.rpc.Models;
import mrtech.smarthome.util.Constants;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import com.Player.Core.PlayerClient;
import com.google.protobuf.ExtensionRegistry;
import com.stream.NewAllStreamParser;

/**
 * @author CJ
 * @version 1.0
 * @date 2015年4月2日 下午9:49:43
 * @function
 */
public class SmartHomeApp {

    private static SmartHomeApp app;

    public static boolean isRunning = true;


    public static ExtensionRegistry registry = ExtensionRegistry.newInstance();

    private Map<ErrorCode, String> errorMessageMap;

    private int connectBeanIndex = 0;
    public long readInterval;
    public long writeInterval;


    public SmartHomeApp() {
        app = this;
        initExtensionRegistry();
        initErrorMessageMap();
    }

    /**
     * 工厂方法
     *
     * @return
     */
    public static SmartHomeApp getInstance() {
        return app;
    }

    private void initExtensionRegistry() {
        new Thread() {
            public void run() {
                Messages.registerAllExtensions(registry);
                Models.registerAllExtensions(registry);
            }
        }.start();
    }

    private void initErrorMessageMap() {
        errorMessageMap = new HashMap<ErrorCode, String>();
        if (BuildConfig.DEBUG) {
//            errorMessageMap.put(ErrorCode.UNKNOWN_PROTOCOL, "未知协议。");
//            errorMessageMap.put(ErrorCode.UNSUPPORTED_VERSION, "版本不支持。");
//            errorMessageMap.put(ErrorCode.SERVER_BUSY, "服务器忙，请稍后再试。");
//            errorMessageMap.put(ErrorCode.INVALID_API_KEY, "无效ApiKey。");
//            errorMessageMap.put(ErrorCode.INCORRECT_PASSWORD, "密码错误。");
//            errorMessageMap.put(ErrorCode.INVALID_ARGUMENT, "参数错误。");
//            errorMessageMap.put(ErrorCode.PACKET_SIZE_OVERFLOW, "数据包大小溢出。");
//            errorMessageMap.put(ErrorCode.DATABASE_ERROR, "服务器数据库错误。");
//            errorMessageMap.put(ErrorCode.ALREADY_ENROLLED, "已经申报了。");
//            errorMessageMap.put(ErrorCode.NOT_ZIGBEE_IAS_WIDGET, "设备无法注册未报警设备。");
//            errorMessageMap.put(ErrorCode.DUPLICATE_ALIAS, "别名重复。");
//            errorMessageMap.put(ErrorCode.NOT_READY_TO_ARM, "系统未能报警。");
//            errorMessageMap.put(ErrorCode.ALREADY_ARMED, "系统已经报警。");
//            errorMessageMap.put(ErrorCode.DUPLICATE_NAME, "用户名重复。");
//            errorMessageMap.put(ErrorCode.MAX_CONNECTIONS_ERROR, "超过最大连接数。");
//            errorMessageMap.put(ErrorCode.NOT_AUTHENTICATED, "没有授权。");
//            errorMessageMap.put(ErrorCode.ALREADY_AUTHENTICATED, "已经授权。");
//            errorMessageMap.put(ErrorCode.INVALID_CHARACTER, "无效字符。");
//            errorMessageMap.put(ErrorCode.INVALID_PHONE_NUMBER, "无效手机号码。");
//            errorMessageMap.put(ErrorCode.MAX_DIAL_NUMBER_EXCEEDED, "超过最大拨号数量。");
//            errorMessageMap.put(ErrorCode.MAX_SMS_RECIPIENT_EXCEEDED, "超过最大短信收件人。");
//            errorMessageMap.put(ErrorCode.TELEPHONY_SERVICE_BUSY, "电话服务忙。");
//            errorMessageMap.put(ErrorCode.SYSTEM_ARMED, "系统报警。");
//            errorMessageMap.put(ErrorCode.EZMODE_ON, "搜索zigbee设备。");
//            errorMessageMap.put(ErrorCode.PERMIT_JOIN_ON, "搜索zigbee设备。");
//            errorMessageMap.put(ErrorCode.WLAN_IN_USE, "wifi正在使用，不能关闭wifi。");
//            errorMessageMap.put(ErrorCode.NOT_AUTHORIZED, "没有授权。");
//            errorMessageMap.put(ErrorCode.BAD_CONFIG_FILE, "配置文件错误。");
//            errorMessageMap.put(ErrorCode.SIGNATURE_MISMATCH, "信号不匹配。");
//            errorMessageMap.put(ErrorCode.NO_UPDATE_AVAILABLE, "没有更新。");
//            errorMessageMap.put(ErrorCode.UPDATE_CHECK_FAILED, "检查更新错误。");
//            errorMessageMap.put(ErrorCode.CAMERA_NOT_ONLINE, "摄像头离线。");
//            errorMessageMap.put(ErrorCode.CAMERA_AUTHENTICATION_ERROR, "没有授权。登陆了不上，");
//            errorMessageMap.put(ErrorCode.CAMERA_INTERNAL_ERROR, "摄像头内部错误。");
//            errorMessageMap.put(ErrorCode.MAX_GROUPS_REACHED, "到达分组最大数量。");
//            errorMessageMap.put(ErrorCode.DUPLICATE_GROUP_NAME, "分组名重复。");
//            errorMessageMap.put(ErrorCode.GROUP_NOT_FOUND, "分组找不大。。");
//            errorMessageMap.put(ErrorCode.DEVICE_NOT_FOUND, "不存在该设备。");
//            errorMessageMap.put(ErrorCode.ON_OFF_NOT_SUPPORTED, "设备不支持开关。");
//            errorMessageMap.put(ErrorCode.CAMERA_SD_CANNOT_FORMAT, "摄像头的sd卡无法格式化。");
//            errorMessageMap.put(ErrorCode.CONFLICT_ACTION, "行动冲突。");
//            errorMessageMap.put(ErrorCode.TOO_MANY_ACTIONS, "行动过多。");
//            errorMessageMap.put(ErrorCode.DUPLICATE_SCENE_NAME, "情景名称重复。");
//            errorMessageMap.put(ErrorCode.INVALID_ACTION_PARAM, "无效的action参数。");
//            errorMessageMap.put(ErrorCode.TOO_MANY_SCENES, "情景模式过多，不能超过10个。");
//            errorMessageMap.put(ErrorCode.NO_ACTION_SPECIFIED, "情景模式没有指定对应操作。");
//            errorMessageMap.put(ErrorCode.SCENE_IN_USE, "该情景正在使用，不能删除。");
//            errorMessageMap.put(ErrorCode.DUPLICATE_PLAN_NAME, "计划任务名字重复。");
//            errorMessageMap.put(ErrorCode.TOO_MANY_PLANS, "创建太多计划了，不能超过20个。");
//            errorMessageMap.put(ErrorCode.PLAN_NOT_FOUND, "该计划不存在。");
//            errorMessageMap.put(ErrorCode.INVALID_PLAN_PARAM, "计划参数无效。");
//            errorMessageMap.put(ErrorCode.OPERATION_NOT_SUPPORTED, "操作不支持。");
//            errorMessageMap.put(ErrorCode.PASSPHRASE_LENGTH_INVALID, "密码长度超长，最多64个字符。");
//            errorMessageMap.put(ErrorCode.NO_SUCH_PORT, "无效端口。");
//            errorMessageMap.put(ErrorCode.BACKUP_RESTORE_IN_PROGRESS, "备份/恢复正在进行中。");
//            errorMessageMap.put(ErrorCode.INVALID_STREAM, "无法获取输入输出流。");
//            errorMessageMap.put(ErrorCode.TOO_MANY_CAMERA, "摄像头过多，最多4个。");
//            errorMessageMap.put(ErrorCode.NO_SUCH_SAMBA_MODE, "不支持samba协议。");
//            errorMessageMap.put(ErrorCode.ZIGBEE_WIDGET_NOT_ONLINE, "设备离线。");
//            errorMessageMap.put(ErrorCode.INTERNAL_ERROR, "服务器内部错误。");
        } else {

        }
    }

    public String getErrorMessage(ErrorCode errorCode) {
        String errorMessage = "";
        errorMessage = errorMessageMap.get(errorCode);
        if (errorMessage == null)
            errorMessage = "未知错误";
        return errorMessage;
    }


}











