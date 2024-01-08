import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Ejecuta los comandos de un protocolo de chat room simple recibidos de un socket.
 */
public class ChatService implements Runnable {
    private String userName;
    private Socket socket;
    private ChatRoom chatRoom;
    private PrintWriter out;
    private BufferedReader in;
    private boolean loggedIn;

    public ChatService(Socket aSocket, ChatRoom aChatRoom) {
        socket = aSocket;
        chatRoom = aChatRoom;
        chatRoom.add(this);
        loggedIn = false;
    }

    @Override
    public void run() {
        try {
            InputStream inStream = socket.getInputStream();
            OutputStream outStream = socket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(inStream));
            out = new PrintWriter(outStream);

            while (true) {
                if (!in.ready()) continue;
                String line = in.readLine();
                int commandDelimiterPos = line.indexOf(' ');
                if (commandDelimiterPos < 0) commandDelimiterPos = line.length();
                String command = line.substring(0, commandDelimiterPos);
                line = line.substring(commandDelimiterPos);

                String response = executeCommand(command, line);
                putMessage(response);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void putMessage(String msg) {
        if (out != null) {
            out.println(msg);
            out.flush();
        }
    }

    public String executeCommand(String command, String line) throws IOException {
        if (command.equals("LOGIN")) {
            userName = line;
            chatRoom.register(userName);
            chatRoom.broadcast(userName, "LOGIN", this);
            loggedIn = true;
            return "Administrador del chat room: Hola, " + userName + ".";
        } else if (!loggedIn) {
            return "Administrador del chat room: Usted debe hacer LOGIN primero";
        } else if (command.equals("CHAT")) {
            String message = line;
            chatRoom.broadcast(userName, message, this);
            return userName + ": " + message;
        } else if (!command.equals("LOGOUT")) {
            return "Administrador del chat room: Comando inválido";
        }

        chatRoom.broadcast(userName, "LOGOUT", this);
        chatRoom.leave(userName, this);
        return "Adiós!";
    }

    public String getUserName() {
        return userName;
    }
}
