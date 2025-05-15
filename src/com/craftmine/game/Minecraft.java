package com.craftmine.game;

public class Minecraft {

    public static void main(){};
    public static void main(String[] args) {
        minecraft.start();
    }

    public static void start(){
        while (KeepOnRunning){
            init();
            updata();
            render();
        }
    }
}