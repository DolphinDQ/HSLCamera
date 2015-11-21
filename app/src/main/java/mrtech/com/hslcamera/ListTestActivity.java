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
        testListAdapter.add("�б����");
        testListAdapter.add("�б����");
        testListAdapter.add("�б����");
        testListAdapter.add("�б����");
        testListAdapter.add("һ������Ư��������");
        testListAdapter.add("180ƽ�׵ķ���");
        testListAdapter.add("һ������");
        testListAdapter.add("һ��ǿ׳������������");
        testListAdapter.add("һ��ϲ������ҵ");
    }

    private void initView() {

        ListView testList =(ListView)findViewById(R.id.test_lst);
        testListAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,new ArrayList<String>());
        testList.setAdapter(testListAdapter);
    }
}
