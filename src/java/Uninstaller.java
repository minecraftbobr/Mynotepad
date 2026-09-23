import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class Uninstaller {

    private static File installDir;
    private static JFrame frame;
    private static JCheckBox deleteSettings;
    private static JCheckBox deleteSha256;
    private static JButton cancelBtn;
    private static JButton uninstallBtn;
    private static JLabel title;
    private static JTextArea info;
    private static String currentLang = "en";

    private static final Map<String, Map<String, String>> L = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("title",         "Uninstall — My Notepad");
        en.put("heading",       "Uninstalling My Notepad");
        en.put("info",          "The program will be removed from folder:\n");
        en.put("delSettings",   "Delete saved settings (settings.txt)");
        en.put("delSha",        "Delete SHA-256 Calculator (sha256sumcalc.jar)");
        en.put("cancel",        "Cancel");
        en.put("uninstall",     "Uninstall");
        en.put("confirmTitle",  "Confirm");
        en.put("confirmMsg",    "Are you sure you want to uninstall My Notepad?");
        en.put("doneTitle",     "Done");
        en.put("doneMsg",       "Program uninstalled.\n\nunins.jar remains in:\n");
        en.put("doneMsg2",      "\n\nYou can delete it manually later.");
        en.put("error",         "Error");
        en.put("errUninstall",  "Uninstall error:\n");
        en.put("errPath",       "Could not determine installation folder.");
        L.put("en", en);

        Map<String, String> ru = new HashMap<>();
        ru.put("title",         "Удаление — Мой Блокнот");
        ru.put("heading",       "Удаление Мой Блокнот");
        ru.put("info",          "Программа будет удалена из папки:\n");
        ru.put("delSettings",   "Удалить сохранённые настройки (settings.txt)");
        ru.put("delSha",        "Удалить SHA-256 Calculator (sha256sumcalc.jar)");
        ru.put("cancel",        "Отмена");
        ru.put("uninstall",     "Удалить");
        ru.put("confirmTitle",  "Подтверждение");
        ru.put("confirmMsg",    "Вы действительно хотите удалить Мой Блокнот?");
        ru.put("doneTitle",     "Готово");
        ru.put("doneMsg",       "Программа удалена.\n\nФайл unins.jar оставлен в папке:\n");
        ru.put("doneMsg2",      "\n\nЕго можно удалить вручную, когда захотите.");
        ru.put("error",         "Ошибка");
        ru.put("errUninstall",  "Ошибка при удалении:\n");
        ru.put("errPath",       "Не удалось определить папку установки.");
        L.put("ru", ru);

        Map<String, String> es = new HashMap<>();
        es.put("title",         "Desinstalar — Mi Bloc de Notas");
        es.put("heading",       "Desinstalando Mi Bloc de Notas");
        es.put("info",          "El programa se eliminará de la carpeta:\n");
        es.put("delSettings",   "Eliminar configuración guardada (settings.txt)");
        es.put("delSha",        "Eliminar Calculadora SHA-256 (sha256sumcalc.jar)");
        es.put("cancel",        "Cancelar");
        es.put("uninstall",     "Desinstalar");
        es.put("confirmTitle",  "Confirmación");
        es.put("confirmMsg",    "¿Está seguro de que desea desinstalar Mi Bloc de Notas?");
        es.put("doneTitle",     "Listo");
        es.put("doneMsg",       "Programa desinstalado.\n\nunins.jar permanece en:\n");
        es.put("doneMsg2",      "\n\nPuede eliminarlo manualmente después.");
        es.put("error",         "Error");
        es.put("errUninstall",  "Error de desinstalación:\n");
        es.put("errPath",       "No se pudo determinar la carpeta de instalación.");
        L.put("es", es);
    }

    private static String T(String key) {
        Map<String, String> lang = L.get(currentLang);
        if (lang != null && lang.containsKey(key)) return lang.get(key);
        return L.get("en").get(key);
    }

    private static void loadSettings() {
        File settings = new File(installDir, "settings.txt");
        if (!settings.exists()) return;

        try {
            for (String line : Files.readAllLines(settings.toPath(), StandardCharsets.UTF_8)) {
                line = line.trim();
                if (line.startsWith("language=")) {
                    String lang = line.substring("language=".length()).trim().toLowerCase();
                    if (L.containsKey(lang)) currentLang = lang;
                }
            }
        } catch (Exception ignored) {}
    }

    private static void applyLanguage() {
        frame.setTitle(T("title"));
        title.setText(T("heading"));
        info.setText(T("info") + installDir.getAbsolutePath());
        deleteSettings.setText(T("delSettings"));
        deleteSha256.setText(T("delSha"));
        cancelBtn.setText(T("cancel"));
        uninstallBtn.setText(T("uninstall"));
    }

    public static void main(String[] args) {
        try {
            File jar = new File(Uninstaller.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            installDir = jar.getParentFile();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Could not determine installation folder.");
            System.exit(1);
        }

        loadSettings();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 340);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        title = new JLabel();
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        frame.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        info = new JTextArea();
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setBackground(frame.getBackground());
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(info);
        center.add(Box.createVerticalStrut(10));

        deleteSettings = new JCheckBox();
        deleteSettings.setSelected(true);
        deleteSettings.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(deleteSettings);

        deleteSha256 = new JCheckBox();
        deleteSha256.setSelected(true);
        deleteSha256.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(deleteSha256);

        frame.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelBtn = new JButton();
        cancelBtn.addActionListener(e -> System.exit(0));

        uninstallBtn = new JButton();
        uninstallBtn.addActionListener(e -> doUninstall());

        bottom.add(cancelBtn);
        bottom.add(uninstallBtn);
        frame.add(bottom, BorderLayout.SOUTH);

        applyLanguage();
        frame.setVisible(true);
    }

    private static void doUninstall() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                T("confirmMsg"),
                T("confirmTitle"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (deleteSettings.isSelected()) {
                File settings = new File(installDir, "settings.txt");
                if (settings.exists()) settings.delete();
            }

            if (deleteSha256.isSelected()) {
                File sha = new File(installDir, "sha256sumcalc.jar");
                if (sha.exists()) sha.delete();
            }

            File bat = new File(installDir, "Start.bat");
            File batRu = new File(installDir, "Запуск.bat");
            File notepad = new File(installDir, "NotepadApp.jar");
            if (bat.exists()) bat.delete();
            if (batRu.exists()) batRu.delete();
            if (notepad.exists()) notepad.delete();

            JOptionPane.showMessageDialog(frame,
                    T("doneMsg") + installDir.getAbsolutePath() + T("doneMsg2"),
                    T("doneTitle"), JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    T("errUninstall") + ex.getMessage(),
                    T("error"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
