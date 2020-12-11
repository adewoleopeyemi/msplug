package com.example.msplug.retrofit.endpoint_request_list;

import com.google.gson.annotations.SerializedName;

public class requestlistresponse {
    @SerializedName("id")
    private transient int id;
    @SerializedName("device")
    private String device;
    @SerializedName("device_name")
    private String device_name;
    @SerializedName("sim_slot")
    private String sim_slot;
    @SerializedName("request_type")
    private String request_type;
    @SerializedName("response_message")
    private String response_message;
    @SerializedName("command")
    private String command;
    @SerializedName("Receipient")
    private String Receipient;
    @SerializedName("status")
    private String status;
    @SerializedName("request_date")
    private String request_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getSim_slot() {
        return sim_slot;
    }

    public void setSim_slot(String sim_slot) {
        this.sim_slot = sim_slot;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getReceipient() {
        return Receipient;
    }

    public void setReceipient(String receipient) {
        Receipient = receipient;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }
}
