package mosqueira.pureStream.DesignApp;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import mosqueira.pureStream.Paneles.PreferencesPanel;

/**
 * Applies a responsive layout to {@link PreferencesPanel} using MigLayout.
 *
 * <p>Layout structure:</p>
 * <ul>
 *   <li><strong>Center</strong>: title, yt-dlp path selector, separator,
 *       options (limit rate / create M3U), slider and value label.</li>
 *   <li><strong>South</strong>: save/return button.</li>
 * </ul>
 *
 * @author Romina
 * @version 1.0
 */
public class PreferencesPanelLayout {

    private final PreferencesPanel panel;

    /**
     * Creates a layout helper bound to the given preferences panel.
     *
     * @param panel preferences panel whose components will be arranged
     */
    public PreferencesPanelLayout(PreferencesPanel panel) {
        this.panel = panel;
    }

    /**
     * Clears the panel and rebuilds its layout.
     */
    public void apply() {
        panel.removeAll();
        panel.setLayout(new BorderLayout(10, 10));

        JPanel center = new JPanel(new MigLayout(
                "insets 20, fillx, wrap 3",
                "[right]15[grow, fill]15[]",
                "[]20[]20[]20[]20[]"
        ));
        center.setOpaque(false);

        center.add(panel.getLblAds(), "span 3, center, gapbottom 25");

        center.add(panel.getLblSelectPath());
        center.add(panel.getTxtExecutable(), "growx");
        center.add(panel.getBtnSearchPath(), "wmin 140");

        center.add(panel.getSeparator(), "span 3, growx, gaptop 20, gapbottom 20");

        JPanel checks = new JPanel(new MigLayout("insets 0, fillx", "[]push[]", "[]"));
        checks.setOpaque(false);
        checks.add(panel.getChkLimit(), "left");
        checks.add(panel.getChkM3U(), "right");

        center.add(checks, "span 3, growx, gapbottom 20");

        center.add(panel.getSliderLimit(), "span 3, growx, gaptop 15");
        center.add(panel.getLblValue(), "span 3, center, gaptop 10");

        JPanel south = new JPanel(new MigLayout("insets 10, fillx", "[center]", "[]"));
        south.setOpaque(false);
        south.add(panel.getBtnSaveAndReturn(), "center, wmin 180");

        panel.add(center, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);

        panel.revalidate();
        panel.repaint();
    }
}