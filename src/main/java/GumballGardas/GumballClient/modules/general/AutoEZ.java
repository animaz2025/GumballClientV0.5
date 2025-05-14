package GumballGardas.GumballClient.modules.general;

import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

import GumballGardas.GumballClient.GumballClient;

public class AutoEZ extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> messageDelay = sgGeneral.add(new IntSetting.Builder()
            .name("message-delay")
            .description("How long to wait before sending the message (in ticks).")
            .defaultValue(20)
            .min(0)
            .sliderMax(100)
            .build()
    );

    private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("Don't send messages if the killed player is your friend.")
            .defaultValue(true)
            .build()
    );

    private final List<String> messages = Arrays.asList(
            "EZ %s, better luck next time",
            "I'm sorry %s, but you were no match for me",
            "Get rekt %s",
            "GG EZ %s",
            "%s just got destroyed",
            "Maybe try harder next time %s?",
            "That was too easy %s",
            "Ez clap %s",
            "You need more practice %s",
            "Outplayed %s",
            "How did that feel %s?",
            "%s just got deleted",
            "No chance %s",
            "Better gaming chair wins %s",
            "Skill issue %s"
    );

    private final HashMap<UUID, Integer> deadPlayers = new HashMap<>();
    private final List<UUID> needsRemoval = new ArrayList<>();
    private final Random random = new Random();

    public AutoEZ() {
        super(GumballClient.CATEGORY, "auto-ez", "Automatically sends a toxic message when you kill someone.");
    }

    @Override
    public void onActivate() {
        deadPlayers.clear();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        // Check for players that need messages and decrement delay
        deadPlayers.forEach((uuid, delay) -> {
            if (delay <= 0) {
                Entity entity = getEntityByUUID(uuid);
                if (entity instanceof PlayerEntity player) {
                    sendKillMessage(player.getName().getString());
                }
                needsRemoval.add(uuid);
            } else {
                deadPlayers.put(uuid, delay - 1);
            }
        });

        // Remove players that have been messaged
        needsRemoval.forEach(deadPlayers::remove);
        needsRemoval.clear();

        // Check for dead players
        if (mc.world == null || mc.player == null) return;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (player == mc.player) continue;
            if (ignoreFriends.get() && Friends.get().isFriend(player)) continue;
            
            // Check if player is dead
            if (player.getHealth() <= 0 && !deadPlayers.containsKey(player.getUuid())) {
                deadPlayers.put(player.getUuid(), messageDelay.get());
            }
        }
    }

    @EventHandler
    private void onGameLeft(GameLeftEvent event) {
        deadPlayers.clear();
    }

    private Entity getEntityByUUID(UUID uuid) {
        if (mc.world == null) return null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity.getUuid().equals(uuid)) return entity;
        }
        return null;
    }

    private void sendKillMessage(String playerName) {
        if (mc.player == null) return;
        
        // Get random message and format with player name
        String message = messages.get(random.nextInt(messages.size()));
        message = String.format(message, playerName);
        
        // Send message to chat
        mc.player.networkHandler.sendChatMessage(message);
    }
}