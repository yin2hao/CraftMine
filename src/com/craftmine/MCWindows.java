package com.craftmine;

public class MCWindows extends Minecraft{
    private final long windowshandle;
    private int height;
    private int width;

    public static class windowsOptions(){
        public boolean compatibleProfile;
        public int height;
        public int width;
        public int fps;
        public int ups = Engine.TARGET_UPS;
    }
}