package com.sheepion.dropname.listener;

import com.sheepion.dropname.DropName;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Logger;

public class AddNameToDrops implements Listener {
    private static final Logger logger = DropName.plugin.getLogger();
    //掉落物消失需要的tick，20tick=1s
    private static long itemDespawnTicks = 6000;

    public AddNameToDrops() {
        //添加循环事件来更新物品消失的倒计时
        DropName.plugin.getServer().getScheduler().runTaskTimer(DropName.plugin, () -> {
            for (World world : DropName.plugin.getServer().getWorlds()) {
                for (Item item : world.getEntitiesByClass(Item.class)) {
                    if (item.getItemStack().getItemMeta() == null) continue;
                    if (item.getTicksLived() >= itemDespawnTicks) {
                        item.remove();
                    }
                    addNameToDrop(item, item.getItemStack().getAmount());
                }
            }
        }, 60, 10);
    }

    //掉落物创建时添加名称
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemSpawn(ItemSpawnEvent event) {
        addNameToDrop(event.getEntity(), event.getEntity().getItemStack().getAmount());
    }

    //掉落物合并时添加名称
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemMerge(ItemMergeEvent event) {
        Item entity = event.getEntity();
        Item target = event.getTarget();
        target.setTicksLived(1);
        //此处需要传递合并以后的数量大小以保证物品名称中的数量正确显示
        addNameToDrop(target, entity.getItemStack().getAmount() + target.getItemStack().getAmount());
    }

    /**
     * 为掉落在地上的物品添加名称显示
     *
     * @param item   掉落在地上的物品
     * @param amount 物品的数量
     */
    public static void addNameToDrop(Item item, int amount) {
        //物品已经消失，返回
        if (item.getItemStack().getItemMeta() == null) return;
        ItemMeta itemMeta = item.getItemStack().getItemMeta();
        //设置自定义名称可见
        item.setCustomNameVisible(true);
        //需要添加的自定义名称
        String customName;
        //已经有display name则显示display name
        //没有display name则显示name
        if (itemMeta.hasDisplayName()) {
            customName = itemMeta.getDisplayName();
            //处理颜色代码
            customName = customName.replace("&", "§");
        } else {
            customName = item.getItemStack().getType().toString();
        }
        //添加物品数量
        if (amount > 1) {
            customName += " §ex" + amount + " ";
        }
        //添加物品消失倒计时
        customName += getTimeBeforeDespawn(item);
        //设置自定义名称
        item.setCustomName(customName);
    }

    public static String getTimeBeforeDespawn(Item item) {
        int seconds = (int) ((itemDespawnTicks - item.getTicksLived()) / 20);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("§6[§a%02d:%02d§6]§r", minutes, seconds);
    }
}
