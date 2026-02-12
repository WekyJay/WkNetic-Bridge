package cn.wekyjay.wknetic.bridge;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cn.wekyjay.wknetic.api.enums.PacketType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.bukkit.Bukkit;


/**
 * 处理来自后端的消息
 */
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

        PacketType packetType = PacketType.getByName(json.get("type").getAsString());

        // 2. 调度回主线程 (Thread Switching)
        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                processMessage(packetType, json);
            } catch (Exception e) {
                plugin.getLogger().severe("处理后端消息时出错: " + e.getMessage());
            }
        });
    }
    


    /**
     * 处理具体的消息类型
     * @param type
     * @param json
     */
    private void processMessage(PacketType type, JsonObject json) {
  
        switch (type) {
            case ADMIN_COMMAND:
                handleAdminCommand(json);
                break;
            case SERVER_LOGIN_RESP:
                // 处理登录响应
                boolean success = json.get("success").getAsBoolean();
                if (success) {
                    plugin.getLogger().info("WkNetic服务器: ✅ 成功通过后端认证!");
                } else {
                    String reason = json.get("reason").getAsString();
                    plugin.getLogger().severe("WkNetic服务器: ❌ 认证失败: " + reason);
                }
                break;
            case SERVER_RESP:
                // 处理服务器信息响应
                String info = json.get("message").getAsString();
                plugin.getLogger().info("WkNetic服务器: " + info);
                break;
            default:
                break;
        }

    }

// if ("GIVE_ITEM".equals(type)) {
//             // 🎁 演示 XSeries 的全版本兼容能力
//             String playerName = json.get("player").getAsString();
//             String matName = json.get("material").getAsString(); // 例如 "DIAMOND_SHOVEL"

//             Player player = Bukkit.getPlayer(playerName);
//             if (player != null) {
//                 // 自动适配: 1.8 变成 SPADE, 1.13+ 变成 SHOVEL
//                 Optional<XMaterial> xMat = XMaterial.matchXMaterial(matName);
//                 if (xMat.isPresent()) {
//                     ItemStack item = xMat.get().parseItem();
//                     player.getInventory().addItem(item);
//                     player.sendMessage("§a[WkNetic] §f你获得了: " + matName);
//                 } else {
//                     plugin.getLogger().warning("未知的物品材质: " + matName);
//                 }
//             }
//         }
//     }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        plugin.getLogger().warning("WkNetic服务器: ⚠️ 与服务器断开连接!");
        // 触发重连
        Bukkit.getScheduler().runTaskLater(plugin, () -> 
            plugin.getNetworkManager().connect(), 60L); // 3 秒后重连
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // 生产环境可以忽略这个异常，因为通常是断线引起的
    }


    /**
     * 处理管理员指令
     * @param json
     */
    public void handleAdminCommand(JsonObject json) {
        String cmd = json.get("command").getAsString();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }
}