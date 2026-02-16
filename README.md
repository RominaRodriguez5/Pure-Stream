# PureStream
Multimedia downloader with ad removal (DI01 Project)

## 👩‍💻 Author
- Name: Romina Rodríguez  
- Course: Desarrollo de Interfaces (DI01)  
- Center: CIFP Pau Casesnoves  

## 🎯 Description
**PureStream** is a Java Swing application that allows downloading videos and audios from YouTube and other platforms compatible with **yt-dlp**, automatically removing ads.  
It features a user-friendly GUI built with NetBeans Designer and allows managing download options, user preferences, and playback of the last downloaded file.

## ⚙️ Main Features
### **1. Download Engine**
- Download videos and audio (MP4 / MP3)
- Automatic ad removal via yt-dlp
- Automatic playback of the last downloaded file

### **2. User Interface**
- GUI designed with NetBeans Swing Designer  
- Table + List media views (**JTable + JList**)  
- Filters:
  - by text  
  - by type (Video / Audio)  
  - by source (Local / Network / Both)  
- Asynchronous operations using **SwingWorker**

### **3. Preferences**
- Customizable download folder  
- Automatic `.m3u` playlist creation  
- Download speed limit  
- External binary configuration: yt-dlp, ffmpeg, ffprobe  

### **4. Library Management**
- Serializable library file: `mediaLibrary.dat`  
- File deletion + automatic UI synchronization  
- Real-time cloud sync  
- Buttons:
  - **Download from Cloud**
  - **Upload to Cloud**

---

## 💻 System Requirements
- JDK 24  
- NetBeans 27  
- OS: Windows recommended (Full HD resolution)  
- External dependencies:
  - **yt-dlp**  
  - **FFmpeg**  
  - **FFprobe**

## 🌐 Cloud Integration (DI Media Component)

PureStream integrates with the **MediaPollingClientComponent**, enabling real-time cloud synchronization.

### **1. Real-time Polling**
The component checks periodically for new uploads in the DI Media API.

onNewMediaDetected → LibraryPanel.loadNetworkMedia()

Updates include:
- Remote file list  
- Local vs Network comparison  
- “Both” tab (intersection of local + cloud files)

### **2. Network Views**
The Library has **three tabs**:
- **Local** → files on disk  
- **Network** → files in the cloud  
- **Both** → files present in both locations  

### **3. Manual Cloud Upload**
The **Upload to Cloud** button allows:
- Selecting any local file  
- Uploading via multipart/form-data  
- Automatic refresh of cloud media lists  

### **4. Manual Cloud Download**
From the **Network** tab:
- Select a remote file  
- Download it  
- Automatically add it to the local library  

### **5. Remember Me + Auto-Login**
- Saves credentials + token to `remember.json`  
- Auto-login when application opens  
- Logout deletes stored credentials  

---

## 📺 Videos and References

During the development of **PureStream**, the following videos and resources were used:

