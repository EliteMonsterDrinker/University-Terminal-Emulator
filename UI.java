import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.InetAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.UnknownHostException;

public class UI extends JFrame{
    private JTextArea output;
    private JTextArea input;
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
        //дефолтный размер окна, потом меняется
        setSize(new Dimension(600,400));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        //вычисляем размер
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension win = getSize();
        //если окно меньше, чем полный экран, то задаём ему полноэкранные значения
        setSize(Math.max(win.width, screen.width), Math.max(win.height, screen.height));
        setLayout(new BorderLayout());
        }

        public static void main(String[] args){

            UI test = new UI();
        }
}
