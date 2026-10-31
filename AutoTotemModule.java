package com.example.cheatclient.module.impl;

import com.example.cheatclient.module.Module;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class AutoTotemModule extends Module {
    private int tickDelay = 0;

    public AutoTotemModule() {
        // Привязка на клавишу T (T для Totem)
        super("AutoTotem", "Автоматически берет тотем в оффхенд", GLFW.GLFW_KEY_T);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        
        // Не работаем, если открыт инвентарь или сундук (чтобы не сломать логику)
        if (mc.currentScreen != null) return; 

        // Задержка между кликами, чтобы не спамить сервер пакетами
        if (tickDelay > 0) {
            tickDelay--;
            return;
        }

        // Если в оффхенде (вторая рука) уже лежит тотем — ничего не делаем
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        // Ищем тотем в инвентаре (0-8 это хотбар, 9-35 основной инвентарь)
        int totemSlot = -1;
        for (int i = 0; i < 36; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                totemSlot = i;
                break;
            }
        }

        // Если нашли тотем — меняем его местами с оффхендом
        if (totemSlot != -1) {
            // В PlayerScreenHandler слоты хотбара идут с 36 по 44, а инвентаря с 9 по 35
            int screenSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;

            // Клик с типом SWAP (кнопка 40 означает оффхенд)
            mc.interactionManager.clickSlot(
                    mc.player.playerScreenHandler.syncId,
                    screenSlot,
                    40, // 40 = слот оффхенда при свапе
                    SlotActionType.SWAP,
                    mc.player
            );
            
            // Ставим задержку в 5 тиков (0.25 сек), чтобы избежать рассинхрона
            tickDelay = 5; 
        }
    }
}
