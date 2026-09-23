import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class Sha256SumCalc {

    private static final String SETTINGS_FILE = "settings.txt";
    private static String currentLang = "en";

    private static final Map<String, Map<String, String>> L = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("title",         "SHA-256 Calculator");
        en.put("file",          "File:");
        en.put("choose",        "Choose file");
        en.put("calc",          "Calculate SHA-256");
        en.put("copy",          "Copy");
        en.put("result",        "Result");
        en.put("resultTitle",   "Result");
        en.put("chooseTitle",   "Choose EXE, APK or other file");
        en.put("filterDesc",    "Executable files (*.exe, *.apk, *.jar, *.msi, *.zip)");
        en.put("noFile",        "Please choose a file first!");
        en.put("fileNotFound",  "File not found!");
        en.put("error",         "Error");
        en.put("warning",       "Warning");
        en.put("computing",     "Computing SHA-256...\n");
        en.put("fileLabel",     "File:   ");
        en.put("pathLabel",     "Path:   ");
        en.put("sizeLabel",     "Size:   ");
        en.put("shaLabel",      "SHA-256:\n");
        en.put("sumFormat",     "sha256sum format:\n");
        en.put("copied",        "Hash copied to clipboard");
        en.put("done",          "Done");
        en.put("hashNotFound",  "Hash not found");
        en.put("notFound",      "File not found: ");
        en.put("unknownCmd",    "Unknown command: ");
        en.put("noFileSpec",    "Error: no file specified.");
        en.put("usageCalc",     "Usage: sha256sumcalc calc <file>");
        en.put("langSet",       "Language set to: ");
        en.put("langUnknown",   "Unknown language: ");
        en.put("langUsage",     "Usage: sha256sumcalc set language <EN|RU|ES>");
        L.put("en", en);

        Map<String, String> ru = new HashMap<>();
        ru.put("title",         "SHA-256 Калькулятор");
        ru.put("file",          "Файл:");
        ru.put("choose",        "Выбрать файл");
        ru.put("calc",          "Вычислить SHA-256");
        ru.put("copy",          "Копировать");
        ru.put("result",        "Результат");
        ru.put("resultTitle",   "Результат");
        ru.put("chooseTitle",   "Выберите EXE, APK или другой файл");
        ru.put("filterDesc",    "Исполняемые файлы (*.exe, *.apk, *.jar, *.msi, *.zip)");
        ru.put("noFile",        "Сначала выберите файл!");
        ru.put("fileNotFound",  "Файл не найден!");
        ru.put("error",         "Ошибка");
        ru.put("warning",       "Предупреждение");
        ru.put("computing",     "Вычисление SHA-256...\n");
        ru.put("fileLabel",     "Файл:   ");
        ru.put("pathLabel",     "Путь:   ");
        ru.put("sizeLabel",     "Размер: ");
        ru.put("shaLabel",      "SHA-256:\n");
        ru.put("sumFormat",     "sha256sum-формат:\n");
        ru.put("copied",        "Хеш скопирован в буфер обмена");
        ru.put("done",          "Готово");
        ru.put("hashNotFound",  "Хеш не найден");
        ru.put("notFound",      "Файл не найден: ");
        ru.put("unknownCmd",    "Неизвестная команда: ");
        ru.put("noFileSpec",    "Ошибка: не указан файл.");
        ru.put("usageCalc",     "Использование: sha256sumcalc calc <файл>");
        ru.put("langSet",       "Язык установлен: ");
        ru.put("langUnknown",   "Неизвестный язык: ");
        ru.put("langUsage",     "Использование: sha256sumcalc set language <EN|RU|ES>");
        L.put("ru", ru);

        Map<String, String> es = new HashMap<>();
        es.put("title",         "Calculadora SHA-256");
        es.put("file",          "Archivo:");
        es.put("choose",        "Elegir archivo");
        es.put("calc",          "Calcular SHA-256");
        es.put("copy",          "Copiar");
        es.put("result",        "Resultado");
        es.put("resultTitle",   "Resultado");
        es.put("chooseTitle",   "Elija EXE, APK u otro archivo");
        es.put("filterDesc",    "Archivos ejecutables (*.exe, *.apk, *.jar, *.msi, *.zip)");
        es.put("noFile",        "¡Primero elija un archivo!");
        es.put("fileNotFound",  "¡Archivo no encontrado!");
        es.put("error",         "Error");
        es.put("warning",       "Advertencia");
        es.put("computing",     "Calculando SHA-256...\n");
        es.put("fileLabel",     "Archivo: ");
        es.put("pathLabel",     "Ruta:    ");
        es.put("sizeLabel",     "Tamaño:  ");
        es.put("shaLabel",      "SHA-256:\n");
        es.put("sumFormat",     "Formato sha256sum:\n");
        es.put("copied",        "Hash copiado al portapapeles");
        es.put("done",          "Listo");
        es.put("hashNotFound",  "Hash no encontrado");
        es.put("notFound",      "Archivo no encontrado: ");
        es.put("unknownCmd",    "Comando desconocido: ");
        es.put("noFileSpec",    "Error: no se especificó archivo.");
        es.put("usageCalc",     "Uso: sha256sumcalc calc <archivo>");
        es.put("langSet",       "Idioma establecido: ");
        es.put("langUnknown",   "Idioma desconocido: ");
        es.put("langUsage",     "Uso: sha256sumcalc set language <EN|RU|ES>");
        L.put("es", es);
    }

    private static String T(String key) {
        Map<String, String> lang = L.get(currentLang);
        if (lang != null && lang.containsKey(key)) return lang.get(key);
        return L.get("en").get(key);
    }

    private static File findSettingsFile() {
        File local = new File(SETTINGS_FILE);
        if (local.exists()) return local;

        try {
            File jar = new File(Sha256SumCalc.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            File dir = jar.isFile() ? jar.getParentFile() : jar;
            File inDir = new File(dir, SETTINGS_FILE);
            if (inDir.exists()) return inDir;
        } catch (Exception ignored) {}

        return local;
    }

    private static void loadSettings() {
        File f = findSettingsFile();
        if (!f.exists()) return;

        try {
            for (String line : Files.readAllLines(f.toPath(), StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.startsWith("language=")) {
                    String lang = line.substring("language=".length()).trim().toLowerCase();
                    if (L.containsKey(lang)) currentLang = lang;
                }
            }
        } catch (Exception ignored) {}
    }

    private static void saveLanguage(String lang) {
        File f = findSettingsFile();
        StringBuilder sb = new StringBuilder();
        boolean replaced = false;

        if (f.exists()) {
            try {
                for (String line : Files.readAllLines(f.toPath(), StandardCharsets.UTF_8)) {
                    if (line.trim().startsWith("language=")) {
                        sb.append("language=").append(lang).append(System.lineSeparator());
                        replaced = true;
                    } else {
                        sb.append(line).append(System.lineSeparator());
                    }
                }
            } catch (Exception ignored) {}
        }

        if (!replaced) {
            if (sb.length() == 0) {
                sb.append("#MyNotepad settings").append(System.lineSeparator());
            }
            sb.append("language=").append(lang).append(System.lineSeparator());
        }

        try {
            Files.write(f.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
            try {
                Files.setAttribute(f.toPath(), "dos:hidden", true);
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        loadSettings();

        if (args.length == 0) {
            launchGui(null);
            return;
        }

        String command = args[0].toLowerCase();

        switch (command) {
            case "help":
            case "-h":
            case "--help":
                printHelp();
                return;

            case "set":
                handleSet(args);
                return;

            case "gui":
                launchGui(args.length > 1 ? args[1] : null);
                return;

            case "calc":
                if (args.length < 2) {
                    System.err.println(T("noFileSpec"));
                    System.err.println(T("usageCalc"));
                    System.exit(1);
                }
                runCli(args[1]);
                return;

            default:
                File f = new File(args[0]);
                if (f.exists()) {
                    runCli(args[0]);
                } else {
                    System.err.println(T("unknownCmd") + args[0]);
                    printHelp();
                    System.exit(1);
                }
        }
    }

    private static void handleSet(String[] args) {
        if (args.length >= 3 && args[1].equalsIgnoreCase("language")) {
            String lang = args[2].toLowerCase();
            switch (lang) {
                case "en":
                case "english":
                case "ru":
                case "russian":
                case "es":
                case "espanol":
                case "español":
                case "spanish":
                    String code;
                    if (lang.startsWith("en")) code = "en";
                    else if (lang.startsWith("ru")) code = "ru";
                    else code = "es";
                    currentLang = code;
                    saveLanguage(code);
                    System.out.println(T("langSet") + code.toUpperCase());
                    return;
                default:
                    System.err.println(T("langUnknown") + args[2]);
                    System.err.println(T("langUsage"));
                    System.exit(1);
            }
        } else {
            System.err.println(T("langUsage"));
            System.exit(1);
        }
    }

    private static void runCli(String fileArg) {
        File file = resolveFile(fileArg);

        if (file == null || !file.exists() || !file.isFile()) {
            System.err.println(T("notFound") + fileArg);
            System.exit(1);
        }

        try {
            String hash = computeSha256(file);
            System.out.println(hash + "  " + file.getName());
        } catch (Exception e) {
            System.err.println(T("error") + ": " + e.getMessage());
            System.exit(1);
        }
    }

    private static File resolveFile(String arg) {
        File f = new File(arg);
        if (f.exists()) return f;

        File cwd = new File(System.getProperty("user.dir"), arg);
        if (cwd.exists()) return cwd;

        return f;
    }

    private static String computeSha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[8192];
        try (FileInputStream fis = new FileInputStream(file)) {
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }
        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static void printHelp() {
        System.out.println();
        System.out.println(T("title"));
        System.out.println("==================");
        System.out.println();
        System.out.println("Usage / Использование / Uso:");
        System.out.println("  sha256sumcalc                    GUI");
        System.out.println("  sha256sumcalc gui [file]         GUI with preselected file");
        System.out.println("  sha256sumcalc calc <file>        SHA-256 of file");
        System.out.println("  sha256sumcalc <file>             Same as calc <file>");
        System.out.println("  sha256sumcalc set language <EN|RU|ES>");
        System.out.println("  sha256sumcalc help               Show this help");
        System.out.println();
    }

    private static void launchGui(String preselectedFile) {
        SwingUtilities.invokeLater(() -> new Sha256Gui(preselectedFile));
    }

    static class Sha256Gui extends JFrame {
        private final JTextField filePathField;
        private final JTextArea resultArea;
        private final JProgressBar progressBar;

        Sha256Gui(String preselectedFile) {
            super(T("title"));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(800, 560);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            JPanel topPanel = new JPanel(new BorderLayout(5, 5));
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

            filePathField = new JTextField();
            filePathField.setEditable(false);

            JButton browseButton = new JButton(T("choose"));
            JButton hashButton = new JButton(T("calc"));
            JButton copyButton = new JButton(T("copy"));

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            buttonPanel.add(browseButton);
            buttonPanel.add(hashButton);
            buttonPanel.add(copyButton);

            topPanel.add(new JLabel(T("file")), BorderLayout.WEST);
            topPanel.add(filePathField, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);

            resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder(T("resultTitle")));

            progressBar = new JProgressBar(0, 100);
            progressBar.setStringPainted(true);
            progressBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

            add(topPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
            add(progressBar, BorderLayout.SOUTH);

            browseButton.addActionListener(e -> chooseFile());
            hashButton.addActionListener(e -> calculateHash());
            copyButton.addActionListener(e -> copyResult());

            if (preselectedFile != null) {
                File f = resolveFile(preselectedFile);
                if (f.exists() && f.isFile()) {
                    filePathField.setText(f.getAbsolutePath());
                } else {
                    resultArea.setText(T("notFound") + preselectedFile);
                }
            }

            setVisible(true);
        }

        private void chooseFile() {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle(T("chooseTitle"));
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String n = f.getName().toLowerCase();
                    return n.endsWith(".exe") || n.endsWith(".apk") || n.endsWith(".jar")
                            || n.endsWith(".zip") || n.endsWith(".msi");
                }
                @Override public String getDescription() {
                    return T("filterDesc");
                }
            });
            chooser.setAcceptAllFileFilterUsed(true);

            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                filePathField.setText(chooser.getSelectedFile().getAbsolutePath());
                resultArea.setText("");
                progressBar.setValue(0);
            }
        }

        private void calculateHash() {
            String path = filePathField.getText();
            if (path == null || path.isEmpty()) {
                JOptionPane.showMessageDialog(this, T("noFile"),
                        T("warning"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                JOptionPane.showMessageDialog(this, T("fileNotFound"),
                        T("error"), JOptionPane.ERROR_MESSAGE);
                return;
            }

            resultArea.setText(T("computing"));
            progressBar.setValue(0);

            new SwingWorker<String, Integer>() {
                @Override
                protected String doInBackground() throws Exception {
                    return computeSha256WithProgress(file, this::publish);
                }

                @Override
                protected void process(java.util.List<Integer> chunks) {
                    progressBar.setValue(chunks.get(chunks.size() - 1));
                }

                @Override
                protected void done() {
                    try {
                        String hash = get();
                        progressBar.setValue(100);
                        StringBuilder sb = new StringBuilder();
                        sb.append(T("fileLabel")).append(file.getName()).append("\n");
                        sb.append(T("pathLabel")).append(file.getAbsolutePath()).append("\n");
                        sb.append(T("sizeLabel")).append(formatSize(file.length())).append("\n\n");
                        sb.append(T("shaLabel")).append(hash).append("\n\n");
                        sb.append(T("sumFormat"));
                        sb.append(hash).append("  ").append(file.getName());
                        resultArea.setText(sb.toString());
                    } catch (Exception ex) {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        resultArea.setText(T("error") + ": " + cause.getMessage());
                    }
                }
            }.execute();
        }

        private void copyResult() {
            String text = resultArea.getText();
            if (text == null || text.isEmpty()) return;

            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("\\b[a-fA-F0-9]{64}\\b")
                    .matcher(text);
            if (m.find()) {
                java.awt.datatransfer.StringSelection sel =
                        new java.awt.datatransfer.StringSelection(m.group());
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
                JOptionPane.showMessageDialog(this, T("copied"),
                        T("done"), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, T("hashNotFound"),
                        T("error"), JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private static String computeSha256WithProgress(File file, java.util.function.Consumer<Integer> cb) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        long total = file.length();
        long read = 0;
        int lastPercent = -1;
        byte[] buffer = new byte[8192];

        try (FileInputStream fis = new FileInputStream(file)) {
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
                read += n;
                if (total > 0) {
                    int percent = (int) (read * 100 / total);
                    if (percent != lastPercent) {
                        lastPercent = percent;
                        cb.accept(percent);
                    }
                }
            }
        }

        byte[] hash = digest.digest();
        StringBuilder sb = new StringBuilder(hash.length * 2);
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private static String formatSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
