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
 * Swing user interface for the terminal emulator.
 */
class UI extends JFrame {
    private final BlockingQueue<String> commandQueue;
    private final String prompt;
    private JTextArea outputArea;
    private JTextField inputField;

    /**
     * Creates the terminal window.
     *
     * @param commandQueue queue for commands entered by the user
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

    private String resolveHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    private void configureFrame(String username, String hostname) {
        setTitle("Эмулятор:  [" + username + "@" + hostname + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height - 75);
        setSize(screen.width, screen.height - 75); //-75 было получено опытным путём. Так лучше выглядит
        setLayout(new BorderLayout());
    }

    private void createOutputArea() {
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.ORANGE);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        outputArea.append("Добро пожаловать в терминал! \n" + prompt);
    }

    private void createInputField() {
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        inputField.setCaretColor(Color.GREEN);
    }

    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
    }

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
     * Returns the queue of commands entered by the user.
     *
     * @return command queue
     */
    public BlockingQueue<String> getCommands() {
        return commandQueue;
    }

    /**
     * Shows the system prompt in the output area.
     */
    public void showPrompt() {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(prompt);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    /**
     * Appends text to the output area.
     *
     * @param text text to append
     */
    public void appendOutput(String text) {
        SwingUtilities.invokeLater(() -> {
            outputArea.append(text);
            outputArea.setCaretPosition(outputArea.getDocument().getLength());
        });
    }

    /**
     * Clears the output area.
     */
    public void clearOutput() {
        outputArea.setText("");
        outputArea.setCaretPosition(0);
    }
}
