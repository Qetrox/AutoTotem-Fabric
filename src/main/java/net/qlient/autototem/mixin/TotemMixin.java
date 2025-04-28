/*
 * This file contains code based on AutoTotem-Fabric by Developer-Mike (https://github.com/Developer-Mike/Autototem-Fabric),
 * licensed under the MIT License.
 *
 * Original code Copyright (c) 2022 MikaDev
 * Modifications and additional original code Copyright (c) 2025 Qetrox
 *
 * MIT-licensed code is clearly marked below.
 * All other code is © 2025 Qetrox, All rights reserved.
 */

package net.qlient.autototem.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.qlient.autototem.config.AutototemConfigManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(GameRenderer.class)
public class TotemMixin {

    // --- MIT LICENSED CODE START ---
    @Unique
    private ArrayList<Packet<?>> packetsToSend = new ArrayList<>();

    @Unique
    private int tickCounter = 0;
    private int delayTicks = 0;
    // --- MIT LICENSED CODE END ---

    // --- MIT LICENSED CODE (modified) ---
    @Inject(at=@At("TAIL"), method="tick")
    private void onTick(CallbackInfo ci) {
        if (packetsToSend.isEmpty()) return;
        tickCounter++;

        // Only send a packet if the delay has passed
        if (tickCounter >= delayTicks) {
            ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
            if (networkHandler == null) return;

            networkHandler.sendPacket(packetsToSend.get(0));
            packetsToSend.remove(0);

            // Reset the tick counter if done
            if (packetsToSend.isEmpty()) tickCounter = 0;
        }
    }

    @Inject(at=@At("TAIL"), method="showFloatingItem")
    private void onTotemUse(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.isOf(Items.TOTEM_OF_UNDYING)) return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);
        if (gameRenderer.getClient().player == null) return;

        if (AutototemConfigManager.getConfig().CheckForEffects) {
            if (!gameRenderer.getClient().player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) return;
            if (!gameRenderer.getClient().player.hasStatusEffect(StatusEffects.REGENERATION)) return;
        }

        int slot = getTotemSlot(gameRenderer.getClient().player.getInventory());
        if (slot == -1) return;
        restockSlot(gameRenderer.getClient().player, slot);
    }
    // --- MIT LICENSED CODE (modified) END ---

    // --- QLIENT ORIGINAL CODE START ---
    @Unique
    private int getTotemSlot(PlayerInventory inventory) {
        for (int i = 9; i < inventory.main.size(); i++) { // Check inventory first
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        for (int i = 0; i < 9; i++) { // Then check hotbar
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1;
    }

    @Unique
    private int getDelay() {
        if (AutototemConfigManager.getConfig().AddRandomDelay) {
            return AutototemConfigManager.getConfig().DelayInMilliseconds + (int) (Math.random() * AutototemConfigManager.getConfig().MaxRandomDelay);
        } else {
            return AutototemConfigManager.getConfig().DelayInMilliseconds;
        }
    }

    @Unique
    private void restockSlot(PlayerEntity player, int slot) {
        PlayerInventory playerInventory = player.getInventory();
        ScreenHandler screenHandler = player.currentScreenHandler;

        delayTicks = getDelay() / 50;
        packetsToSend = new ArrayList<>();
        Int2ObjectMap<ItemStack> emptyMap = new Int2ObjectArrayMap<>();

        if (slot < 9) {
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(slot));
            packetsToSend.add(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                    BlockPos.ORIGIN,
                    Direction.DOWN
            ));
            packetsToSend.add(new UpdateSelectedSlotC2SPacket(playerInventory.selectedSlot));
        } else {
            packetsToSend.add(new ClickSlotC2SPacket(
                    screenHandler.syncId,
                    screenHandler.getRevision(),
                    slot,
                    0,
                    SlotActionType.PICKUP,
                    playerInventory.getStack(slot).copy(),
                    emptyMap
            ));
            packetsToSend.add(new ClickSlotC2SPacket(
                    screenHandler.syncId,
                    screenHandler.getRevision(),
                    45,
                    0,
                    SlotActionType.PICKUP,
                    playerInventory.getStack(slot).copy(),
                    emptyMap
            ));
        }
    }
    // --- QLIENT ORIGINAL CODE END ---
}
