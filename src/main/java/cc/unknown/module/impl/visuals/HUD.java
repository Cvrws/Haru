package cc.unknown.module.impl.visuals;

import static cc.unknown.ui.clickgui.EditHudPositionScreen.arrayListX;
import static cc.unknown.ui.clickgui.EditHudPositionScreen.arrayListY;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.RenderEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.ModuleInfo;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.ui.clickgui.EditHudPositionScreen;
import cc.unknown.ui.clickgui.raven.ClickGUI;
import cc.unknown.ui.clickgui.raven.impl.api.Theme;
import cc.unknown.utils.client.ColorUtil;
import cc.unknown.utils.font.FontUtil;
import cc.unknown.utils.misc.HiddenUtil;
import cc.unknown.utils.pos.Position;
import cc.unknown.utils.pos.PositionUtil;
import net.minecraft.client.gui.Gui;

@ModuleInfo(name = "HUD", category = Category.Visuals)
public class HUD extends Module {

	private ModeValue colorMode = new ModeValue("Theme", "Static", "Static", "Slinky", "Astolfo", "Primavera",
			"Ocean", "Theme");

	private ModeValue fontMode = new ModeValue("Font", "San Francisco", "Minecraft", "Montserrat", "Roboto");
	private SliderValue arrayColor = new SliderValue("Array Color [H/S/B]", 0, 0, 350, 10);
	private SliderValue saturation = new SliderValue("Saturation [H/S/B]", 1.0, 0.0, 1.0, 0.1);
	private BooleanValue editPosition = new BooleanValue("Edit Position", false);
	private BooleanValue noRenderModules = new BooleanValue("No Render Modules", true);
	private BooleanValue background = new BooleanValue("Background", true);
	private BooleanValue lowercase = new BooleanValue("Lowercase", false);
	public BooleanValue suffix = new BooleanValue("Suffix", false);

	public HUD() {
		this.registerSetting(fontMode, colorMode, arrayColor, saturation, editPosition, noRenderModules, background,
				lowercase, suffix);
	}

	@Override
	public void onEnable() {
		Haru.instance.getModuleManager().sort();
	}

	@Override
	public void guiButtonToggled(BooleanValue b) {
		if (b == editPosition) {
			editPosition.disable();
			mc.displayGuiScreen(new EditHudPositionScreen());
		}
	}

