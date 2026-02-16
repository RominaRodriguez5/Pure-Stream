package mosqueira.pureStream.Modelo;

/**
 *
 * @author Lulas
 */
public enum FilterType {
     ALL("All"),
    VIDEO("Video"),
    AUDIO("Audio");

    private final String label;

    FilterType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

