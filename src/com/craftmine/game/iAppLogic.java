package com.craftmine.game;

public interface iAppLogic {
    void cleanup();
    void init(Window window,Scene scene,Render render);
    void updata(Window window,Scene scene,long diffTimeMillis);
    void render(Window window,Scene scene,long diffTimeMillis);
}