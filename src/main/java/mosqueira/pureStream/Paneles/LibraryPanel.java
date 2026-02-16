package mosqueira.pureStream.Paneles;

import mosqueira.mediaPollingClientComponent.model.Media;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import mosqueira.pureStream.MainFrame;
import mosqueira.pureStream.Modelo.FilterType;
import mosqueira.pureStream.Modelo.MediaFile;
import mosqueira.pureStream.Modelo.MediaTableModel;
import mosqueira.pureStream.diseñoApp.IconUtils;
import mosqueira.pureStream.diseñoApp.LibraryPanelLayout;
import mosqueira.pureStream.diseñoApp.MediaFileListRenderer;

/**
 * LibraryPanel displays all downloaded media files (audio/video) and allows the
 * user to search, filter, and delete them from the library.
 *
 * @author Romina
 *
 */
public class LibraryPanel extends javax.swing.JPanel {

    // Name of the serialized library file
    private static String BIBLIOTECA_FILE;

    // Table model for detailed view
    private MediaTableModel tableModel;

    // List model for compact list view
    private DefaultListModel<MediaFile> listModel;

    // Full library of media files (unfiltered)
    private List<MediaFile> allMediaFiles = new ArrayList<>();

    // Reference to MainFrame for navigation
    private MainFrame mainFrame;
    // NETWORK LISTS
    private List<MediaFile> netWorkMedia = new ArrayList<>();
    private List<MediaFile> botMedia = new ArrayList<>();

