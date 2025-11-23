package mosqueira.pureStream.Modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * MediaTableModel is a table model that manages a list of MediaFile objects.
 * It provides the data structure and behavior needed to display media files in
 * a JTable.
 */
public class MediaTableModel extends AbstractTableModel {

    // Column titles for the JTable
    private final String[] columnNames = {
        "File Name",
        "MIME Type",
        "Size (MB)",
        "Date Downloaded"
    };

    // Internal storage for MediaFile rows
    private final List<MediaFile> mediaFiles;

    /**
     * Default constructor. Initializes an empty list of media files.
     */
    public MediaTableModel() {
        mediaFiles = new ArrayList<>();
    }

    /**
     * Returns the number of rows in the table.
     */
    @Override
    public int getRowCount() {
        return mediaFiles.size();
    }

    /**
     * Returns the number of columns in the table.
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of a given column.
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the value for a specific cell in the table.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MediaFile file = mediaFiles.get(rowIndex);

        switch (columnIndex) {
            case 0:
                return file.getFileName();
            case 1:
                return file.getMimeType();
            case 2:
                return String.format("%.2f", file.getSizeInMB());
            case 3:
                return file.getFormattedDate();
        }
        return null;
    }

    /**
     * Adds a new MediaFile to the table.
     */
    public void addMediaFile(MediaFile mediaFile) {
        mediaFiles.add(mediaFile);
        fireTableRowsInserted(mediaFiles.size() - 1, mediaFiles.size() - 1);
    }

    /**
     * Removes a MediaFile from the table.
     */
    public void removeMediaFile(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaFiles.size()) {
            mediaFiles.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Gets the MediaFile at the specified row.
     */
    public MediaFile getMediaFileAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaFiles.size()) {
            return mediaFiles.get(rowIndex);
        }
        return null;
    }

    /**
     * Removes all media files from the table.
     */
    public void clearAll() {
        mediaFiles.clear();
        fireTableDataChanged();
    }

    /**
     * Returns the list of all MediaFiles.
     */
    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    /**
     * Replaces the current list of MediaFiles with another list.
     */
    public void setMediaFiles(List<MediaFile> files) {
        mediaFiles.clear();
        mediaFiles.addAll(files);
        fireTableDataChanged();
    }

    /**
     * Returns the index of a specific MediaFile.
     */
    public int indexOf(MediaFile mf) {
        return mediaFiles.indexOf(mf);
    }
}
