package com.back.messengers;

import com.back.MethodReturns;
import com.back.messages.Message;

import java.util.ArrayList;

public abstract class Messenger implements IMessageManager{
    protected ArrayList<Message> messages;

}
