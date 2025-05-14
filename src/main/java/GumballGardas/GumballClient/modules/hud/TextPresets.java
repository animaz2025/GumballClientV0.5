package GumballGardas.GumballClient.modules.hud;

import GumballGardas.GumballClient.GumballClient;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;

public class TextPresets {
    public static final HudElementInfo<TextHud> INFO = new HudElementInfo<>(GumballClient.HUD_GROUP, "numby-text", "Displays arbitrary text with Starscript.", TextPresets::create);

    static {
        addPreset("81", "Number81 on top!", 0);
        addPreset("Kills", "Kills: #1{GumballClient.kills}", 0);
        addPreset("Deaths", "Deaths: #1{GumballClient.deaths}", 0);
        addPreset("KDR", "KDR: #1{GumballClient.kdr}", 0);
        addPreset("Highscore", "Highscore: #1{GumballClient.highscore}", 0);
        addPreset("Killstreak", "Killstreak: #1{GumballClient.killstreak}", 0);
        addPreset("Crystals/s", "Crystals/s: #1{GumballClient.crystalsps}", 0);
        addPreset("Server Brand", "Server Brand: #1{GumballClient.brand}", 0);
    }

    private static TextHud create() {
        return new TextHud(INFO);
    }

    private static HudElementInfo<TextHud>.Preset addPreset(String title, String text, int updateDelay) {
        return INFO.addPreset(title, textHud -> {
            if (text != null) textHud.text.set(text);
            if (updateDelay != -1) textHud.updateDelay.set(updateDelay);
        });
    }
}
