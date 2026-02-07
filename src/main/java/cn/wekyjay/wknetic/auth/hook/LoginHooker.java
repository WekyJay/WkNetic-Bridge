package cn.wekyjay.wknetic.auth.hook;

import java.util.HashMap;
import cn.wekyjay.wknetic.bridge.WkNeticBridge;

/** 聚合登录钩子，负责管理和注册所有登录钩子 */
public class LoginHooker {
    private final static HashMap<String, ILoginHook> registeredHooks = new HashMap<>();

    public static HashMap<String, ILoginHook> getRegisteredhooks() {
        return registeredHooks;
    }


    public static void registerHook(ILoginHook hook) {
        if (hook != null && hook.isHooked()) {
            registeredHooks.put(hook.getHookName(), hook);
        }
    }

    public static ILoginHook getHookByName(String name) {
        return registeredHooks.getOrDefault(name, null);
    }


    public LoginHooker() {
        // 显式注册所有已知的登录钩子
        registerHook(new FastLoginHook());
        registerHook(new AuthmeHook());
        registerHook(new CustomLoginHook());
        registerHook(new CustomPremiumHook());
        // 遍历已注册的钩子并注册事件
        for (ILoginHook hook : registeredHooks.values()) {
            WkNeticBridge.getInstance().getLogger().info("Registering events for hook: " + hook.getHookName());
        }
    }



}
