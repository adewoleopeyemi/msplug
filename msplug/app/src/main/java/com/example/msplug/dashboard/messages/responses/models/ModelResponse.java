package com.example.msplug.dashboard.messages.responses.models;

import java.io.Serializable;

public class ModelResponse{
    int id;
    int device;
    String device_name;
    String sim_slot;
    String request_type;
    String response_message;
    String Command;
    String Receipient;
    String status;
    String request_date;

    public String getRequest_date() {
        return request_date;
    }

    public void setRequest_date(String request_date) {
        this.request_date = request_date;
    }

    public ModelResponse() {
    }

    public ModelResponse(int id, int device, String device_name, String sim_slot, String request_type, String response_message, String command, String receipient, String status) {
        this.id = id;
        this.device = device;
        this.device_name = device_name;
        this.sim_slot = sim_slot;
        this.request_type = request_type;
        this.response_message = response_message;
        Command = command;
        Receipient = receipient;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDevice() {
        return device;
    }

    public void setDevice(int device) {
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
        return Command;
    }

    public void setCommand(String command) {
        Command = command;
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
}
