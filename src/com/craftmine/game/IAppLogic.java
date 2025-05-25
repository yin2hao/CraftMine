package com.craftmine.game;

import com.craftmine.engine.*;

public interface IAppLogic {

    void cleanup();

    void init(MCWindows windows,Scene scene , Render render);

    void update(MCWindows windows , Scene scene , long diffTimeMillis);

    void input(MCWindows windows , Scene scene , long diffTimeMillis, boolean inputConsumed);
}