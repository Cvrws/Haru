package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.utils.client.RenderUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class PetCommand extends Command {

    private boolean toggle = false;
    private int image;
    private int running;
	private int deltaTime;
    private int height = 80;

    private ResourceLocation[] catTextures = new ResourceLocation[12];

    public PetCommand() {
        Haru.instance.getEventBus().register(this);
        for (int i = 0; i < 12; i++) {
            catTextures[i] = new ResourceLocation("haru/img/pet/cat/" + (i + 1) + ".png");
        }
    }

    @Override
    public void onExecute(String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("cat")) {
                toggle = !toggle;
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("cat") && args[1].equalsIgnoreCase("height")) {
            try {
                height = Integer.parseInt(args[2]);
                PlayerUtil.send(getGreen() + " Height set to " + height);
            } catch (NumberFormatException e) {
                PlayerUtil.send(getRed() + " Invalid Integer");
            }
        } else {
            PlayerUtil.send(getRed() + " Syntax Error. Use .pet cat height <val>");
        }
    }

    @EventLink
    public void onRender2D(Render2DEvent e) {
        if (!toggle)
            return;
        running += 0.015f * deltaTime;
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        running++;
        RenderUtil.drawImage(catTextures[image], (int) running, scaledResolution.getScaledHeight() - height, 64, 32);
        if (scaledResolution.getScaledWidth() <= running)
            running = -64;
    }

    @EventLink
    public void onUpdate(UpdateEvent e) {
        if (!toggle)
            return;
        image = (image + 1) % 12;
    }

	@Override
	public String getName() {
		return "pet";
	}

	@Override
	public String getSyntax() {
		return ".pet <vpet>";
	}

	@Override
	public String getDesc() {
		return "Your own virtual pet";
	}
}