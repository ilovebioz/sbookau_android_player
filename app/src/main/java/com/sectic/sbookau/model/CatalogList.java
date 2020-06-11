package com.sectic.sbookau.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bioz on 6/23/2017.
 */

public class CatalogList {
    @SerializedName("data")
    public List<Catalog> lOCatalog;

    public CatalogList(){
        lOCatalog = new ArrayList<>();
    }
}
