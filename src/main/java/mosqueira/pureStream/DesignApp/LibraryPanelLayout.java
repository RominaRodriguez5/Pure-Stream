package mosqueira.pureStream.DesignApp;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import net.miginfocom.swing.MigLayout;
import mosqueira.pureStream.Paneles.LibraryPanel;

/**
 * Applies a responsive layout to {@link LibraryPanel} using MigLayout and a
 * split pane.
 *
 * <p>
 * Top bar: actions + filters + search. Center: tabs + split (table/list).</p>
 *
 * @author Romina
 * @version 1.0
 */
public class LibraryPanelLayout {

    /**
     * Reference to the library panel whose components are arranged and
     * configured by this layout helper.
     */
    private final LibraryPanel panel;

    /**
     * Creates a layout helper bound to the given panel.
     *
     * @param panel library panel whose components will be arranged
     */
    public LibraryPanelLayout(LibraryPanel panel) {
        this.panel = panel;
    }

    /**
     * Clears the panel and rebuilds its layout.
     */
    public void apply() {
        panel.removeAll();
        panel.setLayout(new BorderLayout(10, 10));

        // ===== TOP BAR (responsive) =====
        JPanel top = new JPanel(new MigLayout(
                "insets 8, fillx",
                "[]10[]10[]10[]push[]10[grow,fill]10[]",
                "[]"
        ));
        top.setOpaque(false);

        top.add(panel.getBtnBack());
        top.add(panel.getBtnDelete());
        top.add(panel.getBtnUploadToCloud());
        top.add(panel.getBtnDownloadFromCloud());

        top.add(panel.getComboFilter(), "wmin 140");
        top.add(panel.getTxtSearch(), "growx");
        top.add(panel.getBtnSearch(), "wmin 120");

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                panel.getScrollTable(),
                panel.getScrollList()
        );
        split.setResizeWeight(0.75);
        split.setOneTouchExpandable(true);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.setOpaque(false);
        center.add(panel.getTabs(), BorderLayout.NORTH);
        center.add(split, BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        panel.revalidate();
        panel.repaint();
    }
}
