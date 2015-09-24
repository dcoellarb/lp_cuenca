package com.dc.lockphone;

/**
 * Created by dcoellar on 9/23/15.
 */
public class PhoneInfo {

    private String brand;
    private String model;
    private String internal_brand;
    private String internal_model;
    private String imei;
    private String imageUrl;

    public PhoneInfo(){
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getInternal_brand() {
        return internal_brand;
    }

    public void setInternal_brand(String internal_brand) {
        this.internal_brand = internal_brand;
    }

    public String getInternal_model() {
        return internal_model;
    }

    public void setInternal_model(String internal_model) {
        this.internal_model = internal_model;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
