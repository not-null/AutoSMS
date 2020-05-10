package com.project.autosms.model;

import android.content.Context;
import android.util.Log;

import com.project.autosms.util.SerializeHandler;

import java.util.ArrayList;

public class ResponseManager {
    private static final String FILENAME = "responses";

    public static String getResponse(String nr, String msg, Context context){

        ArrayList<Record> records = SerializeHandler.readObject(context, FILENAME);

        //Go through the records
        for (Record r : records){
            //If the number has responses
            if (nr.equals(r.getNr())){
                Log.i("MANAGER", "FOUND NR");
                //Get the responses
                ArrayList<ResponseMapping> rms = r.getResponseMappings();
                //Go through the responses
                for (ResponseMapping rm : rms){
                    //Check where to look for string
                    switch(rm.getPosition()){
                        case STARTS:
                            if (msg.toLowerCase().startsWith(rm.getString()))
                                return rm.getResponse();
                            break;
                        case CONTAINS:
                            Log.i("MANAGER", "CONTAINS");
                            if (msg.toLowerCase().contains(rm.getString()))
                                return rm.getResponse();
                            Log.i("MANAGER", "Match failed");
                            break;
                        case ENDS:
                            if (msg.toLowerCase().endsWith(rm.getString()))
                                return rm.getResponse();
                            break;
                    }
                }
            }
        }

        //Bad practice?
        return null;
    }
}
