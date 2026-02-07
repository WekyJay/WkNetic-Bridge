package cn.wekyjay.wknetic.bridge;


import com.google.gson.JsonObject;

import cn.wekyjay.wknetic.api.enums.PacketType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class PLayerChatListener implements Listener {
    private final Plugin plugin;


    PLayerChatListener (Plugin plugin) {
        this.plugin = plugin;
    }



    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncPlayerChatEvent e) {

        // 1. 构造 JSON
        JsonObject json = new JsonObject();
        json.addProperty("type", PacketType.CHAT_MSG.toString()); // 类型 3 = 聊天
        json.addProperty("player", e.getPlayer().getName());
        json.addProperty("msg", e.getMessage());
        json.addProperty("server", plugin.getConfig().getString("Common.server-name", Bukkit.getServer().getName() + "-" + Bukkit.getServer().getVersion()));
        json.addProperty("uuid", e.getPlayer().getUniqueId().toString());
        json.addProperty("world", e.getPlayer().getWorld().getName());
        json.addProperty("time", System.currentTimeMillis());

        // 2. 【异步发送】通过 Netty 发送
        // NetworkManager 内部是 channel.writeAndFlush，线程安全的
        WkNeticBridge.getInstance().getNetworkManager().send(json.toString());
    }
}
