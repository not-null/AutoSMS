package com.project.autosms.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Record implements Serializable, Cloneable {
    private String nr;
    private ArrayList<ResponseMapping> responseMappings;

    // Used when loading from file
    public Record(String nr, ArrayList<ResponseMapping> responseMappings) {
        this.nr = nr;
        this.responseMappings = responseMappings;
    }

    public Record(String nr) {
        this.nr = nr;
        this.responseMappings = new ArrayList<>();
    }

    // Used when creating the first response
    public Record(String nr, ResponseMapping responseMapping) {
        this.nr = nr;
        this.responseMappings = new ArrayList<>();
        this.responseMappings.add(responseMapping);
    }

    public String getNr() {
        return nr;
    }

    public void setNr(String nr) {
        this.nr = nr;
    }

    public ArrayList<ResponseMapping> getResponseMappings() {
        return responseMappings;
    }

    public void setResponseMappings(ArrayList<ResponseMapping> responseMappings) {
        this.responseMappings = responseMappings;
    }

    public void addResponseMapping(ResponseMapping rm){
        this.responseMappings.add(rm);
    }

    @NonNull
    @Override
    public String toString() {
        return nr + "\n" + Arrays.toString(responseMappings.toArray());
    }

    // Creates a deep copy
    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {
        Record r = new Record(this.nr);

        for (ResponseMapping rm: responseMappings)
            r.addResponseMapping((ResponseMapping) rm.clone());

        return r;
    }
}
