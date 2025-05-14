package GumballGardas.GumballClient.modules.general;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;

import GumballGardas.GumballClient.GumballClient;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class KillAuraLegit extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<TargetMode> targetMode = sgGeneral.add(new EnumSetting.Builder<TargetMode>()
        .name("Target Mode")
        .description("Which opponent should be targeted.")
        .defaultValue(TargetMode.Health)
        .build()
    );

    private final Setting<Integer> maxHp = sgGeneral.add(new IntSetting.Builder()
        .name("Max HP")
        .description("Target's health must be under this value.")
        .defaultValue(36)
        .min(0)
        .sliderMax(36)
        .build()
    );

    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("Delay")
        .description("Delay that will be used for hits.")
        .defaultValue(0.5)
        .min(0)
        .sliderMax(1)
        .build()
    );

    private final Setting<Boolean> onlyWeapon = sgGeneral.add(new BoolSetting.Builder()
        .name("Only Weapon")
        .description("Only attacks with a weapon.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> swing = sgGeneral.add(new BoolSetting.Builder()
        .name("Swing")
        .description("Renders swing animation when attacking an entity.")
        .defaultValue(true)
        .build()
    );

    private double timer = 0;
    private PlayerEntity target = null;

    public KillAuraLegit() {
        super(GumballClient.CATEGORY, "KillAuraPlus", "KillAura but Legit and better");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        timer += event.frameTime;
        if (timer < delay.get()) return;

        updateTarget();
        if (target == null) return;

        if (onlyWeapon.get()) {
            ItemStack hand = mc.player.getMainHandStack();
            if (!(hand.getItem() instanceof SwordItem || hand.getItem() instanceof AxeItem)) return;
        }

Vec3d pos = target.getEyePos();
double dx = pos.x - mc.player.getX();
double dy = pos.y - mc.player.getEyeY();
double dz = pos.z - mc.player.getZ();
double dist = Math.sqrt(dx * dx + dz * dz);

float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0F);
float pitch = (float) (-Math.toDegrees(Math.atan2(dy, dist)));

mc.player.setYaw(yaw);
mc.player.setPitch(pitch);

 

        mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
        if (swing.get()) mc.player.swingHand(Hand.MAIN_HAND);

        timer = 0;
    }

    private void updateTarget() {
        target = mc.world.getPlayers().stream()
            .filter(p -> p != mc.player && !p.isSpectator() && p.getHealth() > 0 && !Friends.get().isFriend(p))
            .filter(p -> p.getHealth() + p.getAbsorptionAmount() <= maxHp.get())
            .filter(p -> mc.player.squaredDistanceTo(p) <= 36)
            .min(Comparator.comparingDouble(this::targetPriority))
            .orElse(null);
    }

    private double targetPriority(PlayerEntity p) {
        return switch (targetMode.get()) {
            case Health -> p.getHealth() + p.getAbsorptionAmount();
            case Angle -> Math.abs(p.getYaw() - mc.player.getYaw());
            case Distance -> mc.player.squaredDistanceTo(p);
        };
    }

    public enum TargetMode {
        Health,
        Angle,
        Distance
    }
}
