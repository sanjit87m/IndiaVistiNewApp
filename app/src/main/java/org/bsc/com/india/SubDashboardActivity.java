package org.bsc.com.india;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bsc.com.india.PdfView.SampleActivity;
import org.bsc.com.india.VideoPlayerView.FullscreenActivity;
import org.bsc.com.india.presenter.DashboardPresenter;
import org.bsc.com.india.presenter.DashboardPresenterImp;
import org.bsc.com.india.util.AppConstants;
import org.bsc.com.india.util.NetworkUtils;
import org.bsc.com.india.view.DashboardView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SubDashboardActivity extends Activity implements DashboardView{
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    HashMap<String,String> contentData;
    ArrayList<Model.Categories.Subcategories> subcategories;
    //FinalExpandableListAdapter listAdapter;
    SubDashboardAdapter subDashboardAdapter;
    GridView gridView;
    String FILE_NAME ="hello.txt";
    //Permision code that will be checked in the method onRequestPermissionsResult
    private int STORAGE_PERMISSION_CODE = 23;
    TextView header_back_tv;
    TextView header_detail;
    Activity activity = SubDashboardActivity.this;
    DashboardPresenter loginPresenter;
    private RelativeLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Intent intent = getIntent();
        subcategories = (ArrayList<Model.Categories.Subcategories>) intent.getSerializableExtra("subcategories");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        header_back_tv = (TextView) toolbar.findViewById(R.id.header_back_tv);
        header_back_tv.setVisibility(View.VISIBLE);
        header_detail =(TextView) toolbar.findViewById(R.id.header_detail);
        header_detail.setVisibility(View.INVISIBLE);
        header_back_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // get the listview
        gridView = (GridView) findViewById(R.id.gridview);
        progressBar = (RelativeLayout) findViewById(R.id.progress_login_rl);
        progressBar.setVisibility(View.VISIBLE);

        subDashboardAdapter = new SubDashboardAdapter(this, subcategories);
        gridView.setAdapter(subDashboardAdapter);
        progressBar.setVisibility(View.GONE);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View v, int position, long id){

                String fileType = subcategories.get(position).getFileType();
                String fileName = subcategories.get(position).getFileName();
                String url = subcategories.get(position).getUrl();
                if(!NetworkUtils.isNetworkConnected(SubDashboardActivity.this)){
                    NetworkUtils.open("Please Check Your Internet Connection and Try Again",SubDashboardActivity.this);
                    return;
                }

                if(url.equalsIgnoreCase("")){
                NetworkUtils.open("Unable to access content now. Please try again after sometime",SubDashboardActivity.this);
                return;

         }


                if(fileType.equalsIgnoreCase("youtube")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(fileName));
                    startActivity(browserIntent);
                } else if(fileType.equalsIgnoreCase("pdf")){

                    String tempFileName = fileName.substring(0,fileName.indexOf("."));
                    //  Display screenOrientation = getWindowManager().getDefaultDisplay();
                    if(activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        Intent intent = new Intent(SubDashboardActivity.this, SampleActivity.class);
                        intent.putExtra("fileName", tempFileName+"1.pdf");
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(SubDashboardActivity.this, SampleActivity.class);
                        intent.putExtra("fileName", tempFileName+"2.pdf");
                        intent.putExtra("url", url);
                        startActivity(intent);
                    }
                }
                else if(fileType.equalsIgnoreCase("image")){
                    Intent intent = new Intent(SubDashboardActivity.this, ImageActivity.class);
                    intent.putExtra("fileName",fileName);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }else if(fileType.equalsIgnoreCase("video")){
                    Intent intent = new Intent(SubDashboardActivity.this, FullscreenActivity.class);
                    intent.putExtra("fileName",fileName);
                    intent.putExtra("url",url);

                    startActivity(intent);
                }
            }
        });





//        // Read json data from local file and parsing the data to arraylist
//        String jsonData =    readFileFromAssets();
//        GsonBuilder gsonBuilder = new GsonBuilder();
//        Gson gson = gsonBuilder.create();
//        final Model model = gson.fromJson(jsonData, Model.class);
//        System.out.print("JSON parse is done");

//        loginPresenter = new DashboardPresenterImp(this, AppConstants.SUB_DASHBOARD_API);
//        loginPresenter.getDashboardjsondata();




//
    }

    public String readFileFromAssets(){
        String text = "";
        try{
            InputStream inputStream = getAssets().open("subdashboardui.json");
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

    @Override
    public void onFailure(String result, int code) {

    }



    @Override
    public void setData(final Model model) {

    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Unable to access content now. Please try again after sometime");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    }

