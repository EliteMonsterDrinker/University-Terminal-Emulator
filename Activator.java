import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;

public class Activator {
    public static void main(String[] args) {
        final BlockingQueue<String> queue = new LinkedBlockingQueue<String>(); //создаёт общий почтовый ящик, куда интерфейс шлёт команды, а интерпретатор парсит их оттуда
        final Path startDir = Path.of(System.getProperty("user.dir")); //начальная папка

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UI ui = new UI(queue); //создаёт интерфейс

                Consumer<String> output = new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        ui.appendOutput(s);
                    }
                }; // когда кто-то вызывает accept, получает строку и добавляет в текстовое поле

                Runnable prompt = new Runnable() {
                    @Override
                    public void run() {
                        ui.showPrompt(); //когда кто-то вызывает run, показывает системное приглашение
                    }
                };

                CommandInterpreter interp =
                new CommandInterpreter(queue, output, prompt, startDir); //основная логика программы

                Thread worker = new Thread(interp, "terminal-worker"); //создаёт новый поток, в котором будет вся обработка комманд. Не в потоке swing
                worker.setDaemon(true); //работает в фоне
                worker.start();

                ui.showPrompt();
            }
        });
    }
}
