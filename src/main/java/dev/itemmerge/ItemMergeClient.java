package dev.itemmerge;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.util.HashSet;
import java.util.Set;

public class ItemMergeClient implements ClientModInitializer {
    // Kich thuoc o gom (block). Tang len de gom xa hon.
    public static final double CELL = 2.0;
    // Chi xu ly item trong ban kinh nay quanh nguoi choi.
    public static final double RANGE = 48.0;
    // Cap nhat moi N tick.
    public static final int INTERVAL = 4;

    // Danh sach ID item entity bi an khi render.
    public static volatile IntOpenHashSet HIDDEN = new IntOpenHashSet();

    private record Key(Item item, ComponentMap comps, int cx, int cy, int cz) {}

    private int tick = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.world == null || client.player == null) {
                HIDDEN = new IntOpenHashSet();
                return;
            }
            if (++tick % INTERVAL != 0) return;

            IntOpenHashSet hidden = new IntOpenHashSet();
            Set<Key> seen = new HashSet<>();

            for (ItemEntity e : client.world.getEntitiesByClass(
                    ItemEntity.class,
                    client.player.getBoundingBox().expand(RANGE),
                    x -> true)) {
                ItemStack stack = e.getStack();
                if (stack.isEmpty() || stack.getMaxCount() <= 1) continue; // khong gom do khong stack duoc

                Key key = new Key(
                        stack.getItem(),
                        stack.getComponents(),
                        MathHelper.floor(e.getX() / CELL),
                        MathHelper.floor(e.getY() / CELL),
                        MathHelper.floor(e.getZ() / CELL));

                if (!seen.add(key)) hidden.add(e.getId());
            }
            HIDDEN = hidden;
        });
    }
}
