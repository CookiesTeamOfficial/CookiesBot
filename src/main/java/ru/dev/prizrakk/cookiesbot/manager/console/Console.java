package ru.dev.prizrakk.cookiesbot.manager.console;

import java.io.IOException;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import ru.dev.prizrakk.cookiesbot.util.Utils;

public class Console extends Utils {
    private final Terminal terminal;
    private final LineReader reader;

    public Console() throws IOException {
        // Создаём терминал и LineReader для обработки пользовательского ввода
        this.terminal = TerminalBuilder.terminal();
        this.reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .build();
    }

    /**
     * Читает строку из консоли.
     *
     * @param prompt текст, который отображается перед вводом
     * @return введённая строка
     */
    public String readLine(String prompt) {
        return reader.readLine(prompt);
    }

    /**
     * Выводит сообщение в консоль (например, для логов).
     *
     * @param message текст сообщения
     */
    public void log(String message) {
        terminal.writer().println(message);
        terminal.flush();
    }

    /**
     * Закрывает терминал, если он больше не нужен.
     */
    public void close() throws IOException {
        terminal.close();
    }
}
