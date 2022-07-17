package io.hoogland.utils;

import java.io.*;

public class FileUtils {

    /**
     * Writes a given object to given file. Overwrites the file if it already exists.
     *
     * @param obj  Object to be written to file.
     * @param file File the object will be written to.
     * @throws IOException
     */
    public static void writeToFile(Object obj, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file, false);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(obj);
        objectOutputStream.flush();
    }

    /**
     * Reads the object from a given file.
     *
     * @param file File object will be read from.
     * @return Object read from file.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Object readFromFile(File file) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        return objectInputStream.readObject();
    }

}
