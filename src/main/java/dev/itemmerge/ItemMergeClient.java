package dev.itemmerge;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemMergeClient implements ClientModInitializer {
    // Item cùng loại trong bán kính này chỉ hiện 1 cái.
    public static final double RADIUS = 8.0;
    // Chỉ xử lý item trong phạm vi này quanh người chơi.
    public static final double RANGE = 64.0;
    // Cập nhật mỗi N tick.
    public static final int INTERVAL = 4;

    public static volatile IntOpenHashSet HIDDEN = new IntOpenHashSet();

    private record Key(Item item, ComponentMap comps) {}

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
            Map<Key, List<ItemEntity>> visible = new HashMap<>();
            double r2 = RADIUS * RADIUS;

            for (ItemEntity e : client.world.getEntitiesByClass(
                    ItemEntity.class,
                    client.player.getBoundingBox().expand(RANGE),
                    x -> true)) {
                ItemStack stack = e.getStack();
                if (stack.isEmpty() || stack.getMaxCount() <= 1) continue;

                List<ItemEntity> list = visible.computeIfAbsent(
                        new Key(stack.getItem(), stack.getComponents()),
                        k -> new ArrayList<>());

                boolean near = false;
                for (ItemEntity v : list) {
                    if (v.squaredDistanceTo(e) <= r2) { near = true; break; }
                }
                if (near) hidden.add(e.getId());
                else list.add(e);
            }
            HIDDEN = hidden;
        });
    }
}
