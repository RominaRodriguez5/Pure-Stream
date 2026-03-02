package mosqueira.pureStream.DesignApp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import mosqueira.pureStream.Modelo.MediaFile;

/**
 * Custom {@link ListCellRenderer} for {@link MediaFile} objects.
 *
 * <p>Renders:</p>
 * <ul>
 *   <li>File icon (left)</li>
 *   <li>Title + network state (center)</li>
 *   <li>Play icon (right) only for LOCAL/BOTH items</li>
 * </ul>
 *
 * @author Romina
 * @version 1.0
 */
public class MediaFileListRenderer extends JPanel implements ListCellRenderer<MediaFile> {

       /**
     * Serialization identifier for this renderer component.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Label used to display the media type icon.
     */
    private final JLabel lblIcon = new JLabel();

    /**
     * Label used to display the media title.
     */
    private final JLabel lblTitle = new JLabel();

    /**
     * Label used to display the current state
     * (e.g., downloaded, pending, playing).
     */
    private final JLabel lblState = new JLabel();

    /**
     * Label used to display the play indicator icon.
     */
    private final JLabel lblPlay = new JLabel();
    
    /** File icon size in pixels. */
    private static final int ICON_FILE_SIZE = 20;

    /** Play icon size in pixels. */
    private static final int ICON_PLAY_SIZE = 28;

    /**
     * Creates the renderer panel and configures its subcomponents.
     */
    public MediaFileListRenderer() {
        setLayout(new BorderLayout(12, 6));
        setOpaque(true);

        lblIcon.setIcon(IconUtils.load("/images/file.png", ICON_FILE_SIZE));

        lblTitle.setFont(new Font("Serif", Font.BOLD, 15));

        lblState.setFont(new Font("Serif", Font.PLAIN, 12));
        lblState.setForeground(new Color(80, 80, 80));

        lblPlay.setIcon(IconUtils.load("/images/play.png", ICON_PLAY_SIZE));
        lblPlay.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);
        center.add(lblTitle);
        center.add(lblState);

        add(lblIcon, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(lblPlay, BorderLayout.EAST);

        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }

    /**
     * Configures and returns the renderer component for a given list item.
     *
     * @param list the JList we are painting
     * @param value the media file to render (may be null)
     * @param index cell index
     * @param isSelected whether the cell is selected
     * @param cellHasFocus whether the cell has focus
     * @return the component used to render the cell
     */
    @Override
    public Component getListCellRendererComponent(
            JList<? extends MediaFile> list,
            MediaFile value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        if (value != null) {
            lblTitle.setText(value.getFileName());
            String state = value.getNetworkState();
            lblState.setText(state != null ? state : "");

            boolean canPlay = "LOCAL".equals(state) || "BOTH".equals(state);
            lblPlay.setVisible(canPlay);
        } else {
            lblTitle.setText("");
            lblState.setText("");
            lblPlay.setVisible(false);
        }

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            lblTitle.setForeground(list.getSelectionForeground());
            lblState.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            lblTitle.setForeground(list.getForeground());
            lblState.setForeground(new Color(90, 90, 90));
        }

        return this;
    }
}