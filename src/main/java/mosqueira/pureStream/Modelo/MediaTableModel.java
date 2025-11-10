package mosqueira.pureStream.Modelo;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * MediaTableModel is a table model that manages a list of MediaFile objects.
 * It provides the data structure and behavior needed to display media files
 * in a JTable, showing details like name, MIME type, size and date.
 *
 * @author Romina
 */
public class MediaTableModel extends AbstractTableModel {

    private final String[] columnNames = {"File Name", "MIME Type", "Size (MB)", "Date Downloaded"};
    private final List<MediaFile> mediaFiles;

    /**
     * Default constructor. Initializes an empty list of media files.
     */
    public MediaTableModel() {
        mediaFiles = new ArrayList<>();
    }

    /**
     * Returns the number of rows in the table.
     *
     * @return number of media files
     */
    @Override
    public int getRowCount() {
        return mediaFiles.size();
    }

    /**
     * Returns the number of columns in the table.
     *
     * @return number of columns (fixed)
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     * Returns the name of a given column.
     *
     * @param column the index of the column
     * @return column name
     */
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    /**
     * Returns the value for a specific cell in the table.
     *
     * @param rowIndex    the row index
     * @param columnIndex the column index
     * @return cell value
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
            default:
                return null;
        }
    }

    /**
     * Adds a new MediaFile to the table and updates the view.
     *
     * @param mediaFile the media file to be added
     */
    public void addMediaFile(MediaFile mediaFile) {
        mediaFiles.add(mediaFile);
        fireTableRowsInserted(mediaFiles.size() - 1, mediaFiles.size() - 1);
    }

    /**
     * Removes a MediaFile from the table by its row index.
     *
     * @param rowIndex the index of the row to remove
     */
    public void removeMediaFile(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaFiles.size()) {
            mediaFiles.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    /**
     * Returns the MediaFile at the specified row.
     *
     * @param rowIndex index of the row
     * @return MediaFile object
     */
    public MediaFile getMediaFileAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < mediaFiles.size()) {
            return mediaFiles.get(rowIndex);
        }
        return null;
    }

    /**
     * Clears all media files from the table.
     */
    public void clearAll() {
        mediaFiles.clear();
        fireTableDataChanged();
    }

    /**
     * Returns the entire list of media files.
     *
     * @return list of MediaFile objects
     */
    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }
}
