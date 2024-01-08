import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Programa cliente del chat.
 */
public class ChatClient {
    private static volatile boolean done = false;

    public static void main(String[] args) throws IOException {
        final int PORT = 8888;
        final String HOST = "localhost";

        System.out.println("Bienvenido al chat room!\n");
        System.out.println("Por favor entre su comando.");
        System.out.println("USO:  LOGIN usuario_o_nick");
        System.out.println("      CHAT mensaje");
        System.out.println("      LOGOUT");
        System.out.println("Presione ENTER para enviar su mensaje.\n");

        Socket socket = new Socket(HOST, PORT);
        InputStream inStream = socket.getInputStream();
        OutputStream outStream = socket.getOutputStream();
        final BufferedReader in = new BufferedReader(new InputStreamReader(inStream));
        PrintWriter out = new PrintWriter(outStream);
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        // Hilo para manejar la salida del servidor
        Runnable outputRunnable = () -> {
            try {
                while (!done) {
                    String response = in.readLine();
                    System.out.println(response);
                    if (response.equals("Adios!")) {
                        done = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        Thread outputThread = new Thread(outputRunnable);
        outputThread.start();

        // Hilo para manejar la entrada del usuario
        while (!done) {
            String line = console.readLine();
            out.println(line);
            out.flush();
        }

        socket.close();
    }
}
