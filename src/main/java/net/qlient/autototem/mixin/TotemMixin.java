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
import net.minecraft.network.packet.c2s.play.PickFromInventoryC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
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

    @Unique
    private ArrayList<Packet<?>> packetsToSend = new ArrayList<>();

    @Unique
    private int tickCounter = 0;
    private int delayTicks = 0;

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

            // Reset the tick counter
            if(packetsToSend.isEmpty()) tickCounter = 0;

        }
    }

    @Inject(at=@At("TAIL"), method="showFloatingItem")
    private void onTotemUse(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.isOf(Items.TOTEM_OF_UNDYING)) return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);

        if (gameRenderer.getClient().player == null) return;

        if(AutototemConfigManager.getConfig().CheckForEffects) {
            if (!gameRenderer.getClient().player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) return;
            if (!gameRenderer.getClient().player.hasStatusEffect(StatusEffects.REGENERATION)) return;
        }

        int slot = getTotemSlot(gameRenderer.getClient().player.getInventory());
        if (slot == -1) return;
        restockSlot(gameRenderer.getClient().player, slot);
    }

    @Unique
    private int getTotemSlot(PlayerInventory inventory) {
        for (int i = 9; i < inventory.main.size(); i++) { // Check inventory first
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        for(int i = 0; i < 9; i++) { // Check hotbar
            ItemStack stack = inventory.main.get(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) return i;
        }
        return -1;
    }

    private int getDelay() {
        if (AutototemConfigManager.getConfig().AddRandomDelay) {
            return AutototemConfigManager.getConfig().DelayInMilliseconds + (int) (Math.random() * AutototemConfigManager.getConfig().MaxRandomDelay);
        } else {
            return AutototemConfigManager.getConfig().DelayInMilliseconds;
        }
    }

    @Unique
    private void restockSlot(PlayerEntity p, int s) {
        PlayerInventory playerInventory = p.getInventory();
        delayTicks = getDelay() / 50;
        if (AutototemConfigManager.getConfig().LegitSwap) {
            packetsToSend = new ArrayList<>();
            if (s < 9) {
                packetsToSend.add(new UpdateSelectedSlotC2SPacket(s));
                packetsToSend.add(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                packetsToSend.add(new UpdateSelectedSlotC2SPacket(playerInventory.selectedSlot));
            } else {
                packetsToSend.add(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
                packetsToSend.add(new PickFromInventoryC2SPacket(s));
                packetsToSend.add(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            }
        } else {
            packetsToSend = new ArrayList<>();
            Int2ObjectMap<ItemStack> emptyMap = new Int2ObjectArrayMap<>();

            if (s < 9) {
                packetsToSend.add(new UpdateSelectedSlotC2SPacket(s));

                packetsToSend.add(new PlayerActionC2SPacket(
                        PlayerActionC2SPacket.Action.SWAP_ITEM_WITH_OFFHAND,
                        BlockPos.ORIGIN,
                        Direction.DOWN
                ));

                packetsToSend.add(new UpdateSelectedSlotC2SPacket(playerInventory.selectedSlot));
            } else {
                packetsToSend.add(new ClickSlotC2SPacket(
                        p.currentScreenHandler.syncId,
                        p.currentScreenHandler.getRevision(),
                        s,
                        0,
                        SlotActionType.PICKUP,
                        playerInventory.getStack(s).copy(),
                        emptyMap
                ));

                packetsToSend.add(new ClickSlotC2SPacket(
                        p.currentScreenHandler.syncId,
                        p.currentScreenHandler.getRevision(),
                        45,
                        0,
                        SlotActionType.PICKUP,
                        playerInventory.getStack(s).copy(),
                        emptyMap
                ));
            }
        }
    }
}
