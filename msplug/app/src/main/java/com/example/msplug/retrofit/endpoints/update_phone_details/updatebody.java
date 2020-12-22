package com.example.msplug.retrofit.endpoints.update_phone_details;

import com.google.gson.annotations.SerializedName;

public class updatebody {
    @SerializedName("name")
    private String name;
    @SerializedName("sim_1")
    private String sim_1;
    @SerializedName("sim_2")
    private String sim_2;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSim_1() {
        return sim_1;
    }

    public void setSim_1(String sim_1) {
        this.sim_1 = sim_1;
    }

    public String getSim_2() {
        return sim_2;
    }

    public void setSim_2(String sim_2) {
        this.sim_2 = sim_2;
    }
}
