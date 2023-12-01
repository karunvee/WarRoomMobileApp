package com.example.warroomapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Date;

public class JobTaskParameter implements Parcelable {
    private int id;
    private String plant_name;
    private String line_name;
    private String machine_name;
    private String equipId;
    private String equipType;
    private String cameraIp1;
    private String cameraIp2;
    private String job_name;
    private String description;
    private String type_of;
    private String responder;
    private String start_date;
    private String end_date;

    protected JobTaskParameter(Parcel in) {
        id = in.readInt();
        plant_name = in.readString();
        line_name = in.readString();
        machine_name = in.readString();
        equipId = in.readString();
        equipType = in.readString();
        cameraIp1 = in.readString();
        cameraIp2 = in.readString();
        job_name = in.readString();
        description = in.readString();
        type_of = in.readString();
        responder = in.readString();
        start_date = in.readString();
        end_date = in.readString();
    }

    public static final Creator<JobTaskParameter> CREATOR = new Creator<JobTaskParameter>() {
        @Override
        public JobTaskParameter createFromParcel(Parcel in) {
            return new JobTaskParameter(in);
        }

        @Override
        public JobTaskParameter[] newArray(int size) {
            return new JobTaskParameter[size];
        }
    };

    public  int getId() { return  id;}
    public String getPlant() { return plant_name;}
    public String getLine() { return  line_name;}
    public String getMachine() { return  machine_name;}
    public String getEquipId() { return  equipId;}
    public String getEquipType() { return  equipType;}
    public String getCameraIp1() { return  cameraIp1;}
    public String getCameraIp2() { return  cameraIp2;}
    public String getJob() { return  job_name;}
    public String getDescription() { return  description;}
    public String getTypeOf() { return  type_of;}
    public String getResponder() { return  responder;}
    public String getStartDate() {return  start_date;}
    public String getEndDate() { return end_date;}

    public JobTaskParameter(int id, String plant_name, String line_name, String machine_name,
                        String equipId, String equipType, String cameraIp1, String cameraIp2, String job_name, String description, String type_of,
                        String responder, String start_date, String end_date){
        this.id = id;
        this.plant_name = plant_name;
        this.line_name = line_name;
        this.machine_name = machine_name;
        this.equipId = equipId;
        this.equipType = equipType;
        this.cameraIp1 = cameraIp1;
        this.cameraIp2 = cameraIp2;
        this.job_name = job_name;
        this.description = description;
        this.type_of = type_of;
        this.responder = responder;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(plant_name);
        parcel.writeString(line_name);
        parcel.writeString(machine_name);
        parcel.writeString(equipId);
        parcel.writeString(equipType);
        parcel.writeString(cameraIp1);
        parcel.writeString(cameraIp2);
        parcel.writeString(job_name);
        parcel.writeString(description);
        parcel.writeString(type_of);
        parcel.writeString(responder);
        parcel.writeString(start_date);
        parcel.writeString(end_date);
    }
}
