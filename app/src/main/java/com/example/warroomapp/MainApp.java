package com.example.warroomapp;

import android.app.Application;

public class MainApp extends Application {
    private Integer pk_reasonCode;
    private Integer pk_solutionCode;

    public Integer getPk_reasonCode() {
        return pk_reasonCode;
    }
    public Integer getPk_solutionCode() {
        return pk_solutionCode;
    }

    public void setPk_reasonCode(Integer pk_reasonCode) {
        this.pk_reasonCode = pk_reasonCode;
    }
    public void setPk_solutionCode(Integer pk_solutionCode) {
        this.pk_solutionCode = pk_solutionCode;
    }
}
