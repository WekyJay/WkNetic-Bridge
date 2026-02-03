package cn.wekyjay.wknetic.bridge;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class BridgeClientHandler extends SimpleChannelInboundHandler<String> {

    private final WkNeticBridge plugin;

    public BridgeClientHandler(WkNeticBridge plugin) {
        this.plugin = plugin;
    }

    /**
     * 处理来自后端的消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // ⚠️ 此时在 Netty 线程，禁止操作 Bukkit API (如 player.sendMessage)

        // 1. 解析消息
        JsonObject json = JsonParser.parseString(msg).getAsJsonObject();
        String type = json.get("type").getAsString();

        // 2. 调度回主线程 (Thread Switching)
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                processMessage(type, json);
            } catch (Exception e) {
                plugin.getLogger().severe("处理后端消息时出错: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    /**
     * 处理具体的消息类型
     * @param type
     * @param json
     */
    private void processMessage(String type, JsonObject json) {
        plugin.getLogger().info("Received command from backend: " + type);
        if ("214".equals(type)) {
            // 执行控制台指令
            String cmd = json.get("command").getAsString();
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        } 
        else if ("GIVE_ITEM".equals(type)) {
            // 🎁 演示 XSeries 的全版本兼容能力
            String playerName = json.get("player").getAsString();
            String matName = json.get("material").getAsString(); // 例如 "DIAMOND_SHOVEL"

            Player player = Bukkit.getPlayer(playerName);
            if (player != null) {
                // 自动适配: 1.8 变成 SPADE, 1.13+ 变成 SHOVEL
                Optional<XMaterial> xMat = XMaterial.matchXMaterial(matName);
                if (xMat.isPresent()) {
                    ItemStack item = xMat.get().parseItem();
                    player.getInventory().addItem(item);
                    player.sendMessage("§a[WkNetic] §f你获得了: " + matName);
                } else {
                    plugin.getLogger().warning("未知的物品材质: " + matName);
                }
            }
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        plugin.getLogger().warning("⚠️ 与服务器断开连接!");
        // 触发重连
        Bukkit.getScheduler().runTaskLater(plugin, () -> 
            plugin.getNetworkManager().connect(), 60L);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 生产环境可以忽略这个异常，因为通常是断线引起的
    }
}