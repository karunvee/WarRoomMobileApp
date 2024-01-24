package com.example.warroomapp.Activity;

public class ReasonSolutionParameter {
    private int id;
    private  String equipType;
    private String errorCode;
    private String en_description;
    private String th_description;
    private String update_emp;
    private String update_date;
    private String rating;

    public ReasonSolutionParameter(int id, String equipType, String errorCode, String en_description
    ,String th_description, String update_emp, String update_date, String rating){
        this.id = id;
        this.equipType = equipType;
        this.errorCode = errorCode;
        this.en_description = en_description;
        this.th_description = th_description;
        this.update_emp = update_emp;
        this.update_date = update_date;
        this.rating = rating;
    }

    public int getId(){ return  id;}
    public String getEquipType(){ return  equipType;}
    public String getErrorCode(){ return errorCode;}
    public String getEn_description(){ return en_description;}
    public String getTh_description(){ return  th_description;}
    public String getUpdate_emp(){ return  update_emp;}
    public String getUpdate_date(){ return  update_date;}
    public String getRating(){ return rating;}
}
