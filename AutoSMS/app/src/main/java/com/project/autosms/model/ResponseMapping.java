package com.project.autosms.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ResponseMapping implements Serializable, Cloneable {
    private String string;
    private String response;
    private Position position;

    public ResponseMapping(String string, String response, Position position) {
        this.string = string.toLowerCase();
        this.response = response.toLowerCase();
        this.position = position;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @NonNull
    @Override
    public String toString() {
        return position.toString() + " " + string + " : " + response;
    }

    // Creates a deep copy
    @NonNull
    @Override
    protected Object clone() throws CloneNotSupportedException {

        ResponseMapping clone = new ResponseMapping(this.string, this.response, Position.valueOf(this.position.toString()));

        return clone;
    }
}