    /**
     * Constructor: initializes UI components and loads saved library.
     */
    public LibraryPanel(MainFrame mainFrame, MediaTableModel tableModel) {
        this.mainFrame = mainFrame;
        this.tableModel = tableModel;
        BIBLIOTECA_FILE = mainFrame.getRutaDescargas() + File.separator + "mediaLibrary.dat";
        initComponents();
        btnDelete.setEnabled(false);
        new LibraryPanelLayout(this).apply();
        setSize(800, 800);
        setOpaque(false);
        jtabSources.addTab("Local", new JPanel());
        jtabSources.addTab("Network", new JPanel());
        jtabSources.addTab("Both", new JPanel());
        jListDownloads.setCellRenderer(new MediaFileListRenderer());
        jListDownloads.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int index = jListDownloads.locationToIndex(e.getPoint());
                if (index == -1) {
                    return;
                }

                java.awt.Rectangle r = jListDownloads.getCellBounds(index, index);
                if (r == null) {
                    return;
                }

                int playZoneX = r.x + r.width - 40; // últimos 40px

                if (e.getX() >= playZoneX) {
                    MediaFile mf = jListDownloads.getModel().getElementAt(index);
                    playMedia(mf);
                }
            }
        });

        conectarEventos();

        jTblDetails.setModel(tableModel);
        listModel = new DefaultListModel<>();
        jListDownloads.setModel(listModel);

        cargarBiblioteca();

        jTblDetails.setAutoCreateRowSorter(true);
        conectarEventos();

        tableModel.setMediaFiles(allMediaFiles);
        tableModel.fireTableDataChanged();

        listModel.clear();
        for (MediaFile mf : allMediaFiles) {
            listModel.addElement(mf);
        }
        jcbFiltrados.setModel(
                new DefaultComboBoxModel<>(FilterType.values())
        );
        jtabSources.addChangeListener(new javax.swing.event.ChangeListener() {
            @Override
            public void stateChanged(javax.swing.event.ChangeEvent e) {

                int index = jtabSources.getSelectedIndex();

                List<MediaFile> listaActual;

                if (index == 0) {
                    listaActual = allMediaFiles;
                } else if (index == 1) {
                    listaActual = netWorkMedia;
                } else {
                    listaActual = botMedia;
                }

                boolean isLocal = index == 0;
                boolean isNetwork = index == 1;

                btnDownloadFromCloud.setEnabled(!isLocal);
                if (isLocal) {
                    btnDownloadFromCloud.setToolTipText("Only available in Network/Both mode");
                } else {
                    btnDownloadFromCloud.setToolTipText("Download selected cloud file");
                }

                if (isNetwork) {
                    btnDelete.setEnabled(false);
                    btnDelete.setToolTipText("Cannot delete cloud files from disk");
                } else {
                    btnDelete.setEnabled(false);
                    btnDelete.setToolTipText("Delete selected file");
                }

                jTblDetails.clearSelection();
                jListDownloads.clearSelection();

                if (isNetwork || index == 2) {
                    try {
                        loadNetworkMedia();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(LibraryPanel.this,
                                "Cannot load cloud media:\n" + ex.getMessage(),
                                "Network Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                tableModel.setMediaFiles(listaActual);
                tableModel.fireTableDataChanged();

                listModel.clear();
                for (MediaFile mf : listaActual) {
                    listModel.addElement(mf);
                }
            }
        });

        jtxtSearch.setToolTipText("Search in the table library ");
        btnDelete.setToolTipText("Delete selected file");
        btnBack.setToolTipText("Return to main screen");
        btnUploadtoCloud.setToolTipText("Upload selected local file to cloud");
        btnDownloadFromCloud.setToolTipText("Download selected cloud file");

        btnDelete.setIcon(IconUtils.load("/images/delete.png", 20));
        btnBack.setIcon(IconUtils.load("/images/back.png", 20));
        btnUploadtoCloud.setIcon(IconUtils.load("/images/upload.png", 20));
        btnDownloadFromCloud.setIcon(IconUtils.load("/images/download.png", 20));

    }

    private void playMedia(MediaFile mf) {
        if (mf == null) {
            return;
        }

        File file = mf.getFile();

        if (file == null || !file.exists()) {
            JOptionPane.showMessageDialog(
                    this,
                    "File does not exist on disk.\nDownload it first.",
                    "File not found",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Desktop API not supported on this system.",
                        "Playback error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Cannot open file:\n" + ex.getMessage(),
                    "Playback error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Connects selection events between JTable and JList.
     */
    private void conectarEventos() {
        jListDownloads.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    MediaFile selected = jListDownloads.getSelectedValue();
                    if (selected != null) {
                        int modelIndex = tableModel.indexOf(selected);
                        if (modelIndex != -1) {
                            int viewIndex = jTblDetails.convertRowIndexToView(modelIndex);
                            if (viewIndex != -1) {
                                jTblDetails.setRowSelectionInterval(viewIndex, viewIndex);
                            }
                        }
                    }
                }
            }
        });
        // Sync from JTable to JList
        jTblDetails.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int viewRow = jTblDetails.getSelectedRow();
                    btnDelete.setEnabled(jTblDetails.getSelectedRow() != -1);
                    if (viewRow != -1) {
                        int modelRow = jTblDetails.convertRowIndexToModel(viewRow);
                        MediaFile mf = tableModel.getMediaFileAt(modelRow);
                        if (mf != null) {
                            jListDownloads.setSelectedValue(mf, true);
                        }
                    }
                }
            }
        });
    }

    /**
     * Add a new media file to library
     */
    public void addMediaFile(MediaFile media) {

        for (MediaFile mf : allMediaFiles) {
            if (mf.getFile().getAbsolutePath().equals(media.getFile().getAbsolutePath())) {
                return; // evitar duplicado
            }
        }

        media.setNetworkState("LOCAL");
        allMediaFiles.add(media);

        tableModel.addMediaFile(media);
        listModel.addElement(media);
        guardarBiblioteca(); // save updated library
    }

    /**
     * Saves the full media library via serialization.
     */
    private void guardarBiblioteca() {
        try {

            File f = new File(BIBLIOTECA_FILE);
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BIBLIOTECA_FILE))) {
                oos.writeObject(allMediaFiles);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Cannot save library file.",
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Loads the serialized media library if the file exists.
     */
    private void cargarBiblioteca() {
        File f = new File(BIBLIOTECA_FILE);
        if (!f.exists()) {
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {

            List<MediaFile> files = (List<MediaFile>) ois.readObject();
            allMediaFiles.clear();
            allMediaFiles.addAll(files);
            receiveFiles(allMediaFiles);

        } catch (java.io.InvalidClassException ex) {
            // biblioteca antigua incompatible → reset
            allMediaFiles.clear();
            f.delete();
            receiveFiles(allMediaFiles);
            JOptionPane.showMessageDialog(this,
                    "Your saved library was from an older version and has been reset.",
                    "Library reset",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Update view and internal list with provided files
     */
    public void receiveFiles(List<MediaFile> lista) {
        tableModel.setMediaFiles(lista);

        listModel.clear();
        for (MediaFile mf : lista) {
            listModel.addElement(mf);
        }

        jTblDetails.clearSelection();
        jListDownloads.clearSelection();
        btnDelete.setEnabled(false);

    }

    /**
     * Filters library by search text and selected type.
     */
    private void filtrarLista() {
        String searchText = jtxtSearch.getText().toLowerCase().trim();
        FilterType filter = (FilterType) jcbFiltrados.getSelectedItem();

        List<MediaFile> sourceList;

        int index = jtabSources.getSelectedIndex();
        if (index == 0) {
            sourceList = allMediaFiles;
        } else if (index == 1) {
            sourceList = netWorkMedia;
        } else {
            sourceList = botMedia;
        }

        List<MediaFile> filtered = new ArrayList<>();

        for (MediaFile f : sourceList) {

            boolean matchesText = searchText.isEmpty()
                    || f.getFileName().toLowerCase().contains(searchText);

            if (!matchesText) {
                continue;
            }

            boolean matchesType;

            switch (filter) {
                case VIDEO:
                    matchesType = f.getMimeType() != null
                            && f.getMimeType().toLowerCase().contains("video");
                    break;

                case AUDIO:
                    matchesType = f.getMimeType() != null
                            && f.getMimeType().toLowerCase().contains("audio");
                    break;

                case ALL:
                default:
                    matchesType = true;
                    break;
            }

            if (matchesType) {
                filtered.add(f);
            }
        }

        receiveFiles(filtered);
    }

    public void loadNetworkMedia() throws Exception {
        netWorkMedia.clear();
        botMedia.clear();

        List<Media> mediaFromNet = MainFrame.COMPONENT.getAllMedia(mainFrame.getJwtToken());

        for (Media m : mediaFromNet) {
            File fakeRemote = new File(m.mediaFileName);
            MediaFile mf = new MediaFile(fakeRemote, new Date());
            mf.setRemoteId(m.id);
            mf.setNetworkState("NETWORK");
            netWorkMedia.add(mf);
        }

        for (MediaFile local : allMediaFiles) {
            boolean existsRemote = false;
            for (MediaFile remote : netWorkMedia) {
                if (remote.getFileName().equals(local.getFileName())) {
                    existsRemote = true;
                    break;
                }
            }

            if (existsRemote) {
                local.setNetworkState("BOTH");
                botMedia.add(local);
            } else {

                local.setNetworkState("LOCAL");
            }
        }

        filtrarLista();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollTable = new javax.swing.JScrollPane();
        jTblDetails = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        jSeparatorListDownload = new javax.swing.JSeparator();
        jtxtSearch = new javax.swing.JTextField();
        jcbFiltrados = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListDownloads = new javax.swing.JList<>();
        jtabSources = new javax.swing.JTabbedPane();
        btnBack = new javax.swing.JButton();
        btnUploadtoCloud = new javax.swing.JButton();
        btnDownloadFromCloud = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Download details", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Serif", 1, 18), new java.awt.Color(6, 6, 69))); // NOI18N
        setLayout(new java.awt.BorderLayout());

        jTblDetails.setFont(new java.awt.Font("Serif", 0, 18)); // NOI18N
        jTblDetails.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTblDetails.setSelectionBackground(new java.awt.Color(0, 102, 153));
        jTblDetails.setSelectionForeground(new java.awt.Color(0, 0, 0));
        jScrollTable.setViewportView(jTblDetails);

        add(jScrollTable, java.awt.BorderLayout.CENTER);

        btnDelete.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnDelete.setForeground(new java.awt.Color(6, 6, 69));
        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });
        add(btnDelete, java.awt.BorderLayout.PAGE_START);

        jSeparatorListDownload.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Downloads list", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Serif", 1, 18), new java.awt.Color(6, 6, 69))); // NOI18N
        add(jSeparatorListDownload, java.awt.BorderLayout.PAGE_END);

        jtxtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jtxtSearchActionPerformed(evt);
            }
        });
        add(jtxtSearch, java.awt.BorderLayout.LINE_END);

        jcbFiltrados.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        jcbFiltrados.setForeground(new java.awt.Color(6, 6, 69));
        jcbFiltrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbFiltradosActionPerformed(evt);
            }
        });
        add(jcbFiltrados, java.awt.BorderLayout.LINE_START);

        jScrollPane1.setViewportView(jListDownloads);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
        add(jtabSources, java.awt.BorderLayout.CENTER);

        btnBack.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnBack.setForeground(new java.awt.Color(6, 6, 69));
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack, java.awt.BorderLayout.CENTER);

        btnUploadtoCloud.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnUploadtoCloud.setForeground(new java.awt.Color(6, 6, 69));
        btnUploadtoCloud.setText("Upload to Cloud");
        btnUploadtoCloud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadtoCloudActionPerformed(evt);
            }
        });
        add(btnUploadtoCloud, java.awt.BorderLayout.CENTER);

        btnDownloadFromCloud.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnDownloadFromCloud.setForeground(new java.awt.Color(6, 6, 69));
        btnDownloadFromCloud.setText("Download from cloud");
        btnDownloadFromCloud.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadFromCloudActionPerformed(evt);
            }
        });
        add(btnDownloadFromCloud, java.awt.BorderLayout.CENTER);

        btnSearch.setFont(new java.awt.Font("Serif", 1, 18)); // NOI18N
        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });
        add(btnSearch, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Executes the search and filter logic when the user types or changes
     * filters.
     */
    private void jtxtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jtxtSearchActionPerformed
        filtrarLista();
    }//GEN-LAST:event_jtxtSearchActionPerformed
    /**
     * Handles combo box filter selection (Audio, Video, All).
     */
    private void jcbFiltradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbFiltradosActionPerformed
        filtrarLista();
    }//GEN-LAST:event_jcbFiltradosActionPerformed

    /**
     * Handles the deletion of a selected file from the table and disk.
     */
    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        int selected = jTblDetails.getSelectedRow();
        if (selected == -1) {

            return;
        }
        selected = jTblDetails.convertRowIndexToModel(selected);
        MediaFile mf = tableModel.getMediaFileAt(selected);
        File file = mf.getFile();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this file?\n" + file.getName(),
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        // Delete file from disk
        boolean deleted = !file.exists() || file.delete();
        if (!deleted) {
            JOptionPane.showMessageDialog(this, "Failed to delete the file.");
            return;
        }
        // Remove from internal lists
        allMediaFiles.remove(mf);
        tableModel.removeMediaFile(selected);
        listModel.removeElement(mf);
        guardarBiblioteca();
        JOptionPane.showMessageDialog(this, "File deleted successfully.");

    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed

        mainFrame.showMain();
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnUploadtoCloudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadtoCloudActionPerformed
        int selected = jTblDetails.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file from the table.");
            return;
        }

        selected = jTblDetails.convertRowIndexToModel(selected);
        MediaFile mf = tableModel.getMediaFileAt(selected);
        File file = mf.getFile();

        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "The file does not exist on disk.");
            return;
        }

        try {
            // Subida simple
            MainFrame.COMPONENT.uploadFileMulti(
                    file,
                    null, // downloadedFromUrl
                    mainFrame.getJwtToken() // token JWT
            );

            // Recargar datos de la nube
            loadNetworkMedia();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Upload failed:\n" + ex.getMessage());
        }

    }//GEN-LAST:event_btnUploadtoCloudActionPerformed

    private void btnDownloadFromCloudActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadFromCloudActionPerformed
        int selected = jTblDetails.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Please select a file in the Network tab.");
            return;
        }

        selected = jTblDetails.convertRowIndexToModel(selected);
        MediaFile remoteMF = tableModel.getMediaFileAt(selected);

        if (remoteMF.getRemoteId() == null) {
            JOptionPane.showMessageDialog(this, "This file has no remote ID.");
            return;
        }
        try {
            File destino = new File(mainFrame.getRutaDescargas(), remoteMF.getFileName());
            MainFrame.COMPONENT.download(
                    remoteMF.getRemoteId(),
                    destino,
                    mainFrame.getJwtToken()
            );

            MediaFile localMF = new MediaFile(destino, new Date());
            localMF.setRemoteId(remoteMF.getRemoteId());
            localMF.setNetworkState("BOTH");

            allMediaFiles.add(localMF);

            botMedia.add(localMF);

            guardarBiblioteca();

            loadNetworkMedia();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Download failed:\n" + ex.getMessage());
        }
    }//GEN-LAST:event_btnDownloadFromCloudActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        filtrarLista();
    }//GEN-LAST:event_btnSearchActionPerformed

    public javax.swing.JTabbedPane getTabs() {
        return jtabSources;
    }

    public javax.swing.JScrollPane getScrollTable() {
        return jScrollTable;
    }

    public javax.swing.JTable getTable() {
        return jTblDetails;
    }

    public javax.swing.JScrollPane getScrollList() {
        return jScrollPane1;
    }

    public javax.swing.JList<MediaFile> getListDownloads() {
        return jListDownloads;
    }

    public javax.swing.JTextField getTxtSearch() {
        return jtxtSearch;
    }

    public javax.swing.JComboBox<FilterType> getComboFilter() {
        return jcbFiltrados;
    }

    public javax.swing.JButton getBtnBack() {
        return btnBack;
    }

    public javax.swing.JButton getBtnUploadToCloud() {
        return btnUploadtoCloud;
    }

    public javax.swing.JButton getBtnDownloadFromCloud() {
        return btnDownloadFromCloud;
    }

    public javax.swing.JButton getBtnDelete() {
        return btnDelete;
    }

    public javax.swing.JSeparator getSeparatorList() {
        return jSeparatorListDownload;
    }

    public javax.swing.JButton getBtnSearch() {
        return btnSearch;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnDownloadFromCloud;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnUploadtoCloud;
    private javax.swing.JList<MediaFile> jListDownloads;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollTable;
    private javax.swing.JSeparator jSeparatorListDownload;
    private javax.swing.JTable jTblDetails;
    private javax.swing.JComboBox<FilterType> jcbFiltrados;
    private javax.swing.JTabbedPane jtabSources;
    private javax.swing.JTextField jtxtSearch;
    // End of variables declaration//GEN-END:variables
}
