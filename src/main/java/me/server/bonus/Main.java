package me.server.bonus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    @Override
    public void onEnable() {
        getCommand("bonus").setExecutor(this);
        getCommand("lefttime").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("group.hero")) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + p.getName() + " 500");
                    p.sendMessage("§b[Зарплата] §fВы получили §e500 монет §fкак §lHERO§f!");
                }
            }
        }, 72000L, 72000L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;

        if (label.equalsIgnoreCase("bonus")) {
            openMenu(p);
        } else if (label.equalsIgnoreCase("lefttime")) {
            if (p.hasPermission("bonus.admin")) {
                long mins = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
                p.sendMessage("§a[Админ] Ваше время: §e" + mins + " мин.");
            }
        }
        return true;
    }

    public void openMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, "§0Награды за онлайн");
        long mins = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;

        inv.setItem(10, createItem(Material.IRON_INGOT, "§e5 Минут", mins >= 5, "1000 монет"));
        inv.setItem(13, createItem(Material.GOLD_INGOT, "§e30 Минут", mins >= 30, "5000 монет"));
        inv.setItem(16, createItem(Material.NETHERITE_CHESTPLATE, "§b§lHERO", mins >= 120, "Привилегия HERO"));

        p.openInventory(inv);
    }

    private ItemStack createItem(Material mat, String name, boolean ready, String reward) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add("§7Награда: §a" + reward);
        lore.add(ready ? "§a✔ Можно забрать!" : "§c✖ Еще не отыграно");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("§0Награды за онлайн")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;

            Player p = (Player) e.getWhoClicked();
            long mins = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200;
            int slot = e.getSlot();

            if (slot == 10 && mins >= 5) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "eco give " + p.getName() + " 1000");
                p.closeInventory();
            } else if (slot == 16 && mins >= 120) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent set hero");
                p.sendMessage("§b[Бонус] §fВы получили статус §lHERO§f!");
                p.closeInventory();
            }
        }
    }
}
