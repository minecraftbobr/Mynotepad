import javax.swing.*;
import java.awt.*;
import java.io.*;

public class Uninstaller {

    private static final String APP_DISPLAY_NAME = "Мой Блокнот";

    private static File installDir;
    private static JFrame frame;
    private static JCheckBox deleteSettings;
    private static JCheckBox deleteSha256;

    public static void main(String[] args) {
        try {
            File jar = new File(Uninstaller.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            installDir = jar.getParentFile();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Не удалось определить папку установки.");
            System.exit(1);
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        frame = new JFrame("Удаление — " + APP_DISPLAY_NAME);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 340);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Удаление " + APP_DISPLAY_NAME);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(15, 20, 5, 20));
        frame.add(title, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        JTextArea info = new JTextArea(
                "Программа будет удалена из папки:\n" + installDir.getAbsolutePath());
        info.setEditable(false);
        info.setLineWrap(true);
        info.setWrapStyleWord(true);
        info.setBackground(frame.getBackground());
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(info);
        center.add(Box.createVerticalStrut(10));

        deleteSettings = new JCheckBox("Удалить сохранённые настройки (settings.txt)", true);
        deleteSettings.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(deleteSettings);

        deleteSha256 = new JCheckBox("Удалить SHA-256 Calculator (sha256sumcalc.jar)", true);
        deleteSha256.setAlignmentX(Component.LEFT_ALIGNMENT);
        center.add(deleteSha256);

        frame.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelBtn = new JButton("Отмена");
        cancelBtn.addActionListener(e -> System.exit(0));

        JButton uninstallBtn = new JButton("Удалить");
        uninstallBtn.addActionListener(e -> doUninstall());

        bottom.add(cancelBtn);
        bottom.add(uninstallBtn);
        frame.add(bottom, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static void doUninstall() {
        int confirm = JOptionPane.showConfirmDialog(frame,
                "Вы действительно хотите удалить " + APP_DISPLAY_NAME + "?",
                "Подтверждение", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
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

            File bat = new File(installDir, "Запуск.bat");
            File notepad = new File(installDir, "NotepadApp.jar");
            if (bat.exists()) bat.delete();
            if (notepad.exists()) notepad.delete();

            JOptionPane.showMessageDialog(frame,
                    "Программа удалена.\n\nФайл unins.jar оставлен в папке:\n" +
                    installDir.getAbsolutePath() + "\n\n" +
                    "Его можно удалить вручную, когда захотите.",
                    "Готово", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame,
                    "Ошибка при удалении:\n" + ex.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}