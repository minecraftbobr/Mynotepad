import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class Installer {

    private static final String APP_NAME = "MyNotepad";
    private static final String APP_VERSION = "1.0";

    private static final String RES_NOTEPAD  = "/resources/NotepadApp.jar";
    private static final String RES_UNINS    = "/resources/unins.jar";
    private static final String RES_SHA256   = "/resources/sha256sumcalc.jar";

    private static JFrame frame;
    private static JLabel lblTitle;
    private static JLabel lblLang;
    private static JComboBox<String> cmbLang;
    private static JLabel lblPath;
    private static JTextField pathField;
    private static JButton browseBtn;
    private static JCheckBox installSha256;
    private static JCheckBox launchAfterInstall;
    private static JProgressBar progress;
    private static JTextArea log;
    private static JButton installBtn;
    private static JButton cancelBtn;

    private static File installDir;
    private static String currentLang = "en";

    private static final Map<String, Map<String, String>> L = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("title",       "Setup — My Notepad");
        en.put("heading",     "Installing My Notepad 1.0");
        en.put("lang",        "Language:");
        en.put("path",        "Install folder:");
        en.put("browse",      "Browse...");
        en.put("sha",         "Install SHA-256 calculator");
        en.put("launch",      "Launch My Notepad after install");
        en.put("install",     "Install");
        en.put("cancel",      "Cancel");
        en.put("logStart",    "Starting installation into ");
        en.put("logNotepad",  "Installed NotepadApp.jar");
        en.put("logUnins",    "Installed unins.jar");
        en.put("logSha",      "Installed sha256sumcalc.jar");
        en.put("logShaSkip",  "SHA-256 calculator skipped");
        en.put("logBat",      "Launcher created");
        en.put("logSettings", "settings.txt created");
        en.put("logDone",     "Installation completed successfully!");
        en.put("doneTitle",   "Done");
        en.put("doneMsg",     "Installation complete!%n%nLaunch My Notepad?");
        en.put("errTitle",    "Error");
        en.put("errMsg",      "Installation error:");
        en.put("errResource", "Resource not found inside installer: ");
        en.put("errLaunch",   "Failed to launch application: ");
        L.put("en", en);

        Map<String, String> ru = new HashMap<>();
        ru.put("title",       "Установка — Мой Блокнот");
        ru.put("heading",     "Установка Мой Блокнот 1.0");
        ru.put("lang",        "Язык:");
        ru.put("path",        "Папка установки:");
        ru.put("browse",      "Обзор...");
        ru.put("sha",         "Установить калькулятор суммы SHA-256");
        ru.put("launch",      "Запустить Мой Блокнот после установки");
        ru.put("install",     "Установить");
        ru.put("cancel",      "Отмена");
        ru.put("logStart",    "Начало установки в ");
        ru.put("logNotepad",  "Установлен NotepadApp.jar");
        ru.put("logUnins",    "Установлен unins.jar");
        ru.put("logSha",      "Установлен sha256sumcalc.jar");
        ru.put("logShaSkip",  "Калькулятор SHA-256 пропущен");
        ru.put("logBat",      "Создан файл запуска");
        ru.put("logSettings", "Создан settings.txt");
        ru.put("logDone",     "Установка завершена успешно!");
        ru.put("doneTitle",   "Готово");
        ru.put("doneMsg",     "Установка завершена!%n%nЗапустить Мой Блокнот?");
        ru.put("errTitle",    "Ошибка");
        ru.put("errMsg",      "Ошибка установки:");
        ru.put("errResource", "Ресурс не найден внутри установщика: ");
        ru.put("errLaunch",   "Не удалось запустить приложение: ");
        L.put("ru", ru);

        Map<String, String> es = new HashMap<>();
        es.put("title",       "Instalación — Mi Bloc de Notas");
        es.put("heading",     "Instalando Mi Bloc de Notas 1.0");
        es.put("lang",        "Idioma:");
        es.put("path",        "Carpeta de instalación:");
        es.put("browse",      "Examinar...");
        es.put("sha",         "Instalar calculadora SHA-256");
        es.put("launch",      "Ejecutar Mi Bloc de Notas tras instalar");
        es.put("install",     "Instalar");
        es.put("cancel",      "Cancelar");
        es.put("logStart",    "Iniciando instalación en ");
        es.put("logNotepad",  "Instalado NotepadApp.jar");
        es.put("logUnins",    "Instalado unins.jar");
        es.put("logSha",      "Instalado sha256sumcalc.jar");
        es.put("logShaSkip",  "Calculadora SHA-256 omitida");
        es.put("logBat",      "Lanzador creado");
        es.put("logSettings", "settings.txt creado");
        es.put("logDone",     "¡Instalación completada con éxito!");
        es.put("doneTitle",   "Listo");
        es.put("doneMsg",     "¡Instalación completada!%n%n¿Ejecutar Mi Bloc de Notas?");
        es.put("errTitle",    "Error");
        es.put("errMsg",      "Error de instalación:");
        es.put("errResource", "Recurso no encontrado dentro del instalador: ");
        es.put("errLaunch",   "No se pudo iniciar la aplicación: ");
        L.put("es", es);
    }

    private static String T(String key) {
        Map<String, String> lang = L.get(currentLang);
        if (lang != null && lang.containsKey(key)) {
            return lang.get(key);
        }
        return L.get("en").get(key);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        String defaultPath = System.getenv("LOCALAPPDATA") + File.separator + APP_NAME;

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(580, 520);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(null);

        lblTitle = new JLabel();
        lblTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitle.setBounds(20, 15, 400, 30);
        mainPanel.add(lblTitle);

        lblLang = new JLabel();
        lblLang.setBounds(440, 22, 60, 20);
        lblLang.setHorizontalAlignment(SwingConstants.RIGHT);
        mainPanel.add(lblLang);

        cmbLang = new JComboBox<>(new String[]{"EN", "RU", "ES"});
        cmbLang.setBounds(505, 19, 55, 22);
        cmbLang.setSelectedIndex(0);
        cmbLang.addActionListener(e -> {
            switch (cmbLang.getSelectedIndex()) {
                case 0: currentLang = "en"; break;
                case 1: currentLang = "ru"; break;
                case 2: currentLang = "es"; break;
                default: currentLang = "en";
            }
            applyLanguage();
        });
        mainPanel.add(cmbLang);

        lblPath = new JLabel();
        lblPath.setBounds(20, 80, 110, 20);
        mainPanel.add(lblPath);

        pathField = new JTextField(defaultPath);
        pathField.setBounds(135, 77, 310, 22);
        mainPanel.add(pathField);

        browseBtn = new JButton();
        browseBtn.setBounds(455, 75, 85, 24);
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        mainPanel.add(browseBtn);

        installSha256 = new JCheckBox();
        installSha256.setBounds(23, 115, 500, 20);
        mainPanel.add(installSha256);

        launchAfterInstall = new JCheckBox();
        launchAfterInstall.setBounds(23, 140, 500, 20);
        mainPanel.add(launchAfterInstall);

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        progress.setBounds(23, 175, 517, 23);
        mainPanel.add(progress);

        log = new JTextArea();
        log.setEditable(false);
        log.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(log);
        logScroll.setBounds(23, 210, 517, 160);
        mainPanel.add(logScroll);

        cancelBtn = new JButton();
        cancelBtn.setBounds(455, 390, 85, 25);
        cancelBtn.addActionListener(e -> System.exit(0));
        mainPanel.add(cancelBtn);

        installBtn = new JButton();
        installBtn.setBounds(360, 390, 85, 25);
        installBtn.addActionListener(e -> runInstall());
        mainPanel.add(installBtn);

        frame.add(mainPanel);

        applyLanguage();
        frame.setVisible(true);
    }

    private static void applyLanguage() {
        frame.setTitle(T("title"));
        lblTitle.setText(T("heading"));
        lblLang.setText(T("lang"));
        lblPath.setText(T("path"));
        browseBtn.setText(T("browse"));
        installSha256.setText(T("sha"));
        launchAfterInstall.setText(T("launch"));
        installBtn.setText(T("install"));
        cancelBtn.setText(T("cancel"));
    }

    private static void runInstall() {
        installDir = new File(pathField.getText().trim());
        installBtn.setEnabled(false);
        browseBtn.setEnabled(false);
        cmbLang.setEnabled(false);
        pathField.setEditable(false);

        new Thread(() -> {
            try {
                log(T("logStart") + installDir.getAbsolutePath());
                setProgress(5);

                if (!installDir.exists() && !installDir.mkdirs()) {
                    throw new IOException("Не удалось создать папку: " + installDir);
                }
                setProgress(15);

                extractResource(RES_NOTEPAD, new File(installDir, "NotepadApp.jar"));
                setProgress(40);
                log(T("logNotepad"));

                extractResource(RES_UNINS, new File(installDir, "unins.jar"));
                setProgress(60);
                log(T("logUnins"));

                if (installSha256.isSelected()) {
                    extractResource(RES_SHA256, new File(installDir, "sha256sumcalc.jar"));
                    setProgress(80);
                    log(T("logSha"));
                } else {
                    setProgress(80);
                    log(T("logShaSkip"));
                }

                createLauncherBat();
                setProgress(90);
                log(T("logBat"));

                createSettingsFile();
                setProgress(95);
                log(T("logSettings"));

                setProgress(100);
                log(T("logDone"));

                String doneMsg = String.format(T("doneMsg"), System.lineSeparator());
                int choice = JOptionPane.showConfirmDialog(frame,
                        doneMsg,
                        T("doneTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (launchAfterInstall.isSelected() || choice == JOptionPane.YES_OPTION) {
                    launchApp();
                }
                System.exit(0);

            } catch (Exception ex) {
                log(T("errMsg") + " " + ex.getMessage());
                JOptionPane.showMessageDialog(frame,
                        T("errMsg") + "\n" + ex.getMessage(),
                        T("errTitle"), JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> {
                    installBtn.setEnabled(true);
                    browseBtn.setEnabled(true);
                    cmbLang.setEnabled(true);
                    pathField.setEditable(true);
                });
            }
        }).start();
    }

    private static void extractResource(String resourcePath, File destination) throws IOException {
        try (InputStream in = Installer.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException(T("errResource") + resourcePath);
            }
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void createLauncherBat() throws IOException {
        File bat = new File(installDir, "Start.bat");
        String content = "@echo off\r\nstart \"\" javaw -jar \"" + installDir.getAbsolutePath()
                + "\\NotepadApp.jar\"\r\n";
        Files.write(bat.toPath(), content.getBytes("CP866"));
    }

    private static void createSettingsFile() throws IOException {
        File settings = new File(installDir, "settings.txt");

        java.time.ZonedDateTime mskNow = java.time.ZonedDateTime.now(
                java.time.ZoneId.of("Europe/Moscow"));

        String timeStamp = mskNow.format(java.time.format.DateTimeFormatter.ofPattern(
                "EEE MMM dd HH:mm:ss", java.util.Locale.US))
                + " MSK " + mskNow.getYear();

        StringBuilder sb = new StringBuilder();
        sb.append("#MyNotepad settings").append(System.lineSeparator());
        sb.append("#").append(timeStamp).append(System.lineSeparator());
        sb.append("language=").append(currentLang).append(System.lineSeparator());

        Files.write(settings.toPath(), sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));

        try {
            Files.setAttribute(settings.toPath(), "dos:hidden", true);
        } catch (Exception ignored) {}
    }

    private static void launchApp() {
        try {
            String javaHome = System.getProperty("java.home");
            String javaw = javaHome + File.separator + "bin" + File.separator + "javaw.exe";
            File jar = new File(installDir, "NotepadApp.jar");
            new ProcessBuilder(javaw, "-jar", jar.getAbsolutePath()).start();
        } catch (IOException ex) {
            log(T("errLaunch") + ex.getMessage());
        }
    }

    private static void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            log.append(msg + "\n");
            log.setCaretPosition(log.getDocument().getLength());
        });
    }

    private static void setProgress(int value) {
        SwingUtilities.invokeLater(() -> progress.setValue(value));
    }
}
