package com.dc.lockphone.model;

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
    private Double insuranceValue;
    private Double insuranceMontlyCost;
    private Double depreciation;
    private Double deductible;
    private PhoneInfoError error;
    private UserInfo userInfo;

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

    public PhoneInfoError getError() {
        return error;
    }

    public void setError(PhoneInfoError error) {
        this.error = error;
    }

    public Double getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(Double insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public Double getInsuranceMontlyCost() {
        return insuranceMontlyCost;
    }

    public void setInsuranceMontlyCost(Double insuranceMontlyCost) {
        this.insuranceMontlyCost = insuranceMontlyCost;
    }

    public Double getDeductible() {
        return deductible;
    }

    public void setDeductible(Double deductible) {
        this.deductible = deductible;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Double getDepreciation() {
        return depreciation;
    }

    public void setDepreciation(Double depreciation) {
        this.depreciation = depreciation;
    }
}