- [Course Material](https://fpadistancia.caib.es/pluginfile.php/93626/mod_resource/content/2/index.html)  
- GUI overview: [Graphical User Interface](https://en.wikipedia.org/wiki/Graphical_user_interface)  
- Maven Swing project creation:
  - [Create a Maven project with NetBeans 15 and JDK 17](https://youtu.be/JLnld38AzUE?si=xgls1k1XCVuJkkIi)  
- Swing Containers and components:
  - JFrame, JDialog, JPanel  
  - Naming conventions: [StackOverflow](https://stackoverflow.com/questions/4770174/name-convention-on-java-swing-componentsprefix), [MIT Java tutorial](https://web.mit.edu/6.005/www/sp14/psets/ps4/java-6-tutorial/components.html)  
- Dialogs (modal and non-modal): [Playlist videos 1–8](https://www.youtube.com/playlist?list=PLIfP1vJ2qakli4Z_-yVZV-rq_hQeHQzUb)  
- JOptionPane: Video 11 from the same playlist  

- JDBC Swing tutorial: [UBC Article](https://www.cs.ubc.ca/~laks/cpsc304/Swing/jdbc_swing.html)  
- Practiced components: JFrame, JLabel, JButton, JTextField, JTextArea, JCheckBox, JRadioButton, ButtonGroup, JSlider, JProgressBar, JSpinner, JOptionPane, JFileChooser, JMenuBar, JMenu, JMenuItem  

- JList and JComboBox of objects: Tutoria 2022-11-11  
- JTable: [Playlist videos 9–20 (exclude 13 & 17)](https://www.youtube.com/playlist?list=PLIfP1vJ2qakli4Z_-yVZV-rq_hQeHQzUb)  

## 📝 Prompts to LLM
- “Explain how to create a Swing GUI in Java with NetBeans for video download application.”  
- “Provide a SwingWorker implementation to handle asynchronous video downloads in Java.”  
- “Show how to use JList, JTable, JComboBox, and filters for a media library in Java Swing.”  
- “How to serialize and deserialize a list of media files in Java.”  
- “Give an example of using yt-dlp with command line options from Java.”
- “Implement cloud synchronization with MediaPollingClientComponent.”

## ⚠️ Problems Encountered
1. **Detecting the last downloaded file:**  
   - Solved by parsing the console output of yt-dlp and checking the download folder.  

2. **M3U playlist creation and validation:**  
   - Solved by creating `PlaylistValidator` and checking/creating the file if missing.  

3. **File deletion and library synchronization:**  
   - Solved by updating both the `MediaTableModel` and the internal `allMediaFiles` list after deletion.  

4. **Preferences management (paths, limits, etc.):**  
   - Solved with `ConfigProperties` class, replacing `${user.home}` dynamically.  

5. **GUI responsiveness during downloads:**  
   - Solved using `SwingWorker` for asynchronous background tasks.  

## 🎨 UX & Interface Improvements

### 🎭 Look & Feel Customization

The application applies a unified visual style across all panels:

- Consistent color palette  
- Customized progress bar  
- Harmonized fonts and component styling  
- Improved visual coherence between Login, Main, Library and Preferences panels  

This ensures a professional and modern appearance.

---

### 🖼 Images & Icons

Icons are centralized in:

`mosqueira.pureStream.DesignApp.IconUtils`

Icons are used consistently in:

- Navigation menus  
- Action buttons (Download, Upload, Delete, Back, etc.)  
- Preferences and configuration controls  

This improves visual affordance and immediate action recognition.

---

### 🔤 Typography & Readability

A clear font hierarchy is implemented:

- Titles → Serif 24  
- Labels and main buttons → Serif 18  
- Logs and input fields → 14–16  

Text contrast and spacing were adjusted to improve legibility and user comfort.

---

### 🧩 Component Distribution & Grouping

Components are logically grouped by functionality instead of being randomly placed.

#### MainPanel
- URL section  
- Format and quality selection  
- Folder selection  
- Log and progress area  
- Action buttons  

#### LibraryPanel
- Tabs (Local / Network / Both)  
- Media table  
- Media list  
- Filters  
- Cloud action buttons  

Tooltips reduce unnecessary dialogs and improve clarity.

---

### 📐 Layout Redesign & Resizable Structure

Rigid `null` layouts were removed.

Dedicated layout classes were introduced:

- `MainPanelLayout`  
- `LibraryPanelLayout`  
- `PreferencesPanelLayout`  

Layouts used:

- `BorderLayout`  
- `MigLayout`  

Benefits:

- Responsive resizing  
- Better scalability  
- Clearer structure  
- Cleaner separation between layout logic and business logic  

---

### ⚠️ Error Handling & User Feedback

The application provides clear and controlled feedback:

- URL validation before download  
- Folder validation  
- Controlled enabling/disabling of buttons  
- Clear error dialogs  
- Recovery from serialization incompatibilities  
- Controlled cloud actions based on current tab  

During downloads:

- WAIT cursor is displayed  
- Progress bar updates in real time  
- Buttons are temporarily disabled  
- UI is re-enabled automatically after completion  

---

### ✨ Extra UX Improvements

#### Affordance
- Icon-based buttons  
- Tooltips for all interactive elements  
- Clickable play zone in media list  

#### Feedback
- Real-time download percentage  
- Informative log messages  
- Clear completion notifications  

#### Restriction & Safety
- Download button only enabled when inputs are valid  
- Delete disabled when not applicable  
- Cloud actions restricted depending on selected tab  


## 🔗 References
- [yt-dlp documentation](https://github.com/yt-dlp/yt-dlp)  
- [FFmpeg documentation](https://ffmpeg.org/documentation.html)  
- [Java Swing tutorial](https://docs.oracle.com/javase/tutorial/uiswing/)  
- [NetBeans Swing Designer Guide](https://netbeans.apache.org/kb/docs/java/gui-builder.html)  

---

This README contains all required links, references, prompts, and a summary of the problems solved during the development of PureStream.
