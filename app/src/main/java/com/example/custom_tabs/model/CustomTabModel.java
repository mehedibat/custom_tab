package com.example.custom_tabs.model;



import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public class CustomTabModel  implements Serializable {

    @SerializedName("id")
    private String id;
    @SerializedName("uuId")
    private String uuId;


}
