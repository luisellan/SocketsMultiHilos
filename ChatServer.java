import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer
{
    public static void main(String[] args) throws IOException
    {
        final int ROOM_SIZE = 10;
        ChatRoom chatRoom = new ChatRoom(ROOM_SIZE);
        final int PORT = 8888;
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Esperando que se conecten clientes...");

        while (true)
        {
            // Aceptar conexiones en un socket
            Socket s = server.accept();

            // Crear una nueva instancia de ChatService para cada nueva conexión
            ChatService servicio = new ChatService(s, chatRoom);

            // Crear un nuevo hilo e incluir la nueva instancia de ChatService en ese hilo
            Thread hilo = new Thread(servicio);

            // Iniciar el hilo
            hilo.start();
        }
    }
}
