package com.craftmine;

public class MCWindows extends Minecraft{

    public static void start(){
        init();

        while (true){
            input();
            loof();
            render();
        }
    }
}
