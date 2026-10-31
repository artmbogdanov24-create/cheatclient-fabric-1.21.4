package com.example.cheatclient.module.impl;

import com.example.cheatclient.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.glfw.GLFW;

public class KillAuraModule extends Module {
    private float range = 3.5f; // Дальность удара (в блоках)

    public KillAuraModule() {
        // Привязка на клавишу K
        super("KillAura", "Автоматическая атака мобов", GLFW.GLFW_KEY_K);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;

        // Ждем восстановления кулдауна атаки (чтобы бить как в ванилле)
        if (mc.player.getAttackCooldownProgress(0.0f) < 1.0f) return;

        LivingEntity target = null;
        double bestDistance = Double.MAX_VALUE;

        // Ищем ближайшую цель
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof LivingEntity living) {
                // Игнорируем мертвых и себя
                if (!living.isAlive() || living == mc.player) continue;
                
                // Атакуем только злых мобов (HostileEntity) или других игроков
                if (!(living instanceof HostileEntity) && !(living instanceof PlayerEntity)) continue;

                double dist = mc.player.distanceTo(living);
                if (dist <= range && dist < bestDistance) {
                    target = living;
                    bestDistance = dist;
                }
            }
        }

        // Если цель найдена — бьем
        if (target != null) {
            faceEntity(target); // Поворачиваемся к цели
            mc.interactionManager.attackEntity(mc.player, target);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    // Логика поворота камеры на цель
    private void faceEntity(LivingEntity target) {
        Vec3d targetPos = target.getPos();
        Vec3d playerPos = mc.player.getPos();
        
        double dx = targetPos.x - playerPos.x;
        double dz = targetPos.z - playerPos.z;
        double dy = (targetPos.y + target.getStandingEyeHeight()) - (playerPos.y + mc.player.getStandingEyeHeight());
        
        double dist = Math.sqrt(dx * dx + dz * dz);
        
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dist));
        
        // Устанавливаем углы обзора
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);
    }
}