	@EventLink
	public void onDraw(RenderEvent e) {
		if (e.is2D()) {
			if (mc.gameSettings.showDebugInfo || mc.currentScreen instanceof ClickGUI) {
				return;
			}

			HiddenUtil.setVisible(!noRenderModules.isToggled());

			int margin = 2;
			AtomicInteger y = new AtomicInteger(arrayListY.get());

			if (Arrays.asList(Position.DOWNLEFT, Position.DOWNRIGHT).contains(PositionUtil.getPositionMode())) {
				Haru.instance.getModuleManager().sort();
			}

			List<Module> en = new ArrayList<>(Haru.instance.getModuleManager().getModule());
			if (en.isEmpty()) {
				return;
			}

			AtomicInteger textBoxWidth = new AtomicInteger(
					Haru.instance.getModuleManager().getLongestActiveModule(mc.fontRendererObj));
			AtomicInteger textBoxHeight = new AtomicInteger(
					Haru.instance.getModuleManager().getBoxHeight(mc.fontRendererObj, margin));

			if (arrayListX.get() < 0) {
				arrayListX.set(margin);
			}

			if (arrayListY.get() < 0) {
				arrayListY.set(margin);
			}

			arrayListX.set((arrayListX.get() + textBoxWidth.get() > mc.displayWidth / 2)
					? (mc.displayWidth / 2 - textBoxWidth.get() - margin)
					: arrayListX.get());

			arrayListY.set((arrayListY.get() + textBoxHeight.get() > mc.displayHeight / 2)
					? (mc.displayHeight / 2 - textBoxHeight.get())
					: arrayListY.get());

			AtomicInteger color = new AtomicInteger(0);

			en.stream().filter(m -> m.isEnabled() && m.isHidden()).sorted(Comparator.comparingDouble(module -> -textBoxWidth.get())).forEach(m -> {

				String nameOrSuffix = m.getModuleInfo().name();
				if (suffix.isToggled()) {
					nameOrSuffix += " §7" + m.getSuffix();
				}
				if (lowercase.isToggled()) {
					nameOrSuffix = nameOrSuffix.toLowerCase();
				}

				int fontHeightWithMargin = mc.fontRendererObj.FONT_HEIGHT + margin;

				switch (colorMode.getMode()) {
				case "Static":
					color.set(Color.getHSBColor((arrayColor.getInputToFloat() % 360) / 360.0f,
							saturation.getInputToFloat(), 1f).getRGB());
					break;
				case "Slinky":
					color.set(ColorUtil.reverseGradientDraw(new Color(255, 165, 128), new Color(255, 0, 255), y.get())
							.getRGB());
					break;
				case "Astolfo":
					color.set(ColorUtil.reverseGradientDraw(new Color(243, 145, 216), new Color(152, 165, 243),
							new Color(64, 224, 208), y.get()).getRGB());
					break;
				case "Primavera":
					color.set(ColorUtil.reverseGradientDraw(new Color(0, 206, 209), new Color(255, 255, 224),
							new Color(211, 211, 211), y.get()).getRGB());
					break;
				case "Ocean":
					color.set(ColorUtil.reverseGradientDraw(new Color(0, 0, 128), new Color(0, 255, 255),
							new Color(173, 216, 230), y.get()).getRGB());
					break;
				case "Theme":
					color.set(Theme.getMainColor().getRGB());
					break;
				}

				y.addAndGet(fontHeightWithMargin);

				int fontHeight = mc.fontRendererObj.FONT_HEIGHT + 2;
				int stringWidth;
				if (fontMode.is("Roboto")) {
				    stringWidth = (int) FontUtil.roboto.getStringWidth(nameOrSuffix);
				} else if (fontMode.is("Montserrat")) {
				    stringWidth = (int) FontUtil.montserrat.getStringWidth(nameOrSuffix);
				} else {
				    stringWidth = mc.fontRendererObj.getStringWidth(nameOrSuffix);
				}

				int positionOffset = (PositionUtil.getPositionMode() == Position.DOWNRIGHT || PositionUtil.getPositionMode() == Position.UPRIGHT) ? 5 : 4;
				int backgroundWidth = stringWidth + positionOffset;

				boolean isRightAligned = PositionUtil.getPositionMode() == Position.DOWNRIGHT || PositionUtil.getPositionMode() == Position.UPRIGHT;
				int x1 = isRightAligned ? arrayListX.get() + textBoxWidth.get() + 4 : arrayListX.get() - 3;
				int x2 = isRightAligned ? arrayListX.get() + (textBoxWidth.get() - backgroundWidth) : arrayListX.get() + backgroundWidth;

				float xPos = isRightAligned ? arrayListX.get() + (textBoxWidth.get() - stringWidth) : arrayListX.get();
				int backgroundColor = background.isToggled() 
					    ? new Color(0, 0, 0, 100).getRGB() 
					    : new Color(0, 0, 0, 87).getRGB();
				
				if (background.isToggled()) {
				    Gui.drawRect(x1, y.get(), x2, y.get() + fontHeight, backgroundColor);
				}

				if (fontMode.is("Roboto")) {
				    FontUtil.roboto.drawStringWithShadow(nameOrSuffix, xPos, y.get() + 2, color.get());
				} else if (fontMode.is("Montserrat")) {
				    FontUtil.montserrat.drawStringWithShadow(nameOrSuffix, xPos, y.get() + 2, color.get());
				} else {
				    mc.fontRendererObj.drawStringWithShadow(nameOrSuffix, xPos, y.get() + 2, color.get());
				}
			});
		}
	}
}
