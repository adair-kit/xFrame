package cn.adair.sample.ui.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.adair.sample.ui.BaseActivity;
import cn.adair.sample.R;
import cn.adair.xframe.adapter.XRecyclerViewAdapter;
import cn.adair.xframe.adapter.XViewHolder;
import cn.adair.xframe.adapter.decoration.DividerDecoration;

public class XRecyclerViewAdapterActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    private List<String> datas;
    private TestAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeLayout;
    private Handler mHandler = new Handler();

    @Override
    public int getLayoutId() {
        return R.layout.activity_xrecycler_view_adapter;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        datas = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            datas.add("第 " + i + " 个");
        }
    }

    @Override
    public void initView() {
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(R.color.main_color);

        //添加分割线
        recyclerView.addItemDecoration(new DividerDecoration(Color.parseColor("#C4C4C4"), 1));
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        adapter = new TestAdapter(recyclerView, datas);
        //添加header，footer
        final View headerView = LayoutInflater.from(this).inflate(R.layout.header, recyclerView, false);
        final View footerView = LayoutInflater.from(this).inflate(R.layout.footer, recyclerView, false);
        adapter.addHeaderView(headerView);
        adapter.addFooterView(footerView);
        //点击事件
        adapter.setOnItemClickListener(new XRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Toast.makeText(getApplication(), "position：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(XRecyclerViewAdapterActivity.this, XMultiTypeAdapterActivity.class)
                        .putExtra("title", "多布局使用+StickyHeader"));

            }
        });
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "移除footer", Toast.LENGTH_SHORT).show();
                adapter.removeFooterView(footerView);
            }
        });

        adapter.isLoadMore(true);//开启加载更多功能,默认关闭

        adapter.setOnLoadMoreListener(new XRecyclerViewAdapter.OnLoadMoreListener() {
            @Override
            public void onRetry() {//加载失败，重新加载回调方法
                load();
            }

            @Override
            public void onLoadMore() {//加载更多回调方法
                load();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void load() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //模拟几种错误情况和正确情况
                int random = new Random().nextInt(10);
                if (random < 3) {
                    adapter.showLoadError();//显示加载错误
                } else if (random >= 3 && random < 6) {
                    adapter.showLoadComplete();//没有更多数据了
                } else {
                    adapter.addAll(getDatas("新增加"));//加载更多
                }
//                adapter.addAll(getDatas("新增加"));//加载更多
            }
        }, 2000);
    }

    public List<String> getDatas(String str) {
        List<String> datas = new ArrayList<>();
        int random = new Random().nextInt(10);
        for (int i = 1; i <= random; i++) {
            datas.add(str + " 第 " + i + " 个");
        }
        Log.e("--", str + "  " + datas.size());
        return datas;
    }

    @Override
    public void onRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.setDataLists(getDatas("刷新"));
                mSwipeLayout.setRefreshing(false);
            }
        }, 2000);
    }


    //单一类型adapter
    class TestAdapter extends XRecyclerViewAdapter<String> {

        public TestAdapter(RecyclerView recyclerView, List<String> datas) {
            super(recyclerView, datas, R.layout.dome_item);
        }

        @Override
        public void bindData(XViewHolder holder, String data, int position) {
            //方法一：
            holder.setText(R.id.text, data);
            //方法二：
            //TextView textView = holder.getView(R.id.text);
            //textView.setText(data);
        }
    }
}
