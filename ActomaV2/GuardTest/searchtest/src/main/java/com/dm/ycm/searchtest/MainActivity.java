package com.dm.ycm.searchtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private ContentAdapter contentAdapter;
    private SearchView searchView;
    private ListView listView;
    private List<String> dataSource = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentAdapter = new ContentAdapter(this);
        searchView = (SearchView) findViewById(R.id.search_edit);
        searchView.setOnQueryTextListener(this);
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(contentAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            dataSource.clear();
            dataSource.add(newText);
            contentAdapter.setDataSource(dataSource);
            contentAdapter.notifyDataSetChanged();
            Log.d("ddd", contentAdapter.getDataSource().get(0));
        }
        return false;
    }
}
