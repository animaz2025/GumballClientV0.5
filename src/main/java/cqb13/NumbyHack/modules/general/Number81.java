package cqb13.NumbyHack.modules.general;

import cqb13.NumbyHack.NumbyHack;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * made by cqb13
 */
public class Number81 extends Module {

    private static final List<String> MESSAGES = Arrays.asList(
        "GumballClient is the best!",
        "Well done GumballGardas!",
        "Made by GumballGardas",
        "GumballClient on top!",
        "I don't need to play too well because there is GumballClient!"
    );

    public Number81() {
        super(NumbyHack.CATEGORY, "GumballClient", "GumballClient is the best!");
    }

    private int timer;
    private boolean setTimer;
    private final Random random = new Random();

    @Override
    public void onActivate() {
        timer = 200; // Timer for 10 seconds
    }

    @Override
    public void onDeactivate() {
        assert mc.player != null;
        var name = mc.player.getName();
        if (Objects.equals(name.toString(), "GumballGardas") || Objects.equals(name.toString(), "GumballClient")) {
            return;
        }
        ChatUtils.sendPlayerMsg("I don't need to play too well because there is GumballClient!");
        ChatUtils.sendPlayerMsg("Well done GumballGardas!");
        ChatUtils.sendPlayerMsg("Made by GumballGardas");
        ChatUtils.sendPlayerMsg("GumballClient is the best!");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (setTimer) {
            timer = 200; // Reset timer to 10 seconds
            setTimer = false;
        }

        timer--;
        if (timer <= 0) {
            // Randomly pick a message and send it
            String message = MESSAGES.get(random.nextInt(MESSAGES.size()));
            ChatUtils.sendPlayerMsg(message);
            setTimer = true;
        }
    }
}
