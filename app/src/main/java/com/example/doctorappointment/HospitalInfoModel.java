package com.example.doctorappointment;

import java.io.Serializable;

public class HospitalInfoModel implements Serializable {

    String hospitalID;
    String hospitalName;
    String hospitalAddress;
    String hospitalContact;
    String image;


    public HospitalInfoModel() {
    }

    public HospitalInfoModel(String hospitalID, String hospitalName, String hospitalAddress, String hospitalContact, String image) {
        this.hospitalID = hospitalID;
        this.hospitalName = hospitalName;
        this.hospitalAddress = hospitalAddress;
        this.hospitalContact = hospitalContact;
        this.image = image;
    }

    public String getHospitalID() {
        return hospitalID;
    }

    public void setHospitalID(String hospitalID) {
        this.hospitalID = hospitalID;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }

    public String getHospitalContact() {
        return hospitalContact;
    }

    public void setHospitalContact(String hospitalContact) {
        this.hospitalContact = hospitalContact;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
