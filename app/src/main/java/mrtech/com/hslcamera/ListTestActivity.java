package mrtech.com.hslcamera;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.stream.NewAllStreamParser;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.Calendar;

public class ListTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ArrayAdapter<String> testListAdapter;
    private P2PConectionTask p2pConnectionTask;
    private Handler handler;

    private static final String p2pId = "umkss83g7brx";
//    private static final String p2pId = "umkssninfcuf";
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);
        initView();
//        initData();

    }
//
//    private void initData() {
//        testListAdapter.add("列表测试");
//        testListAdapter.add("列表测试");
//        testListAdapter.add("列表测试");
//        testListAdapter.add("列表测试");
//        testListAdapter.add("一个勤劳漂亮的老婆");
//        testListAdapter.add("180平米的房子");
//        testListAdapter.add("一辆宝马");
//        testListAdapter.add("一个强壮不生病的身体");
//        testListAdapter.add("一个喜欢的事业");
//    }

    private void initView() {
        handler = new Handler(this.getMainLooper());

        ListView testList = (ListView) findViewById(R.id.test_lst);
        testListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, new ArrayList<String>());
        testList.setAdapter(testListAdapter);
         findViewById(R.id.start_task_btn).setOnClickListener(this);
        findViewById(R.id.stop_task_btn).setOnClickListener(this);
        findViewById(R.id.clear_log_btn).setOnClickListener(this);
    }

    private void trace(final String str) {
        Log.d("trace", str);
        handler.post(new Runnable() {
            @Override
            public void run() {
                testListAdapter.add(str);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.start_task_btn:
                startTask();
                break;
            case R.id.stop_task_btn:
                stopTask();
                break;
            case R.id.clear_log_btn:
                testListAdapter.clear();
                break;
        }
    }

    private void stopTask() {
        if (p2pConnectionTask != null && p2pConnectionTask.getStatus() == AsyncTask.Status.RUNNING) {
            p2pConnectionTask.cancel(false);
        }
    }

    private void startTask() {
        stopTask();
        (p2pConnectionTask = new P2PConectionTask()).execute();
    }

    class P2PConectionTask extends AsyncTask<Void, Void, Void> {
        private Calendar start;
        private Calendar last;

        private String getDuring() {
            long total = 0;
            long step = 0;
            if (start != null) {
                Calendar now = Calendar.getInstance();
                total = now.getTimeInMillis() - start.getTimeInMillis();
                if (last != null) {
                    step = now.getTimeInMillis() - last.getTimeInMillis();
                }
                last = now;
            }
            return " \n单步耗时" + step + "ms...总耗时" + total + "ms";
        }

        @Override
        protected Void doInBackground(Void... params) {
            last=  start = Calendar.getInstance();
            trace("启动任务...");
            int handle = NewAllStreamParser.DNPCreatePortServer(
                    "cloud.hzmr-tech.com", 8300, "sdktest", "sdktest");
            if (handle == 0) {
                trace("创建映射服务失败" + getDuring());
                return null;
            } else
                trace("创建映射服务成功" + getDuring());
//            int state = NewAllStreamParser.DNPCheckSrvConnState(handle);
//            int checkTimes = 0;
            try {
//                while (state != 2) { // 2 为 已连接
//                    if (checkTimes >= 30) {
//                        trace("连接超时。。" + getDuring());
//                        return null;
//                    }
//                    if (isCancelled()) {
//                        trace("任务取消。。" + getDuring());
//                        return null;
//                    }
//                    Thread.sleep(333);
//                    checkTimes++;
//                    state = NewAllStreamParser.DNPCheckSrvConnState(handle);
//                }
//                trace("连接成功" + getDuring());
                int port = NewAllStreamParser.DNPAddPort(handle, p2pId);
                if (port == 0) {
                    trace("创建端口失败" + getDuring());
                    return null;
                } else
                    trace("创建端口成功： " + port + getDuring());
                /**
                 * 通信处理
                 */
                NewAllStreamParser.DNPDelPort(handle, port);
                trace("删除端口" + getDuring());
                NewAllStreamParser.DNPDestroyPortServer(handle);
                trace("销毁映射服务" + getDuring());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                trace("任务异常" + getDuring());
            }
            trace("任务完成..." + getDuring());
            return null;
        }

        @Override
        protected void onCancelled() {
            trace("任务取消...");
            super.onCancelled();
        }
    }

}
