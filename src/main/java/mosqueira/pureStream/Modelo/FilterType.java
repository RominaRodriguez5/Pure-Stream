package mosqueira.pureStream.Modelo;

/**
 * Represents the available filter types used in the library panel.
 *
 * <p>This enumeration is used to filter media files by type
 * (video, audio or all). Each constant contains a user-friendly
 * label displayed in the UI components such as combo boxes.</p>
 *
 * @author Lulas
 * @version 1.0
 */
public enum FilterType {

    /**
     * Shows all media files regardless of their type.
     */
    ALL("All"),

    /**
     * Shows only video files.
     */
    VIDEO("Video"),

    /**
     * Shows only audio files.
     */
    AUDIO("Audio");

    /**
     * Display label used in the user interface.
     */
    private final String label;

    /**
     * Creates a new filter type with a display label.
     *
     * @param label text shown in UI components
     */
    FilterType(String label) {
        this.label = label;
    }

    /**
     * Returns the display label associated with this filter type.
     *
     * @return user-friendly label
     */
    @Override
    public String toString() {
        return label;
    }
}