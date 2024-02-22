package cc.unknown.module.impl.visuals;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.ui.clickgui.raven.ClickGui;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class ClickGuiModule extends Module {
	
    public ModeValue clientTheme = new ModeValue("Color", "Static", "RGB", "Pastel", "Memories", "Static");
    public ModeValue waifuMode = new ModeValue("Waifu", "Astolfo", "Astolfo", "Hideri", "Gwen", "Kurumi", "Uzaki", "Rem", "Loona", "Megumi", "Magic", "Typh", "None");
    public BooleanValue gradient = new BooleanValue("Gradient Background", true);
    
    private final KeyBinding[] moveKeys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint, mc.gameSettings.keyBindSneak};

    public ClickGuiModule() {
        super("ClickGui", ModuleCategory.Visuals);
        this.registerSetting(clientTheme, waifuMode, gradient);
        this.withKeycode(54, ClickGuiModule.class);
    }
    
    @Override
    public void onEnable() {
    	if (PlayerUtil.inGame() && mc.currentScreen != Haru.instance.getClickGui()) {
    		mc.displayGuiScreen(Haru.instance.getClickGui());
    	}
    }
    
    @Override
    public void onDisable() {
    	if (PlayerUtil.inGame() && mc.currentScreen instanceof ClickGui) {
    		mc.displayGuiScreen(null);
    	}
    }

    @EventLink
    public void onTick(TickEvent e) {
        for (KeyBinding bind : moveKeys) {
            bind.pressed = GameSettings.isKeyDown(bind);
        }
    }
}