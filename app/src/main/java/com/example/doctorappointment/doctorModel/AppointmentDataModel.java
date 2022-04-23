package com.example.doctorappointment.doctorModel;

import java.io.Serializable;

public class AppointmentDataModel implements Serializable {

    private String doctorName, doctorSpecializedArea, doctorDegree, hospitalName, visitingFee, proImgUri, patientName, patientPhone, time, docUserId, patientUserId, appointStatus, pushKey;

    public AppointmentDataModel() {
    }

    public AppointmentDataModel(String doctorName, String doctorSpecializedArea, String doctorDegree, String hospitalName, String visitingFee, String proImgUri, String patientName, String patientPhone, String time, String docUserId, String patientUserId, String appointStatus, String pushKey) {
        this.doctorName = doctorName;
        this.doctorSpecializedArea = doctorSpecializedArea;
        this.doctorDegree = doctorDegree;
        this.hospitalName = hospitalName;
        this.visitingFee = visitingFee;
        this.proImgUri = proImgUri;
        this.patientName = patientName;
        this.patientPhone = patientPhone;
        this.time = time;
        this.docUserId = docUserId;
        this.patientUserId = patientUserId;
        this.appointStatus = appointStatus;
        this.pushKey = pushKey;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorSpecializedArea() {
        return doctorSpecializedArea;
    }

    public void setDoctorSpecializedArea(String doctorSpecializedArea) {
        this.doctorSpecializedArea = doctorSpecializedArea;
    }

    public String getDoctorDegree() {
        return doctorDegree;
    }

    public void setDoctorDegree(String doctorDegree) {
        this.doctorDegree = doctorDegree;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getVisitingFee() {
        return visitingFee;
    }

    public void setVisitingFee(String visitingFee) {
        this.visitingFee = visitingFee;
    }

    public String getProImgUri() {
        return proImgUri;
    }

    public void setProImgUri(String proImgUri) {
        this.proImgUri = proImgUri;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public void setPatientPhone(String patientPhone) {
        this.patientPhone = patientPhone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDocUserId() {
        return docUserId;
    }

    public void setDocUserId(String docUserId) {
        this.docUserId = docUserId;
    }

    public String getPatientUserId() {
        return patientUserId;
    }

    public void setPatientUserId(String patientUserId) {
        this.patientUserId = patientUserId;
    }

    public String getAppointStatus() {
        return appointStatus;
    }

    public void setAppointStatus(String appointStatus) {
        this.appointStatus = appointStatus;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }
}
