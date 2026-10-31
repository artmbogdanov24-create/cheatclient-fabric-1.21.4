package com.example.cheatclient.module.impl;

import com.example.cheatclient.module.Module;
import org.lwjgl.glfw.GLFW;

public class EspModule extends Module {
    public EspModule() {
        super("ESP", "Подсветка сущностей", GLFW.GLFW_KEY_X);
    }

    @Override
    public void onTick() {
        // логика рендера — пока заглушка, чтобы проверить сборку
        if (mc.world == null) return;
        // TODO: рендер боксов через WorldRenderEvents.LAST
    }
}
