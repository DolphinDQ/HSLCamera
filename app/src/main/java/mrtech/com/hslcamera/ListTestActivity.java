package mrtech.com.hslcamera;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListTestActivity extends AppCompatActivity {

    private ArrayAdapter<String> testListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_test);
        initView();
        initData();
    }

    private void initData() {
        testListAdapter.add("列表测试");
        testListAdapter.add("列表测试");
        testListAdapter.add("列表测试");
        testListAdapter.add("列表测试");
        testListAdapter.add("一个勤劳漂亮的老婆");
        testListAdapter.add("180平米的房子");
        testListAdapter.add("一辆宝马");
        testListAdapter.add("一个强壮不生病的身体");
        testListAdapter.add("一个喜欢的事业");
    }

    private void initView() {

        ListView testList =(ListView)findViewById(R.id.test_lst);
        testListAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,new ArrayList<String>());
        testList.setAdapter(testListAdapter);
    }
}
