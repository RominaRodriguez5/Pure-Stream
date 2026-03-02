package mosqueira.pureStream.DesignApp;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

/**
 * Custom background panel that paints a vertical gradient.
 *
 * <p>
 * Used as a container to give the application a consistent background
 * style.</p>
 *
 * @author Lulas
 * @version 1.0
 */
public class PanelUtils extends JPanel {

    /**
     * Serialization identifier for this panel.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Primary color used in the gradient background. Defaults to white.
     */
    private Color color1 = Color.WHITE;

    /**
     * Secondary color used in the gradient background. Defaults to a dark navy
     * tone (RGB 0, 38, 71).
     */
    private Color color2 = new Color(0, 38, 71);

    /**
     * Creates a {@code PanelUtils} panel with the default gradient colors.
     */
    public PanelUtils() {
        // Default constructor for GUI builders and manual instantiation.
        setOpaque(true);
    }

    /**
     * Creates a {@code PanelUtils} panel with custom gradient colors.
     *
     * @param topColor the color at the top of the panel
     * @param bottomColor the color at the bottom of the panel
     */
    public PanelUtils(Color topColor, Color bottomColor) {
        this.color1 = topColor;
        this.color2 = bottomColor;
        setOpaque(true);
    }

    /**
     * Paints the gradient background.
     *
     * @param g the graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth();
        int h = getHeight();

        GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2);
        g2.setPaint(gp);
        g2.fillRect(0, 0, w, h);

        g2.dispose();
    }
}
