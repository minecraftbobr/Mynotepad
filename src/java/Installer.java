import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;

public class Installer {

    private static final String APP_NAME = "MyNotepad";
    private static final String APP_DISPLAY_NAME = "Мой Блокнот";
    private static final String APP_VERSION = "1.0";

    private static final String RES_NOTEPAD  = "/resources/NotepadApp.jar";
    private static final String RES_UNINS    = "/resources/unins.jar";
    private static final String RES_SHA256   = "/resources/sha256sumcalc.jar";

    private static JFrame frame;
    private static JTextField pathField;
    private static JCheckBox installSha256;
    private static JCheckBox launchAfterInstall;
    private static JProgressBar progress;
    private static JTextArea log;

    private static File installDir;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        String defaultPath = System.getenv("LOCALAPPDATA") + File.separator + APP_NAME;

        frame = new JFrame("Установка — " + APP_DISPLAY_NAME);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(580, 440);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Установка " + APP_DISPLAY_NAME + " " + APP_VERSION);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        frame.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        center.add(new JLabel("Папка установки:"), gbc);

        pathField = new JTextField(defaultPath);
        gbc.gridx = 1; gbc.weightx = 1;
        center.add(pathField, gbc);

        JButton browseBtn = new JButton("Обзор...");
        browseBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                pathField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        gbc.gridx = 2; gbc.weightx = 0;
        center.add(browseBtn, gbc);

        installSha256 = new JCheckBox("Установить калькулятор суммы SHA-256", false);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        center.add(installSha256, gbc);

        launchAfterInstall = new JCheckBox("Запустить Мой Блокнот после установки", false);
        gbc.gridy = 2;
        center.add(launchAfterInstall, gbc);

        progress = new JProgressBar(0, 100);
        progress.setStringPainted(true);
        gbc.gridy = 3;
        center.add(progress, gbc);

        log = new JTextArea(6, 40);
        log.setEditable(false);
        log.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane logScroll = new JScrollPane(log);
        gbc.gridy = 4; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1;
        center.add(logScroll, gbc);

        frame.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> System.exit(0));

        JButton installBtn = new JButton("Установить");
        installBtn.addActionListener(e -> runInstall());

        bottom.add(cancelBtn);
        bottom.add(installBtn);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void runInstall() {
        installDir = new File(pathField.getText().trim());

        new Thread(() -> {
            try {
                log("Начало установки в " + installDir.getAbsolutePath());
                setProgress(5);

                if (!installDir.exists() && !installDir.mkdirs()) {
                    throw new IOException("Не удалось создать папку: " + installDir);
                }
                setProgress(15);

                extractResource(RES_NOTEPAD, new File(installDir, "NotepadApp.jar"));
                setProgress(40);
                log("Установлен NotepadApp.jar");

                extractResource(RES_UNINS, new File(installDir, "unins.jar"));
                setProgress(60);
                log("Установлен unins.jar");

                if (installSha256.isSelected()) {
                    extractResource(RES_SHA256, new File(installDir, "sha256sumcalc.jar"));
                    setProgress(80);
                    log("Установлен sha256sumcalc.jar");
                } else {
                    setProgress(80);
                    log("Калькулятор SHA-256 пропущен");
                }

                createLauncherBat();
                setProgress(95);
                log("Создан файл запуска");

                setProgress(100);
                log("Установка завершена успешно!");

                int choice = JOptionPane.showConfirmDialog(frame,
                        "Установка завершена!\n\nЗапустить " + APP_DISPLAY_NAME + "?",
                        "Готово", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                if (launchAfterInstall.isSelected() || choice == JOptionPane.YES_OPTION) {
                    launchApp();
                }
                System.exit(0);

            } catch (Exception ex) {
                log("ОШИБКА: " + ex.getMessage());
                JOptionPane.showMessageDialog(frame,
                        "Ошибка установки:\n" + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }).start();
    }

    private static void extractResource(String resourcePath, File destination) throws IOException {
        try (InputStream in = Installer.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Ресурс не найден внутри install.jar: " + resourcePath);
            }
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static void createLauncherBat() throws IOException {
        File bat = new File(installDir, "Запуск.bat");
        String content = "@echo off\r\nstart \"\" javaw -jar \"" + installDir.getAbsolutePath()
                + "\\NotepadApp.jar\"\r\n";
        Files.write(bat.toPath(), content.getBytes("CP866"));
    }

    private static void launchApp() {
        try {
            String javaHome = System.getProperty("java.home");
            String javaw = javaHome + "\\bin\\javaw.exe";
            File jar = new File(installDir, "NotepadApp.jar");
            new ProcessBuilder(javaw, "-jar", jar.getAbsolutePath()).start();
        } catch (IOException ex) {
            log("Не удалось запустить: " + ex.getMessage());
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