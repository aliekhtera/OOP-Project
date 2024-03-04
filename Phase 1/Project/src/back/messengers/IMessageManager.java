package back.messengers;

import back.MethodReturns;
import back.messages.Message;

public interface IMessageManager {
    public MethodReturns sendMessage(String text, Message repliedTO,boolean isForwarded);
   // public MethodReturns editMessage(int messageID , String newText);
    public MethodReturns deleteMessage(int messageID);
    public MethodReturns forwardMessage(int messageID,Messenger messenger);
}
