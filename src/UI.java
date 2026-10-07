import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Графический интерфейс эмулятора
 *
 * <p>Состоит из двух компонентов:
 * <ul>
 *   <li>{@link JTextArea} — область вывода</li>
 *   <li>{@link JTextField} — поле ввода</li>
 * </ul>
 *
 * <p>Пользовательский ввод попадает в {@link BlockingQueue} и обрабатывается
 * потоком {@link CommandInterpreter}. Все изменения UI выполняются в потоке
 * EDT через {@link SwingUtilities#invokeLater(Runnable)}.
 *
 * @see CommandInterpreter
 */
class UI extends JFrame {

    /** Очередь команд, разделяемая с интерпретатором. */
    private final BlockingQueue<String> commandQueue;

    /** Приглашение командной строки */
    private final String prompt;

    /** Область вывода. */
    private JTextArea outputArea;

    /** Поле ввода. */
    private JTextField inputField;

    /**
     * Создаёт окно терминала.
     *
     * @param commandQueue очередь для передачи введённых команд интерпретатору
     */
    public UI(BlockingQueue<String> commandQueue) {
        this.commandQueue = commandQueue;
        String username = System.getProperty("user.name");
        String hostname = resolveHostname();
        this.prompt = username + "@" + hostname + ":~$ ";

        configureFrame(username, hostname);
        createOutputArea();
        createInputField();
        addComponents();
        wireInput();
        setVisible(true);
    }

    /**
     * Определяет имя хоста.
     *
     */
    private String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    /**
     *
     * @param username имя пользователя
     * @param hostname имя хоста
     */
    private void configureFrame(String username, String hostname) {
        setTitle("Эмулятор:  [" + username + "@" + hostname + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height - 75);
        setLayout(new BorderLayout());
    }

    /**
     * Создаёт и настраивает область вывода.
     */
    private void createOutputArea() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.ORANGE);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.append("Добро пожаловать в терминал! \n" + prompt);
    }

    /**
     * Создаёт и настраивает поле ввода.
     */
    private void createInputField() {
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        inputField.setCaretColor(Color.GREEN);
    }

    /**
     * Добавляет компоненты в окно.
     */
    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
    }

    /**
     * Ввод на нажатие enter
     */
    private void wireInput() {
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String command = inputField.getText().trim();
                if (!command.isEmpty()) {
                    outputArea.append(command + "\n");
                    inputField.setText("");
                    commandQueue.offer(command);
                }
            }
        });
    }

    /**
     * Возвращает очередь команд.
     *
     * @return очередь команд
     */
    public BlockingQueue<String> getCommands() {
        return commandQueue;
    }

    /**
     * Показывает приглашение командной строки в области вывода.
     */
    public void showPrompt() {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(prompt);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    /**
     * Дописывает текст в область вывода.
     *
     * @param text текст для добавления
     */
    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    /**
     * Очищает область вывода.
     */
    public void clearOutput() {
        outputArea.setText("");
        outputArea.setCaretPosition(0);
    }
}
