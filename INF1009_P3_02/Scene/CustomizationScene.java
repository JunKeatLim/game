package INF1009_P3_02.Scene;

import INF1009_P3_02.BackgroundChoice;
import INF1009_P3_02.SettingsData;
import INF1009_P3_02.SimulationConfig;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class CustomizationScene extends Scene {

    private final SceneManager sceneManager;
    private final SettingsData settings;

    public CustomizationScene(SceneManager sceneManager, SettingsData settings) {
        this.sceneManager = sceneManager;
        this.settings = settings;
    }

    private <T> SelectBox<T> createBigSelectBox() {
        SelectBox<T> box = new SelectBox<>(skin);

        // Clone default style
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle(box.getStyle());
        style.listStyle = new List.ListStyle(style.listStyle);
        style.scrollStyle = new ScrollPane.ScrollPaneStyle(style.scrollStyle);

        // Scale font (only for this SelectBox style)
        style.font.getData().setScale(1.5f);

        // Keep closed box original, but make opened dropdown list more visible
        style.scrollStyle.background = skin.getDrawable("button-hover");
        style.listStyle.selection = skin.getDrawable("button-disabled");

        box.setStyle(style);
        return box;
    }

    @Override
    protected void buildUI() {
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Title
        Label title = new Label("CUSTOMIZATION", skin, "title");

        // Labels
        Label bgLabel = new Label("Select Difficulty", skin);
        bgLabel.setFontScale(2.0f);

        // Select boxes
        SelectBox<BackgroundChoice> bgSelect = createBigSelectBox();
        bgSelect.setItems(
            BackgroundChoice.SCHOOL_BASKETBALL,
            BackgroundChoice.SCHOOL_CANTEEN,
            BackgroundChoice.SCHOOL_PARK
        );

        // Buttons
        TextButton begin = new TextButton("Begin", skin);
        TextButton back = new TextButton("Back", skin);

        begin.getLabel().setFontScale(1.5f);
        back.getLabel().setFontScale(1.5f);

        // Layout
        root.add(title).pad(20).row();

        root.add(bgLabel).padTop(10).row();
        root.add(bgSelect).width(650).height(90).pad(12).row();

        root.add(begin).width(520).height(90).padTop(12).row();
        root.add(back).width(520).height(90).padTop(12).row();

        // Behaviour
        begin.addListener(e -> {
            if (!begin.isPressed()) return false;
            System.out.println("Button pressed: Begin");

            SimulationConfig cfg = new SimulationConfig();
            cfg.background = bgSelect.getSelected();

            // SceneManager owns navigation + stores config
            sceneManager.startSimulation(cfg);
            return true;
        });

        back.addListener(e -> {
            if (!back.isPressed()) return false;
            System.out.println("Button pressed: Back");
            sceneManager.goToMainMenu();
            return true;
        });
    }
}
