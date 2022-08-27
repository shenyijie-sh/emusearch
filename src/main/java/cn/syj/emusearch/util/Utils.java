package cn.syj.emusearch.util;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author syj
 **/
public class Utils {

    public static ImageIcon loadImageIcon(String name) {
        return new ImageIcon(Objects.requireNonNull(Utils.class.getClassLoader().getResource(name)));
    }

    public static String[] loadCfg(String name, String regex) {
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(Objects.requireNonNull(Utils.class.getClassLoader().getResourceAsStream(name)));
            int a = inputStream.available();
            byte[] buff = new byte[a];
            a = inputStream.read(buff);
            System.out.println("read " + a + " bytes...");
            String s = new String(buff);
            return s.split(regex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
