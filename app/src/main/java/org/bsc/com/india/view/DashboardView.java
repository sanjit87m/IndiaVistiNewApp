package org.bsc.com.india.view;


import org.bsc.com.india.Model;

/**
 * Created by sanjit on 20/12/17.
 */

public interface DashboardView {
    public void onFailure(String result, int code);
    void setData(Model model);




}
