package mosqueira.pureStream.diseñoApp;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 *
 * @author Lulas
 */
public class IconUtils {

    private IconUtils() {
    }

    public static ImageIcon load(String path, int size) {
        URL url = IconUtils.class.getResource(path);
        if (url == null) {
            return null;
        }
        ImageIcon base = new ImageIcon(url);
        Image scaled = base.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    public static void applyFrameIcon(JFrame frame, String path, int size) {
        ImageIcon icon = load(path, size);
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }
    }
}
