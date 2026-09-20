import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.UnknownHostException;

public class UI extends JFrame{
    private JTextArea outputArea;
    private JTextField inputField;
    public UI(){
        //создаём окно на основе реальных данных пк
        String username = System.getProperty("user.name");
        String hostname;
        //дефолтное имя устройства задаём на unknown, если не получится найти
        try{
            hostname = InetAddress.getLocalHost().getHostName();
        }
        catch(UnknownHostException e){
            hostname = "unknown";
        }

        setTitle("Эмулятор:  [" + username + "@" + hostname + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //вычисляем размер
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        //тут мы рисуем сам интерфейс

        //поле вывода
        outputArea = new JTextArea();
        setLayout(new BorderLayout());
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.ORANGE);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        //добавляет пролистывание страницы и рендерит текстовые поля
        JScrollPane scrollPane = new JScrollPane(outputArea);

        //поле ввода
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 14));
        inputField.setCaretColor(Color.GREEN);
        //расставляет элементы в окне
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        setVisible(true);
        }

        public static void main(String[] args){
            SwingUtilities.invokeLater(()-> new UI());
        }
}
