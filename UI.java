import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class UI extends JFrame{
    private String prompt; //системное приглашение
    private JTextArea outputArea;
    private JTextField inputField;
    private final BlockingQueue<String> commandQueue;
    //поле для очереди команд

    public UI(BlockingQueue<String> commandQueue){
        //создаём в объекте очередь для заполнения командами
        this.commandQueue = commandQueue;
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

        prompt = username + "@" + hostname + ":~$ ";

        setTitle("Эмулятор:  [" + username + "@" + hostname + "]");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //вычисляем размер
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        //такая высота, так как иначе текстовая строка плохо в окно помещается. Выбрал опытным путём
        setSize(screen.width, screen.height-75);

        //тут мы рисуем сам интерфейс
        //поле вывода
        outputArea = new JTextArea();
        setLayout(new BorderLayout());
        outputArea.setEditable(false);
        outputArea.setBackground(Color.BLACK);
        outputArea.setForeground(Color.ORANGE);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        outputArea.append("Добро пожаловать в терминал! \n" + prompt);

        //добавляет пролистывание страницы и рендерит текстовые поля
        JScrollPane scrollPane = new JScrollPane(outputArea);

        //поле ввода
        inputField = new JTextField();
        inputField.setBackground(Color.BLACK);
        inputField.setForeground(Color.GREEN);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        inputField.setCaretColor(Color.GREEN);


        // добавляем обработку ввода от пользователя
        inputField.addActionListener(new ActionListener(){ //создаём слушателя событий
            @Override
            public void actionPerformed(ActionEvent e){ //создаём последовательность реакции в ответ на событие(энтер)
                String command = inputField.getText().trim();
                if(!command.isEmpty()){
                    outputArea.append(command + "\n");
                    inputField.setText("");
                    //очищаем текстовое поле ввода
                    //добавляем в очередь команд
                    commandQueue.offer(command);
                }

            }
        });

        //расставляет элементы в окне
        add(scrollPane, BorderLayout.CENTER);
        add(inputField, BorderLayout.SOUTH);
        setVisible(true);
        }

        public BlockingQueue<String> getCommands(){
            return commandQueue;
        }
        //вызывается только после исполнения команды
        public void showPrompt(){
            SwingUtilities.invokeLater(()->{
                outputArea.append(prompt);
                //опускает каретку
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }

        public void appendOutput(String text){
            SwingUtilities.invokeLater(()->{
                outputArea.append(text);
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            });
        }
        public void clearOutput(){
            outputArea.setText("");
            outputArea.setCaretPosition(0);
        }
}
