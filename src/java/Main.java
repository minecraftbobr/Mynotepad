import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

public class Main {

    private static File currentFile = null;
    private static JFrame frame;
    private static JTextArea textArea;
    private static JScrollPane scrollPane;
    private static JMenuBar menuBar;

    private static boolean showSaveNotification = true;
    private static String language = "ru";
    private static String theme = "light";

    private static File settingsFile;
    private static final Properties settings = new Properties();

    private static void initSettings() {
        try {
            File jar = new File(Main.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            File dir = jar.getParentFile();
            settingsFile = new File(dir, "settings.txt");
        } catch (Exception ex) {
            settingsFile = new File("settings.txt");
        }

        if (settingsFile.exists()) {
            try (InputStream in = new FileInputStream(settingsFile)) {
                settings.load(in);
            } catch (IOException ignored) {}
        }

        showSaveNotification = Boolean.parseBoolean(
                settings.getProperty("show_save_notification", "true"));
        language = settings.getProperty("language", "ru");
        theme = settings.getProperty("theme", "light");

        hideFile(settingsFile);
    }

    private static void saveSettings() {
        try (OutputStream out = new FileOutputStream(settingsFile)) {
            settings.store(out, "MyNotepad settings");
        } catch (IOException ignored) {}
        hideFile(settingsFile);
    }

    private static void hideFile(File f) {
        try {
            Files.setAttribute(f.toPath(), "dos:hidden", true);
        } catch (Exception ignored) {}
    }

    private static String t(String key) {
        switch (language) {
            case "en":
                switch (key) {
                    case "title_new": return "Untitled";
                    case "menu_file": return "File";
                    case "menu_settings": return "Settings";
                    case "open": return "Open";
                    case "save": return "Save";
                    case "save_as": return "Save As...";
                    case "exit": return "Exit";
                    case "find": return "Find";
                    case "show_notify": return "Show notification on save";
                    case "language": return "Language";
                    case "theme": return "Theme";
                    case "theme_light": return "Light (white bg, black text)";
                    case "theme_dark": return "Dark (black bg, white text)";
                    case "saved_title": return "Saved";
                    case "saved_msg": return "File saved successfully!";
                    case "error_read": return "Error reading file!";
                    case "error_save": return "Error saving file!";
                    case "find_title": return "Find";
                    case "find_prompt": return "Enter text to find:";
                    case "find_not_found": return "Text not found!";
                    case "exit_title": return "Exit";
                    case "exit_msg": return "Save changes before exiting?";
                    case "exit_save": return "Save";
                    case "exit_dont_save": return "Don't Save";
                    case "exit_cancel": return "Cancel";
                }
                break;
            case "es":
                switch (key) {
                    case "title_new": return "Sin título";
                    case "menu_file": return "Archivo";
                    case "menu_settings": return "Ajustes";
                    case "open": return "Abrir";
                    case "save": return "Guardar";
                    case "save_as": return "Guardar como...";
                    case "exit": return "Salir";
                    case "find": return "Buscar";
                    case "show_notify": return "Mostrar notificación al guardar";
                    case "language": return "Idioma";
                    case "theme": return "Tema";
                    case "theme_light": return "Claro (fondo blanco, texto negro)";
                    case "theme_dark": return "Oscuro (fondo negro, texto blanco)";
                    case "saved_title": return "Guardado";
                    case "saved_msg": return "¡Archivo guardado correctamente!";
                    case "error_read": return "¡Error al leer el archivo!";
                    case "error_save": return "¡Error al guardar el archivo!";
                    case "find_title": return "Buscar";
                    case "find_prompt": return "Introduce el texto a buscar:";
                    case "find_not_found": return "¡Texto no encontrado!";
                    case "exit_title": return "Salir";
                    case "exit_msg": return "¿Guardar cambios antes de salir?";
                    case "exit_save": return "Guardar";
                    case "exit_dont_save": return "No guardar";
                    case "exit_cancel": return "Cancelar";
                }
                break;
            default:
                switch (key) {
                    case "title_new": return "Без имени";
                    case "menu_file": return "Файл";
                    case "menu_settings": return "Настройки";
                    case "open": return "Открыть";
                    case "save": return "Сохранить";
                    case "save_as": return "Сохранить как...";
                    case "exit": return "Выход";
                    case "find": return "Найти";
                    case "show_notify": return "Показывать уведомление при сохранении";
                    case "language": return "Язык";
                    case "theme": return "Тема";
                    case "theme_light": return "Светлая (белый фон, чёрный текст)";
                    case "theme_dark": return "Тёмная (чёрный фон, белый текст)";
                    case "saved_title": return "Сохранено";
                    case "saved_msg": return "Файл успешно сохранён!";
                    case "error_read": return "Ошибка при чтении файла!";
                    case "error_save": return "Ошибка при сохранении файла!";
                    case "find_title": return "Поиск";
                    case "find_prompt": return "Введите текст для поиска:";
                    case "find_not_found": return "Текст не найден!";
                    case "exit_title": return "Выход";
                    case "exit_msg": return "Сохранить изменения перед выходом?";
                    case "exit_save": return "Сохранить";
                    case "exit_dont_save": return "Не сохранять";
                    case "exit_cancel": return "Отмена";
                }
        }
        return key;
    }

    public static void main(String[] args) {
        initSettings();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(700, 550);
        frame.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setFont(new Font("Arial", Font.PLAIN, 16));
        scrollPane = new JScrollPane(textArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        buildMenuBar();
        applyTheme();
        updateTitle();

        textArea.getInputMap().put(
            KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK), "find");
        textArea.getActionMap().put("find", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) { showFindDialog(); }
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApp();
            }
        });

        frame.setVisible(true);
    }

    private static void buildMenuBar() {
        menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(t("menu_file"));

        JMenuItem openItem = new JMenuItem(t("open"));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openItem.addActionListener(e -> openFile());

        JMenuItem saveItem = new JMenuItem(t("save"));
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        saveItem.addActionListener(e -> saveToCurrentFile());

        JMenuItem saveAsItem = new JMenuItem(t("save_as"));
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        saveAsItem.addActionListener(e -> saveAs());

        JMenuItem findItem = new JMenuItem(t("find"));
        findItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK));
        findItem.addActionListener(e -> showFindDialog());

        JMenuItem exitItem = new JMenuItem(t("exit"));
        exitItem.addActionListener(e -> exitApp());

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();
        fileMenu.add(findItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu settingsMenu = new JMenu(t("menu_settings"));

        JCheckBoxMenuItem notifyItem = new JCheckBoxMenuItem(t("show_notify"), showSaveNotification);
        notifyItem.addActionListener(e -> {
            showSaveNotification = notifyItem.isSelected();
            settings.setProperty("show_save_notification", String.valueOf(showSaveNotification));
            saveSettings();
        });

        JMenu langMenu = new JMenu(t("language"));
        ButtonGroup langGroup = new ButtonGroup();
        JRadioButtonMenuItem ruItem = new JRadioButtonMenuItem("Русский", language.equals("ru"));
        JRadioButtonMenuItem enItem = new JRadioButtonMenuItem("English", language.equals("en"));
        JRadioButtonMenuItem esItem = new JRadioButtonMenuItem("Español", language.equals("es"));

        ruItem.addActionListener(e -> setLanguage("ru"));
        enItem.addActionListener(e -> setLanguage("en"));
        esItem.addActionListener(e -> setLanguage("es"));

        langGroup.add(ruItem);
        langGroup.add(enItem);
        langGroup.add(esItem);
        langMenu.add(ruItem);
        langMenu.add(enItem);
        langMenu.add(esItem);

        JMenu themeMenu = new JMenu(t("theme"));
        ButtonGroup themeGroup = new ButtonGroup();
        JRadioButtonMenuItem lightItem = new JRadioButtonMenuItem(t("theme_light"), theme.equals("light"));
        JRadioButtonMenuItem darkItem = new JRadioButtonMenuItem(t("theme_dark"), theme.equals("dark"));

        lightItem.addActionListener(e -> setTheme("light"));
        darkItem.addActionListener(e -> setTheme("dark"));

        themeGroup.add(lightItem);
        themeGroup.add(darkItem);
        themeMenu.add(lightItem);
        themeMenu.add(darkItem);

        settingsMenu.add(notifyItem);
        settingsMenu.addSeparator();
        settingsMenu.add(langMenu);
        settingsMenu.add(themeMenu);
        menuBar.add(settingsMenu);

        frame.setJMenuBar(menuBar);
    }

    private static void setLanguage(String lang) {
        language = lang;
        settings.setProperty("language", lang);
        saveSettings();
        buildMenuBar();
        updateTitle();
        frame.revalidate();
        frame.repaint();
    }

    private static void setTheme(String th) {
        theme = th;
        settings.setProperty("theme", th);
        saveSettings();
        applyTheme();
    }

    private static void applyTheme() {
        Color bg, fg, caret, selectionBg, selectionFg;

        if (theme.equals("dark")) {
            bg = Color.BLACK;
            fg = Color.WHITE;
            caret = Color.WHITE;
            selectionBg = new Color(60, 60, 60);
            selectionFg = Color.WHITE;
        } else {
            bg = Color.WHITE;
            fg = Color.BLACK;
            caret = Color.BLACK;
            selectionBg = new Color(180, 210, 255);
            selectionFg = Color.BLACK;
        }

        textArea.setBackground(bg);
        textArea.setForeground(fg);
        textArea.setCaretColor(caret);
        textArea.setSelectionColor(selectionBg);
        textArea.setSelectedTextColor(selectionFg);

        scrollPane.getViewport().setBackground(bg);
        scrollPane.setBackground(bg);

        if (theme.equals("dark")) {
            scrollPane.getVerticalScrollBar().setBackground(new Color(40, 40, 40));
            scrollPane.getHorizontalScrollBar().setBackground(new Color(40, 40, 40));
        } else {
            scrollPane.getVerticalScrollBar().setBackground(null);
            scrollPane.getHorizontalScrollBar().setBackground(null);
        }

        frame.repaint();
    }

    private static void updateTitle() {
        String title = (currentFile == null) ? t("title_new") : currentFile.getAbsolutePath();
        frame.setTitle(title);
    }

    private static void openFile() {
        JFileChooser chooser = new JFileChooser();
        if (currentFile != null) chooser.setCurrentDirectory(currentFile.getParentFile());
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                textArea.setText("");
                String line;
                while ((line = reader.readLine()) != null) {
                    textArea.append(line + "\n");
                }
                currentFile = file;
                updateTitle();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, t("error_read"),
                        t("saved_title"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void saveToCurrentFile() {
        if (currentFile == null) {
            saveAs();
        } else {
            writeFile(currentFile);
        }
    }

    private static void saveAs() {
        JFileChooser chooser = new JFileChooser();
        if (currentFile != null) chooser.setCurrentDirectory(currentFile.getParentFile());
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            if (writeFile(file)) {
                currentFile = file;
                updateTitle();
            }
        }
    }

    private static boolean writeFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            if (showSaveNotification) {
                JOptionPane.showMessageDialog(frame, t("saved_msg"),
                        t("saved_title"), JOptionPane.INFORMATION_MESSAGE);
            }
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, t("error_save"),
                    t("saved_title"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static void showFindDialog() {
        String query = JOptionPane.showInputDialog(frame,
                t("find_prompt"), t("find_title"), JOptionPane.QUESTION_MESSAGE);
        if (query == null || query.isEmpty()) return;

        String text = textArea.getText();
        int start = textArea.getSelectionEnd();
        int index = text.indexOf(query, start);
        if (index == -1) {
            index = text.indexOf(query);
        }
        if (index == -1) {
            JOptionPane.showMessageDialog(frame, t("find_not_found"),
                    t("find_title"), JOptionPane.WARNING_MESSAGE);
        } else {
            textArea.requestFocus();
            textArea.select(index, index + query.length());
        }
    }

    private static void exitApp() {
        Object[] options = {
            t("exit_save"),
            t("exit_dont_save"),
            t("exit_cancel")
        };
        int choice = JOptionPane.showOptionDialog(
                frame,
                t("exit_msg"),
                t("exit_title"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            if (currentFile == null) {
                if (!saveAsForExit()) return;
            } else {
                if (!writeFileSilent(currentFile)) return;
            }
            System.exit(0);
        } else if (choice == 1) {
            System.exit(0);
        }
    }

    private static boolean saveAsForExit() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            if (writeFileSilent(file)) {
                currentFile = file;
                updateTitle();
                return true;
            }
        }
        return false;
    }

    private static boolean writeFileSilent(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(textArea.getText());
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, t("error_save"),
                    t("saved_title"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}