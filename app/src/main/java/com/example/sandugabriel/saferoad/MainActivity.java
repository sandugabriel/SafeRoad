package com.example.sandugabriel.saferoad;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";

    private final String[] mPermissionsRequired = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private final int PERMISSIONS_REQUEST_CODE = 1;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FileListAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String path = Session.getDir();
        File directory = new File(path);

        final File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.getPath().endsWith(".mp4");
            }
        });
        //for (int i = 0; i < files.length; i++) {
          //  Log.d(LOG_TAG, "FileName:" + files[i].getName());
        //}

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FileListAdapter(files, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = mRecyclerView.getChildLayoutPosition(view);
                File file = files[position];
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getPath()));
                intent.setDataAndType(Uri.parse(file.getPath()), "video/mp4");
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                openCamera();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(LOG_TAG, "onRequestPermissionsResult");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length == 0) {
                    Log.i(LOG_TAG, "grantResults empty");
                    openCamera();
                    return;
                }

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        openCamera();
                        Log.i(LOG_TAG, permissions[i] + " denied");
                        return;
                    }
                }

                Intent intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    public void openCamera() {
        String[] permissionsToRequest = getPermissionsToRequest();

        for (int i = 0; i < permissionsToRequest.length; i++) {
            Log.i(LOG_TAG, permissionsToRequest[i]);
        }

        if (permissionsToRequest.length > 0) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionsToRequest, PERMISSIONS_REQUEST_CODE);
        } else {
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }
    }

    private String[] getPermissionsToRequest() {
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        for (int i = 0; i < mPermissionsRequired.length; i++) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, mPermissionsRequired[i]) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(mPermissionsRequired[i]);
            }
        }

        return permissionsToRequest.toArray(new String[0]);
    }


}