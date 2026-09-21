import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandInterpreter{
    public static void main(String[] args){
        BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
        UI test = new UI(commandQueue);
        while(true){

            System.out.println(test.getCommands());          // the object
            System.out.println(test.getCommands().size());   // how many items
            test.getCommands().forEach(System.out::println); // each item
        }
    }
}

