package mosqueira.pureStream.DesignApp;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import mosqueira.pureStream.Paneles.MainPanel;

public class MainPanelLayout {

    private final MainPanel panel;

    public MainPanelLayout(MainPanel panel) {
        this.panel = panel;
    }

    public void apply() {
        panel.removeAll();
        panel.setLayout(new BorderLayout(10, 10));

        JPanel top = new JPanel(new MigLayout(
                "insets 8, fillx, wrap 3",
                "[right]10[grow, fill]10[]",
                "[]8[]8[]8[]"
        ));
        top.setOpaque(false);

        // Row 0: URL (SIN botón Download aquí)
        top.add(panel.getLblUrl());
        top.add(panel.getTxtUrl(), "span 2, growx");

        // Row 1: Output format + quality
        top.add(panel.getLblFormat());
        JPanel formatRow = new JPanel(new MigLayout("insets 0, fillx", "[grow,fill]10[grow,fill]", "[]"));
        formatRow.setOpaque(false);
        formatRow.add(panel.getComboFormat(), "growx");
        formatRow.add(panel.getComboQuality(), "growx");
        top.add(formatRow, "span 2, growx");

        // Row 2: Folder
        top.add(panel.getLblFolder());
        top.add(panel.getTxtFolder(), "growx");
        top.add(panel.getBtnBrowseFolder(), "wmin 140");

        // Bottom actions (Download aquí)
        JPanel bottom = new JPanel(new MigLayout("insets 8, fillx", "[left]push[right]10[right]", "[]"));
        bottom.setOpaque(false);

        bottom.add(panel.getBtnPlay(), "left");
        bottom.add(panel.getBtnDownload(), "right, wmin 160");
        bottom.add(panel.getBtnOpenLibrary(), "right");

        panel.add(top, BorderLayout.NORTH);
        panel.add(panel.getScrollLog(), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        panel.revalidate();
        panel.repaint();
    }

}
