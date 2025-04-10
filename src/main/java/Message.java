/* Rayyan Athar, Mohammed Shayan Khan */

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    ArrayList<ArrayList<String>> groups;
    ArrayList<String> users;
    String content;
    String sender;
    String recipient;
    Integer destinationGroup;
    String type;

    public Message() {
        groups = new ArrayList<>();
        users = new ArrayList<>();
    }

    public Message(String requestType, String msgContent) {
        type = requestType;
        content = msgContent;
    }

    public Message(String requestType, String msgContent, String msgSender, String msgRecipient) {
        type = requestType;
        content = msgContent;
        sender = msgSender;
        recipient = msgRecipient;
    }

    public Message(String requestType, String msgContent, String msgSender, String msgRecipient, Integer msgDestinationGroup) {
        type = requestType;
        content = msgContent;
        sender = msgSender;
        recipient = msgRecipient;
        destinationGroup = msgDestinationGroup;
    }

    public Message(String requestType, String msgSender, ArrayList<ArrayList<String>> newGroups, ArrayList<String> newUsers) {
        type = requestType;
        groups = newGroups;
        users = newUsers;
        sender = msgSender;
    }

    public String toString() {
        if (type.equals("invalid_username")) {
            return "Error (" + type + "): " + content;
        }
        if (destinationGroup != null) {
            return sender + " to group " + destinationGroup + ": " + content;
        }
        return sender + " to " + recipient + ": " + content;
    }
}