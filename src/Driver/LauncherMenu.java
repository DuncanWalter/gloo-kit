package Driver;

import GlooKit.GlooAPI.GlooBatch;
import GlooKit.GlooAPI.Texture;
import GlooKit.GlooFramework.*;
import GlooKit.GlooFramework.Components.Label;
import GlooKit.GlooFramework.Components.Rect;

import static GlooKit.GlooAPI.GlooCore.DEFAULT;
import static GlooKit.GlooAPI.GlooCore.TEXT;
import static GlooKit.GlooAPI.GlooFontFamily.STANDARD_TEXT_SET;
import static GlooKit.GlooFramework.KitBit.*;

public class LauncherMenu {
    public static void open(GlooApplication app){

        GlooBatch defaultBatch;
        defaultBatch = app.getBatch(DEFAULT);
        defaultBatch.addTexture("assets");
        defaultBatch.bindTextures();

        TextBatch textBatch;
        textBatch = (TextBatch) app.getBatch(TEXT);
        textBatch.addFont("assets/fonts", STANDARD_TEXT_SET, 72);
        textBatch.getFontFamilies().forEach(System.out::println);
        textBatch.bindTextures();

        Texture blue       = defaultBatch.getTexture("Blue");
        Texture fullscreen = defaultBatch.getTexture("fullscreen");
        Texture windowed   = defaultBatch.getTexture("windowed");
        Texture fontAtlas  = textBatch.getTexture("atlas");

        GlooApplicationConfiguration config;
        config = new GlooApplicationConfiguration("Game.cfg");

        Button fullscreenToggleButton = new Button(CENTER, CENTER, "1/n+", "1/n+", new Rect(config.isFullscreen() ? windowed : fullscreen, DEFAULT), null, null);
        fullscreenToggleButton.setAction(()->{
            config.setFullscreen(!config.isFullscreen());
            config.save();
            fullscreenToggleButton.setFrame(new Rect(config.isFullscreen() ? windowed : fullscreen, DEFAULT));
        });

        Runnable runGame = ()->{
            GlooApplication.runConcurrent("Game", MainMenu::open);
            app.close();
        };

        new Room(app
//                , new Canvas(CENTER, CENTER, "1/n+", "1/n+", new Rect(fontAtlas, TEXT))
                , new RowSpan(
                    new ColSpan(
                         new Canvas(LEFT, CENTER, "1/n", "0.5w", new Rect(blue, DEFAULT))
                        ,new Canvas(LEFT, CENTER, "1/n", "equal", new Rect(blue, DEFAULT))
                        ,new Canvas(LEFT, CENTER, "1/n", "equal", new Rect(blue, DEFAULT))
                    )
                    ,new ColSpan(
                         new Canvas(LEFT, CENTER, "1/n", "1/2", new Rect(blue, DEFAULT))
                        ,new Canvas(LEFT, CENTER, "1/n", "1/2", new Rect(blue, DEFAULT))
                    )
                    ,new ColSpan(
                         new Canvas(LEFT, CENTER, "1/n", "1/n", new Rect(blue, DEFAULT))
                        ,new Canvas(LEFT, CENTER, "equal", "70p", new Rect(blue, DEFAULT))
                        ,new Canvas(LEFT, CENTER, "1/n", "1/n", new Rect(blue, DEFAULT))
                    )
                )
                , new Modal(CENTER, CENTER, "250p", "250p", app
                    , fullscreenToggleButton)
                , new Button(CENTER, BOTTOM, "200p", "wraps", new Rect(app, 0.1f, 0.2f, 0.4f, 1.0f), new Label(app, "Alte Haas Grotesk", "PLAY"), runGame)
        ).configure();

    }
}
