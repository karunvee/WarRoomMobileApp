package com.example.warroomapp.Activity.Class;

public class JobHistoryParameter {
    private int pk;
    private String reason_en;
    private String reason_th;
    private String solution_en;
    private String solution_th;
    private String response_by;
    private String ended_date;

    public JobHistoryParameter(int pk, String reason_en, String reason_th, String solution_en, String solution_th, String response_by, String ended_date){
        this.pk = pk;
        this.reason_en = reason_en;
        this.reason_th = reason_th;
        this.solution_en = solution_en;
        this.solution_th = solution_th;
        this.response_by = response_by;
        this.ended_date = ended_date;
    }
    public  int getPk(){ return pk; }
    public  String getReason_en(){ return  reason_en; }
    public  String getReason_th(){ return  reason_th; }
    public  String getSolution_en(){ return solution_en; }
    public  String getSolution_th(){ return  solution_th; }
    public String getResponse_by(){ return  response_by; }
    public String getEnded_date(){ return  ended_date; }

}
