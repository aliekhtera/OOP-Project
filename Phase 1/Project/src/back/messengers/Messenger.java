package back.messengers;

import back.MethodReturns;
import back.messages.Message;

import java.util.ArrayList;

public abstract class Messenger implements IMessageManager{
    protected ArrayList<Message> messages;

}
