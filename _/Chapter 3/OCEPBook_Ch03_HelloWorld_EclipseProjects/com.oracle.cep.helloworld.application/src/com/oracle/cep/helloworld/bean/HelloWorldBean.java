package com.oracle.cep.helloworld.bean;

import com.bea.wlevs.ede.api.StreamSink;
import com.oracle.cep.helloworld.event.*;

public class HelloWorldBean implements StreamSink {

    /* (non-Javadoc)
     * @see com.bea.wlevs.ede.api.StreamSink#onInsertEvent(java.lang.Object)
     */
    public void onInsertEvent(Object event) {
        if (event instanceof HelloWorldEvent) {
            HelloWorldEvent helloWorldEvent = (HelloWorldEvent) event;
            System.out.println("Message: " + helloWorldEvent.getMessage());
        }
    }
}
