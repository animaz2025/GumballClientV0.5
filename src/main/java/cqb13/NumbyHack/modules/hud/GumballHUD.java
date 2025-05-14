package cqb13.NumbyHack.modules.hud;

import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.*;
import net.minecraft.util.Identifier;

import cqb13.NumbyHack.NumbyHack;

public class GumballHUD extends HudElement {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Double> scale = sgGeneral.add(new DoubleSetting.Builder()
        .name("scale")
        .description("The scale of the HUD.")
        .defaultValue(1.0)
        .min(0.1)
        .sliderRange(0.1, 5)
        .build()
    );
    
    private final Setting<SettingColor> nameColor = sgGeneral.add(new ColorSetting.Builder()
        .name("name-color")
        .description("The color of the client name.")
        .defaultValue(new SettingColor(0, 255, 255)) // Cyan
        .build()
    );
    
    private final Setting<SettingColor> versionColor = sgGeneral.add(new ColorSetting.Builder()
        .name("version-color")
        .description("The color of the version text.")
        .defaultValue(new SettingColor(255, 255, 255)) // White
        .build()
    );

    public static final HudElementInfo<GumballHUD> INFO = new HudElementInfo<>(
        NumbyHack.HUD_GROUP,
        "gumball-watermark",
        "Displays the GumballClient watermark at the top of your screen.",
        GumballHUD::new
    );

    private static final String CLIENT_NAME = "GumballClient";
    private static final String VERSION = "V0.2";
    private static final String TEXT = CLIENT_NAME + " " + VERSION;

    public GumballHUD() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        // Calculate width and height based on scale
        double textWidth = renderer.textWidth(TEXT);
        double textHeight = renderer.textHeight();
        double width = textWidth * scale.get();
        double height = textHeight * scale.get();

        // Set box boundaries
        box.setSize(width, height);

        // Renderer for client name with cyan color
        double x = box.x;
        double y = box.y;
        
        // Calculate name width for version positioning
        double nameWidth = renderer.textWidth(CLIENT_NAME) * scale.get();
        
        // Render client name in cyan
        renderer.text(CLIENT_NAME, x, y, nameColor.get(), true, scale.get());
        
        // Render version in white right after the name
        renderer.text(" " + VERSION, x + nameWidth, y, versionColor.get(), true, scale.get());
    }
}