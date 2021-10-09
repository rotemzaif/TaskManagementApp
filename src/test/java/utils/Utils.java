package utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Utils class as an assistance class which includes methods to read/write data from files
 * */
public class Utils {
    // objects
    private static FileInputStream fis;
    // variables

    // methods
    /**
     * this method reads a property from a configuration.properties file and return its value
     * @param - a String key property name
     * @return - property value
     * */
    public static String readProperty(String key){
        String value = "";
        try {
            fis = new FileInputStream(".\\src\\test\\resources\\data\\configuration.properties");
            Properties prop = new Properties();
            prop.load(fis); // load a properties file
            value = prop.getProperty(key); // get the property value
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;
    }
}
