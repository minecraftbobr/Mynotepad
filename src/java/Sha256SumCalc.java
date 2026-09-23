import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.security.MessageDigest;

public class Sha256SumCalc {

    public static void main(String[] args) {
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

            case "gui":
                launchGui(args.length > 1 ? args[1] : null);
                return;

            case "calc":
                if (args.length < 2) {
                    System.err.println("Ошибка: не указан файл.");
                    System.err.println("Использование: sha256sumcalc calc <файл>");
                    System.exit(1);
                }
                runCli(args[1]);
                return;

            default:
                File f = new File(args[0]);
                if (f.exists()) {
                    runCli(args[0]);
                } else {
                    System.err.println("Неизвестная команда: " + args[0]);
                    printHelp();
                    System.exit(1);
                }
        }
    }

    private static void runCli(String fileArg) {
        File file = resolveFile(fileArg);

        if (file == null || !file.exists() || !file.isFile()) {
            System.err.println("Файл не найден: " + fileArg);
            System.exit(1);
        }

        try {
            String hash = computeSha256(file);
            System.out.println(hash + "  " + file.getName());
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
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
        System.out.println("SHA-256 Calculator");
        System.out.println("==================");
        System.out.println();
        System.out.println("Использование:");
        System.out.println("  sha256sumcalc                    Запустить GUI");
        System.out.println("  sha256sumcalc gui [файл]         GUI, опц. сразу выбрать файл");
        System.out.println("  sha256sumcalc calc <файл>        SHA-256 файла (exe, apk, ...)");
        System.out.println("  sha256sumcalc <файл>             То же, что calc <файл>");
        System.out.println("  sha256sumcalc help               Показать справку");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  sha256sumcalc calc app.apk");
        System.out.println("  sha256sumcalc calc installer.exe");
        System.out.println("  sha256sumcalc calc C:\\Downloads\\app.apk");
        System.out.println("  sha256sumcalc calc /home/user/file.exe");
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
            super("SHA-256 Calculator");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(800, 560);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            JPanel topPanel = new JPanel(new BorderLayout(5, 5));
            topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

            filePathField = new JTextField();
            filePathField.setEditable(false);

            JButton browseButton = new JButton("Выбрать файл");
            JButton hashButton = new JButton("Вычислить SHA-256");
            JButton copyButton = new JButton("Копировать");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            buttonPanel.add(browseButton);
            buttonPanel.add(hashButton);
            buttonPanel.add(copyButton);

            topPanel.add(new JLabel("Файл:"), BorderLayout.WEST);
            topPanel.add(filePathField, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.SOUTH);

            resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            JScrollPane scrollPane = new JScrollPane(resultArea);
            scrollPane.setBorder(BorderFactory.createTitledBorder("Результат"));

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
                    resultArea.setText("Файл не найден: " + preselectedFile);
                }
            }

            setVisible(true);
        }

        private void chooseFile() {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Выберите EXE, APK или другой файл");
            chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                @Override public boolean accept(File f) {
                    if (f.isDirectory()) return true;
                    String n = f.getName().toLowerCase();
                    return n.endsWith(".exe") || n.endsWith(".apk") || n.endsWith(".jar")
                            || n.endsWith(".zip") || n.endsWith(".msi");
                }
                @Override public String getDescription() {
                    return "Исполняемые файлы (*.exe, *.apk, *.jar, *.msi, *.zip)";
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
                JOptionPane.showMessageDialog(this, "Сначала выберите файл!",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
                return;
            }

            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                JOptionPane.showMessageDialog(this, "Файл не найден!",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            resultArea.setText("Вычисление SHA-256...\n");
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
                        sb.append("Файл:   ").append(file.getName()).append("\n");
                        sb.append("Путь:   ").append(file.getAbsolutePath()).append("\n");
                        sb.append("Размер: ").append(formatSize(file.length())).append("\n\n");
                        sb.append("SHA-256:\n").append(hash).append("\n\n");
                        sb.append("sha256sum-формат:\n");
                        sb.append(hash).append("  ").append(file.getName());
                        resultArea.setText(sb.toString());
                    } catch (Exception ex) {
                        Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                        resultArea.setText("Ошибка: " + cause.getMessage());
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
                JOptionPane.showMessageDialog(this, "Хеш скопирован в буфер обмена",
                        "Готово", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Хеш не найден",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
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