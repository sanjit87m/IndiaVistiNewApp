package org.bsc.com.india.PdfView;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


import org.bsc.com.india.FileDownloader;
import org.bsc.com.india.R;

import java.io.File;

public class SampleActivity extends Activity implements DownloadListener{
    private static final String TAG = "SampleActivity";
    //private static final String SAMPLE_FILE = "test.pdf";
    private static final String FILE_PATH = "filepath";
    private static final String SEARCH_TEXT = "text";
    private org.bsc.com.india.PdfView.PdfFragment fragment;
    private static Context context;
    TextView header_back_tv;
    TextView header_detail;
    FileDownloader fileDownloader = null;
    private String fileName="";
    private String url = "";
    private String filePath="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        fileName = getIntent().getStringExtra("fileName");
        url = getIntent().getStringExtra("url");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        header_back_tv = (TextView) toolbar.findViewById(R.id.header_back_tv);
        header_back_tv.setVisibility(View.VISIBLE);
        header_detail =(TextView) toolbar.findViewById(R.id.header_detail);
        header_detail.setVisibility(View.INVISIBLE);
        context = SampleActivity.this;
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download"+File.separator +"BSCVisit"+ File.separator+fileName;
        header_back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if(fileExist(filePath)){
            openPdfWithFragment(filePath);

        }else {
            downloadFile(url,fileName);

        }

    }
    private boolean fileExist(String filePath){
        File file = new File(filePath);
        if(file.exists()){
            return true;
        }
        else{
            return false;
        }

    }

    public void openPdfWithFragment(String filePath) {
        fragment = new PdfFragment();
        Bundle args = new Bundle();
        args.putString(FILE_PATH, filePath);
        //.putString(FILE_PATH, getFilesDir() + File.separator + "Download"+File.separator+fileName);
        fragment.setArguments(args);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }
    private void downloadFile(String url, String fileName) {
        try {
            fileDownloader = new FileDownloader(SampleActivity.this,url,fileName,this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fileDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                fileDownloader.execute();
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void onSuccess() {
        openPdfWithFragment(filePath);

    }

    @Override
    public void onFailure() {

    }
}
