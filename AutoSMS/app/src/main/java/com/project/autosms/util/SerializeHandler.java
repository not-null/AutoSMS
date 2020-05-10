package com.project.autosms.util;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializeHandler {

    /**
     * Serializes an object.
     */

    public static <T extends Serializable> void saveObject(Context context, T object, String filename) {
        try {
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(object);

            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a serialized object.
     */

    public static<T extends Serializable> T readObject(Context context, String filename) {
        T returnObject = null;

        try {
            FileInputStream fip = context.openFileInput(filename);
            ObjectInputStream ois = new ObjectInputStream(fip);
            returnObject = (T) ois.readObject();

            ois.close();
            fip.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return returnObject;
    }

    /**
     * Removes a specified file.
     */

    public static void removeSavedData(Context context, String filename) {
        context.deleteFile(filename);
    }

}