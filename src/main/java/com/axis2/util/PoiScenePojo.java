package com.axis2.util;

import java.io.Serializable;
import java.util.Date;

public class PoiScenePojo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String user_code;
    private String communityId;
    private String cityCode;
    private String communityName;
    private Integer truth;
    private String tag;
    private String locations;
    private String centers;
    private Double aceage;
    private Integer ispass;
    private String pass;
    private Integer isintersect;
    private Date udtime;
    private String districtCode;
    private Integer drawflag;


    public Integer getDrawflag() {
        return drawflag;
    }

    public void setDrawflag(Integer drawflag) {
        this.drawflag = drawflag;
    }

    public String getDistrictCode() {
        return districtCode;
    }

    public void setDistrictCode(String districtCode) {
        this.districtCode = districtCode;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Integer getTruth() {
        return truth;
    }

    public void setTruth(Integer truth) {
        this.truth = truth;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getCenters() {
        return centers;
    }

    public void setCenters(String centers) {
        this.centers = centers;
    }

    public Double getAceage() {
        return aceage;
    }

    public void setAceage(Double aceage) {
        this.aceage = aceage;
    }

    public Integer getIspass() {
        return ispass;
    }

    public void setIspass(Integer ispass) {
        this.ispass = ispass;
    }

    public Integer getIsintersect() {
        return isintersect;
    }

    public void setIsintersect(Integer isintersect) {
        this.isintersect = isintersect;
    }

    public Date getUdtime() {
        return udtime;
    }

    public void setUdtime(Date udtime) {
        this.udtime = udtime;
    }

}
