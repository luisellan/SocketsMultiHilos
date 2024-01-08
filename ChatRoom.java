import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class ChatRoom {
    private List<ChatService> activeService;
    int capacity;
    private Map<String, Chatter> chatterHash;

    public ChatRoom(int aCapacity) {
        capacity = aCapacity;
        chatterHash = Collections.synchronizedMap(new HashMap<>(capacity));
        activeService = Collections.synchronizedList(new ArrayList<>(capacity));
    }

    public synchronized void register(String aName) {
        chatterHash.put(aName, new Chatter(aName));
    }

    public synchronized void leave(String aName, ChatService service) {
        chatterHash.remove(aName);
        activeService.remove(service);
    }

    public void add(ChatService cs) {
        activeService.add(cs);
    }

    public synchronized void broadcast(String requestor, String msg, ChatService chatService) {
        for (int i = 0; i < activeService.size(); i++) {
            ChatService cs = activeService.get(i);
            if (cs != chatService && cs.getUserName() != null) {
                cs.putMessage(requestor + ": " + msg);
            }
        }
    }
}
