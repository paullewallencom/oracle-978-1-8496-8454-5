package com.oracle.cep.bean;

import com.bea.wlevs.ede.api.EventRejectedException;
import com.bea.wlevs.ede.api.RelationSink;

public class OutputBean implements RelationSink
{
  public void onInsertEvent(Object event) throws EventRejectedException
  {
    System.out.println("+ " + event.toString());
  }

  public void onDeleteEvent(Object event) throws EventRejectedException
  {
    System.out.println("- " + event.toString());
  }

  public void onUpdateEvent(Object event) throws EventRejectedException
  {
    System.out.println("U " + event.toString());
  }
}
