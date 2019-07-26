package edu.fudan.jenaDemon;

import java.io.InputStream;

public class Utils {
    public static InputStream getResourceAsStream(String filePath){
        InputStream in = Utils.class.getClassLoader().getResourceAsStream(filePath);
        return in;
    }
}
