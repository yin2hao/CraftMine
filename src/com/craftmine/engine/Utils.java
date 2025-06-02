package com.craftmine.engine;

import java.io.IOException;
import java.nio.file.*;

public class Utils {

    private Utils() {
    }

    //用来阅读着色器文件的内容
    public static String readFile(String filePath){
        String str;
        try{
            str = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e){
            throw new RuntimeException("无法读取文件 [" + filePath + "]", e);
        }
        return str;
    }
}