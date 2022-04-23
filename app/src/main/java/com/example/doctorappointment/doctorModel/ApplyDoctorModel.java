package com.example.doctorappointment.doctorModel;

import java.io.Serializable;

public class ApplyDoctorModel implements Serializable {

    String doctorName;
    String hospitalName;
    String specializedField;
    String visitFee;
    String hospitalId;
    Boolean isApproved;
    String userId;
    String docApplyDegree;
    String docImageUrl;
    String docImageIdentityUrl;

    public ApplyDoctorModel() {
    }

    public ApplyDoctorModel(String doctorName, String hospitalName, String specializedField, String visitFee, String hospitalId, Boolean isApproved, String userId, String docApplyDegree, String docImageUrl, String docImageIdentityUrl) {
        this.doctorName = doctorName;
        this.hospitalName = hospitalName;
        this.specializedField = specializedField;
        this.visitFee = visitFee;
        this.hospitalId = hospitalId;
        this.isApproved = isApproved;
        this.userId = userId;
        this.docApplyDegree = docApplyDegree;
        this.docImageUrl = docImageUrl;
        this.docImageIdentityUrl = docImageIdentityUrl;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getSpecializedField() {
        return specializedField;
    }

    public void setSpecializedField(String specializedField) {
        this.specializedField = specializedField;
    }

    public String getVisitFee() {
        return visitFee;
    }

    public void setVisitFee(String visitFee) {
        this.visitFee = visitFee;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocApplyDegree() {
        return docApplyDegree;
    }

    public void setDocApplyDegree(String docApplyDegree) {
        this.docApplyDegree = docApplyDegree;
    }

    public String getDocImageUrl() {
        return docImageUrl;
    }

    public void setDocImageUrl(String docImageUrl) {
        this.docImageUrl = docImageUrl;
    }

    public String getDocImageIdentityUrl() {
        return docImageIdentityUrl;
    }

    public void setDocImageIdentityUrl(String docImageIdentityUrl) {
        this.docImageIdentityUrl = docImageIdentityUrl;
    }
}
