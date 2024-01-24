package com.example.warroomapp.Activity.Class;

import android.os.Parcel;

public class ProficientSkillParameter {

    private int pk;
    private String line_name;
    private String machine_name;
    private String equip_code;
    private String equip_type;
    private String fund_point;
    private String skill_point;

    public ProficientSkillParameter(int pk, String line_name, String machine_name, String equip_code, String equip_type, String fund_point, String skill_point ){
        this.pk = pk;
        this.line_name = line_name;
        this.machine_name = machine_name;
        this.equip_code = equip_code;
        this.equip_type = equip_type;
        this.fund_point = fund_point;
        this.skill_point = skill_point;
    }

    public int getPk(){return pk;}
    public String getLine_name() { return line_name;}
    public String getMachine_name() { return machine_name;}
    public String getEquip_code() { return equip_code;}
    public String getEquip_type() { return equip_type;}
    public String getFund_point() { return fund_point;}
    public String getSkill_point() { return skill_point;}
}
