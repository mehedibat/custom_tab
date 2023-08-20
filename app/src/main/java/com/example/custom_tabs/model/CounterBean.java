package com.example.custom_tabs.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by FRabbi on 13-07-2023.
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CounterBean implements Serializable {
    private String id;
    private String counterName;
    private String counterNameBn;
    private Integer counterNo;
    private String branchId;
    @SerializedName("userPictureData")
    private String imageBase64;
}
