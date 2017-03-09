package com.as.atlas.demofileexplorer;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by atlas on 2017/3/7.
 */

public class FileChooser extends ListActivity implements AdapterView.OnItemLongClickListener {

    private File currentDir;
    private FileArrayAdapter adapter;

    public final static String TAG = FileChooser.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.file_view);

        Log.d(TAG, Environment.getExternalStorageDirectory().getPath());
        currentDir = new File(Environment.getExternalStorageDirectory().getPath());
        fill(currentDir);

        this.getListView().setLongClickable(true);
        this.getListView().setOnItemLongClickListener(this);
    }

    private void fill(File f)
    {
        Log.d(TAG, "fill: " + f);
        File[] dirs = f.listFiles();
        this.setTitle("Current Dir: "+ f.getAbsolutePath());
        List<Item> dir = new ArrayList<Item>();
        List<Item>fls = new ArrayList<Item>();
        try{
            for(File ff: dirs)
            {
                Log.d(TAG, "Current directory:" + ff);
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(), num_item, date_modify, ff.getAbsolutePath(), "directory_icon"));
                } else {
                    Log.d(TAG, "files: " + ff);
                    fls.add(new Item(ff.getName(), ff.length() + " Byte", date_modify, ff.getAbsolutePath(), "file_icon"));
                }
            }
        } catch(Exception e) {
        }
        Collections.sort(dir);
        Collections.sort(fls);

        dir.addAll(fls);
        if(!f.getName().equalsIgnoreCase("sdcard"))
            dir.add(0,new Item("..","Parent Directory","",f.getParent(),"directory_up"));

        adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view, dir);
        this.setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        Log.d(TAG, "onListItemClick");
        super.onListItemClick(l, v, position, id);

        Item o = adapter.getItem(position);
        if (o.getImage().equalsIgnoreCase("directory_icon") || o.getImage().equalsIgnoreCase("directory_up")) {
            Log.d(TAG, "onListItemClick currentDir=" + currentDir);

            if (!currentDir.toString().equals("/") && currentDir != null) {  // Prevent crash
                File parent = new File(currentDir.getParent());
                Log.d(TAG, "parent=" + parent);
                if (parent.exists()) {
                    currentDir = new File(o.getPath());
                    fill(currentDir);
                }
            }
        }
        else {
            onFileClick(o);
        }
    }

    private void onFileClick(Item o)
    {
        Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra("GetPath",currentDir.toString());
        intent.putExtra("GetFileName",o.getName());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        Item o = adapter.getItem(position);
        Log.d(TAG, "onItemLongClick:" + o.getPath() + o.getName());

        return true;
    }
}

