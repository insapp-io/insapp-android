package fr.insapp.insapp.models.credentials;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thomas on 10/07/2017.
 */

public class SigninCredentials {

    @SerializedName("Device")
    private String device;

    public SigninCredentials(String device) {
        this.device = device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDevice() {
        return device;
    }
}
