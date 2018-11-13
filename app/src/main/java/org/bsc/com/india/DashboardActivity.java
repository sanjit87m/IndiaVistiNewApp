package org.bsc.com.india;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import org.bsc.com.india.PdfView.SampleActivity;
import org.bsc.com.india.VideoPlayerView.FullscreenActivity;
import org.bsc.com.india.presenter.DashboardPresenterImp;
import org.bsc.com.india.util.AppConstants;
import org.bsc.com.india.util.NetworkUtils;
import org.bsc.com.india.view.DashboardView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DashboardActivity extends Activity implements DashboardView {
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String,String> contentData;
    //FinalExpandableListAdapter listAdapter;
    org.bsc.com.india.ImageAdapter imageAdapter;
    GridView gridView;
    String FILE_NAME ="hello.txt";
    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    Activity activity = DashboardActivity.this;
    org.bsc.com.india.presenter.DashboardPresenter loginPresenter;
    private RelativeLayout progressBar;
    String filePath;
    String directoryPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        // get the listview
        gridView = (GridView) findViewById(R.id.gridview);
        if(!NetworkUtils.isNetworkConnected(DashboardActivity.this)){
            NetworkUtils.open("Please Check Your Internet Connection and Try Again",DashboardActivity.this);
            return;
        }
        progressBar = (RelativeLayout) findViewById(R.id.progress_login_rl);
        progressBar.setVisibility(View.VISIBLE);
        filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download"+File.separator+"BSCVisit";
        directoryPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Download";
        File path = new File(filePath);
        deleteDirectory(path);

        createFolder(directoryPath);
//        // Read json data from local file and parsing the data to arraylist
//        String jsonData =    readFileFromAssets();
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        Gson gson = gsonBuilder.create();
//        final Model model = gson.fromJson(jsonData, Model.class);
//        System.out.print("JSON parse is done");

        loginPresenter = new DashboardPresenterImp(this, AppConstants.DASHBOARD_API);
        loginPresenter.getDashboardjsondata();


    }

    public String readFileFromAssets(){
        String text = "";
        try{
            InputStream inputStream = getAssets().open("dashboardui.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            text = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text;
           }

    public static boolean deleteDirectory(File path) {

        if (path.exists()) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

//    public void deleteFilesInDirectory(File path) {
//        if(path.exists()){
//            for(File file: path.listFiles())
//                if (!file.isDirectory())
//                    file.delete();
//        }
//
//    }

    private void createFolder(String directoyPath){
        File sub = new File(directoyPath, "BSCVisit");
        if (!sub.exists())
            sub.mkdirs();
    }

    @Override
    public void onFailure(String result, int code) {

    }



    @Override
    public void setData(final Model model) {

        imageAdapter = new ImageAdapter(this, model);
        // expListView.setAdapter(listAdapter);
        gridView.setAdapter(imageAdapter);
        progressBar.setVisibility(View.GONE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id) {

//
                String fileType = model.getCategories().get(position).getFileType();
                String fileName = model.getCategories().get(position).getFileName();
                String url = model.getCategories().get(position).getUrl();

                ArrayList<Model.Categories.Subcategories> subcategories = model.getCategories().get(position).getSubcategories();
                if (subcategories.size() > 0) {
                    Intent intent = new Intent(DashboardActivity.this, SubDashboardActivity.class);
                    intent.putExtra("subcategories", (Serializable) subcategories);
                    startActivity(intent);
                    return;
                }
                if (!NetworkUtils.isNetworkConnected(DashboardActivity.this)){
                    return;
                }

                if(url.equalsIgnoreCase("")){
                    NetworkUtils.open("Unable to access content now. Please try again after sometime",DashboardActivity.this);
                    return;

                }

                if (fileType.equalsIgnoreCase("youtube")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileName));
                    startActivity(browserIntent);
                } else if (fileType.equalsIgnoreCase("pdf")) {

                    String tempFileName = fileName.substring(0, fileName.indexOf("."));
                    //  Display screenOrientation = getWindowManager().getDefaultDisplay();
                    if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Intent intent = new Intent(DashboardActivity.this, SampleActivity.class);
                        intent.putExtra("fileName", tempFileName + "1.pdf");
                        intent.putExtra("url", url);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(DashboardActivity.this, SampleActivity.class);
                        intent.putExtra("fileName", tempFileName + "2.pdf");
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                } else if (fileType.equalsIgnoreCase("image")) {
                    Intent intent = new Intent(DashboardActivity.this, ImageActivity.class);
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("url", url);
                    startActivity(intent);
                } else if (fileType.equalsIgnoreCase("video")) {
                    Intent intent = new Intent(DashboardActivity.this, FullscreenActivity.class);
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("url", url);

                    startActivity(intent);
                }
            }
        });

    }



}
