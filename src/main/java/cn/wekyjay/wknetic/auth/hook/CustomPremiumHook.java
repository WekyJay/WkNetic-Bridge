package cn.wekyjay.wknetic.auth.hook;

import org.bukkit.entity.Player;

public class CustomPremiumHook implements ILoginHook {
    @Override
    public boolean isHooked() {
        // 当 FastLogin 未安装时使用
        return true;
    }

    @Override
    public String getHookName() {
        return "CustomPremium";
    }

    // 检查玩家是否为正版
    public boolean isPremium(Player player) {
        // TODO: 实现自己的正版验证功能
        return false; // 暂不实现，返回 false
    }
}