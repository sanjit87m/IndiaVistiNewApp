package org.bsc.com.india;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.bsc.com.india.PdfView.DownloadListener;
import org.bsc.com.india.PdfView.SampleActivity;

import java.io.File;

public class ImageActivity extends Activity implements DownloadListener {
    private String fileName="";
    TextView header_back_tv;
    TextView header_detail;
    private String url = "";
    private String filePath="";
    FileDownloader fileDownloader = null;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        header_back_tv = (TextView) toolbar.findViewById(R.id.header_back_tv);
        header_back_tv.setVisibility(View.VISIBLE);
        header_detail =(TextView) toolbar.findViewById(R.id.header_detail);
        imageView = (ImageView) findViewById(R.id.image_view);

        header_detail.setVisibility(View.INVISIBLE);
        fileName = getIntent().getStringExtra("fileName");
        url = getIntent().getStringExtra("url");
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download"+File.separator +"BSCVisit"+ File.separator+fileName;

        header_back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(fileExist(filePath)){
            openImage(filePath,imageView);
        }else {
            downloadFile(url,fileName);

        }

    }
    private void downloadFile(String url, String fileName) {
        try {
            fileDownloader = new FileDownloader(ImageActivity.this,url,fileName,this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                fileDownloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                fileDownloader.execute();
            }
        } catch (Exception e) {

        }
    }
    private void openImage(String filePath, ImageView imageView){
        File imgFile = new  File(filePath);

        if(imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(myBitmap);

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

    @Override
    public void onSuccess() {
        openImage(filePath,imageView);
    }

    @Override
    public void onFailure() {

    }
}
