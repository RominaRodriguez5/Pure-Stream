package mosqueira.pureStream.diseñoApp;

import javax.swing.*;
import java.awt.*;
import mosqueira.pureStream.Modelo.MediaFile;

public class MediaFileListRenderer extends JPanel implements ListCellRenderer<MediaFile> {

    private final JLabel lblIcon = new JLabel();
    private final JLabel lblTitle = new JLabel();
    private final JLabel lblState = new JLabel();
    private final JLabel lblPlay = new JLabel();

    // Tamaños (ajusta aquí sin tocar el resto)
    private static final int ICON_FILE_SIZE = 20;
    private static final int ICON_PLAY_SIZE = 28;

    public MediaFileListRenderer() {

        setLayout(new BorderLayout(12, 6));
        setOpaque(true);

        lblIcon.setIcon(IconUtils.load("/images/file.png", ICON_FILE_SIZE));

        lblTitle.setFont(new Font("Serif", Font.BOLD, 15));

        lblState.setFont(new Font("Serif", Font.PLAIN, 12));
        lblState.setForeground(new Color(80, 80, 80));

        // Icono play más grande
        lblPlay.setIcon(IconUtils.load("/images/play.png", ICON_PLAY_SIZE));
        lblPlay.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel center = new JPanel(new GridLayout(2, 1));
        center.setOpaque(false);
        center.add(lblTitle);
        center.add(lblState);

        add(lblIcon, BorderLayout.WEST);
        add(center, BorderLayout.CENTER);
        add(lblPlay, BorderLayout.EAST);

        // Un poquito de padding para que no se pegue a los bordes
        setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
    }

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

            // ✅ Mostrar play SOLO en LOCAL o BOTH
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
