package com.thriwin.expendio;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

import static com.thriwin.expendio.Utils.isNull;

@Setter
@Getter
public class User {
    private String name;
    private String number;
    private String pairDetail;
    private String pairDeviceName;

    @JsonIgnore
    public boolean isAlreadyPaired() {
        return !Utils.isEmpty(pairDetail);
    }

    @JsonIgnore
    public boolean hasPhoneNumber() {
        return !Utils.isEmpty(number);
    }

    @JsonIgnore
    public boolean isSMSFrom(String from) {
        return from.contains(this.number);
    }

    @JsonIgnore
    public boolean hasPairDetails() {
        return !Utils.isEmpty(pairDetail);
    }

    @JsonIgnore
    public boolean isMessageFrom(String from) {
        return this.pairDetail.equalsIgnoreCase(from);
    }

    @JsonIgnore
    public String getPairInfo() {
        if (isNull(this.pairDetail)) {
            return "Touch here to select paired device.";
        }
        return this.getPairDeviceName() + " - " + this.getPairDetail();
    }

    public void setPairInfoDetail(String pairInfoDetail) {
        String[] split = pairInfoDetail.split("-");
        this.pairDeviceName = split[0].trim();
        this.pairDetail = split[1].trim();
    }
}
