package com.example.warroomapp;

import java.util.Date;

public class JobTaskParameter {
    private int id;
    private String plant_name;
    private String line_name;
    private String machine_name;
    private String equipId;
    private String job_name;
    private String description;
    private String type_of;
    private String responder;
    private String start_date;
    private String end_date;

    public  int getId() { return  id;}
    public String getPlant() { return plant_name;}
    public String getLine() { return  line_name;}
    public String getMachine() { return  machine_name;}
    public String getEquipId() { return  equipId;}
    public String getJob() { return  job_name;}
    public String getDescription() { return  description;}
    public String getTypeOf() { return  type_of;}
    public String getResponder() { return  responder;}
    public String getStartDate() {return  start_date;}
    public String getEndDate() { return end_date;}

    public JobTaskParameter(int id, String plant_name, String line_name, String machine_name,
                        String equipId, String job_name, String description, String type_of,
                        String responder, String start_date, String end_date){
        this.id = id;
        this.plant_name = plant_name;
        this.line_name = line_name;
        this.machine_name = machine_name;
        this.equipId = equipId;
        this.job_name = job_name;
        this.description = description;
        this.type_of = type_of;
        this.responder = responder;
        this.start_date = start_date;
        this.end_date = end_date;
    }
}
