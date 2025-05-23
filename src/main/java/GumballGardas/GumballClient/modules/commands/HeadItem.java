package GumballGardas.GumballClient.modules.commands;

import GumballGardas.GumballClient.utils.CHMainUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.screen.slot.SlotActionType;

public class HeadItem extends Command {
    public HeadItem() {
        super("head-item", "Allows you to put any item in your head slot.", "head");
    }

    public void build(LiteralArgumentBuilder builder) {
        builder.executes((context) -> {
            CHMainUtils.clickSlotPacket(mc.player.getInventory().selectedSlot + 36, 39, SlotActionType.SWAP);
            return 1;
        });
    }
}
