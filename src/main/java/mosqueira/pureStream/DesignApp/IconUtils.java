package mosqueira.pureStream.DesignApp;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Utility class for loading and scaling icons from the classpath resources.
 *
 * <p>Icons are typically stored under {@code src/main/resources} and accessed
 * using {@link Class#getResource(String)}. This helper loads an image and scales it
 * to a square size to be used in Swing components.</p>
 *
 * @author Lulas
 * @version 1.0
 */
public final class IconUtils {

    /**
     * Private constructor to prevent instantiation (utility class).
     */
    private IconUtils() {
        // Utility class: do not instantiate.
    }

    /**
     * Loads an image resource from the classpath and scales it to the given size.
     *
     * @param path resource path relative to the classpath (e.g. {@code "/images/file.png"})
     * @param size target width and height in pixels
     * @return a scaled {@link ImageIcon}, or {@code null} if the resource is not found
     */
    public static ImageIcon load(String path, int size) {
        URL url = IconUtils.class.getResource(path);
        if (url == null) {
            return null;
        }
        ImageIcon base = new ImageIcon(url);
        Image scaled = base.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Applies the given icon resource as the window icon of a {@link JFrame}.
     *
     * <p>If the resource cannot be found, the frame icon is not changed.</p>
     *
     * @param frame target frame where the icon will be applied
     * @param path resource path relative to the classpath (e.g. {@code "/images/app.png"})
     * @param size target icon size in pixels
     */
    public static void applyFrameIcon(JFrame frame, String path, int size) {
        ImageIcon icon = load(path, size);
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }
    }
}