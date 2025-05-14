package cqb13.NumbyHack.modules.general;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import cqb13.NumbyHack.NumbyHack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PingSpoofer extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> fakePing = sgGeneral.add(new IntSetting.Builder()
            .name("fake-ping")
            .description("The fake ping to display to other players.")
            .defaultValue(31)
            .min(0)
            .sliderMax(10000)
            .build()
    );

    public PingSpoofer() {
        super(NumbyHack.CATEGORY, "ping-spoofer", "Spoofs your ping to a specified value.");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (!(event.packet instanceof PlayerListS2CPacket)) return;
        
        PlayerListS2CPacket packet = (PlayerListS2CPacket) event.packet;
        
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        
        try {
            // Get all entries from the packet
            List<PlayerListS2CPacket.Entry> entries = new ArrayList<>(packet.getEntries());
            
            // Check if there's an entry that matches our player
            boolean modified = false;
            for (int i = 0; i < entries.size(); i++) {
                PlayerListS2CPacket.Entry entry = entries.get(i);
                
                if (mc.player.getUuid().equals(entry.profileId())) {
                    // Use reflection to modify the ping value in the entry
                    try {
                        // Access the latency field in the entry
                        Field latencyField = findLatencyField(entry);
                        if (latencyField != null) {
                            latencyField.setAccessible(true);
                            latencyField.setInt(entry, fakePing.get());
                            modified = true;
                        }
                    } catch (Exception e) {
                        NumbyHack.Log("Error modifying ping: " + e.getMessage());
                    }
                }
            }
            
            if (modified) {
                NumbyHack.Log("Successfully spoofed ping to " + fakePing.get() + "ms");
            }
        } catch (Exception e) {
            NumbyHack.Log("Error in PingSpoofer: " + e.getMessage());
        }
    }
    
    private Field findLatencyField(PlayerListS2CPacket.Entry entry) {
        // Try to find the latency field using reflection
        Field[] fields = entry.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == int.class) {
                field.setAccessible(true);
                try {
                    // Check if this might be the latency field (usually contains a reasonable ping value)
                    int value = field.getInt(entry);
                    if (value >= 0 && value < 10000) {
                        return field;
                    }
                } catch (Exception ignored) {}
            }
        }
        return null;
    }
}