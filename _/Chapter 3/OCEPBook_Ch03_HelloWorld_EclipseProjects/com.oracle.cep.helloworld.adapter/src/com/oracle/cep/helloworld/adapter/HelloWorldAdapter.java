package com.oracle.cep.helloworld.adapter;

import java.text.DateFormat;
import java.util.Date;

import com.bea.wlevs.ede.api.Adapter;
import com.bea.wlevs.ede.api.RunnableBean;
import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSource;
import com.oracle.cep.helloworld.event.HelloWorldEvent;

public class HelloWorldAdapter implements Adapter, RunnableBean, StreamSource {

    private static final int SLEEP_MILLIS = 300;

    private DateFormat dateFormat;
    private String message;
    private boolean suspended;

    private StreamSender eventSender;

    public HelloWorldAdapter() {
        super();
        dateFormat = DateFormat.getTimeInstance();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        suspended = false;
        while (!isSuspended()) { // Generate messages forever...

            generateHelloMessage();

            try {
                synchronized (this) {
                    wait(SLEEP_MILLIS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private void generateHelloMessage() {
        String message = this.message + dateFormat.format(new Date());
        HelloWorldEvent event = new HelloWorldEvent();
        event.setMessage(message);
        eventSender.sendInsertEvent(event);
    }

    /* (non-Javadoc)
     * @see com.bea.wlevs.ede.api.StreamSource#setEventSender(com.bea.wlevs.ede.api.StreamSender)
     */
    public void setEventSender(StreamSender sender) {
        eventSender = sender;
    }

    /*
    /* (non-Javadoc)
     * @see com.bea.wlevs.ede.api.SuspendableBean#suspend()
     */
    public synchronized void suspend() {
        suspended = true;
    }
    
    private synchronized boolean isSuspended() {
        return suspended;
    }
    
}
