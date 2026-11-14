package ui;


import java.io.IOException;
import static ui.EscapeSequences.*;

public class CommandLineUI {
    public CommandLineUI() {
        try {
            runBashCommand("stty raw -echo < /dev/tty");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Failed to switch terminal to raw mode");
            return;
        }
        Runtime.getRuntime().addShutdownHook(new Thread(CommandLineUI::revertTerminal));


        System.out.print(ENTER_ALT_SCREEN + ERASE_SCREEN + "Chess UI :)");
        System.out.flush();

        try {
            System.in.read();
        }
        catch (IOException e) {

        }

        revertTerminal();
    }

    private static void runBashCommand(String command) throws IOException, InterruptedException {
        Process process = new ProcessBuilder("sh", "-c", command).redirectErrorStream(true).start();
        String output = new String(process.getInputStream().readAllBytes());
        process.waitFor();
    }

    private static void revertTerminal() {
        try {
            runBashCommand("stty echo icanon < /dev/tty");
        }
        catch (IOException | InterruptedException e) {
            System.err.println("Error restoring terminal to previous state");
        }
        System.out.println(EXIT_ALT_SCREEN + RESET_ALL_FORMATTING + SHOW_CURSOR);
        System.out.flush();
    }
}
