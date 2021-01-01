package com.oracle.cep.sample.spatial;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.bea.wlevs.ede.api.ApplicationIdentityAware;
import com.bea.wlevs.ede.api.EventProperty;
import com.bea.wlevs.ede.api.EventRejectedException;
import com.bea.wlevs.ede.api.EventType;
import com.bea.wlevs.ede.api.EventTypeRepository;
import com.bea.wlevs.ede.api.RunnableBean;
import com.bea.wlevs.ede.api.StreamSender;
import com.bea.wlevs.ede.api.StreamSink;
import com.bea.wlevs.ede.api.StreamSource;
import com.bea.wlevs.util.Service;
import java.lang.RuntimeException;

import oracle.spatial.geometry.JGeometry;
import com.oracle.cep.cartridge.spatial.Geometry;

public class BusStopAdapter implements RunnableBean, StreamSource, StreamSink, ApplicationIdentityAware
{
	private String path ;
	private String applicationName ;

	public final static double  SMA                   = 6378137.0;                  // in
                                                                                   // meters
	public final static double  ROF                   = 298.257223563;
	public static final double  DEFAULT_ARC_TOLERANCE = 0;

	static final Log            s_logger              = LogFactory.getLog("BusStopAdapter");

	private boolean             m_start               = false;
	private String              m_id;

	private int                 m_initialDelay        = 0;
	private int                 m_sleep               = 0;
	private String              m_filePath			= "";
	private String              m_eventTypeName;
	private EventType           m_eventType;
	private StreamSender        m_eventSender;
	private boolean             m_stopped;
	private boolean             m_show;
	private int                 m_lineno;

	private int                 m_repeat              = 1;
	private EventTypeRepository m_etr                 = null;
	private Thread              m_runningThread;

	boolean                     m_hasSchema           = false;
	boolean                     m_hasTs               = false;
	boolean                     m_doBuffer            = false;
	double                      m_buffer              = 0;
	boolean                     m_done                = false;

	public BusStopAdapter()
	{
		super();
	}

	public void setHasTs(boolean b)
	{
		m_hasTs = b;
	}

	public void setBuffer(double d)
	{
		m_buffer = d;
		m_doBuffer = true;
	}

	public void setApplicationIdentity(String id) {
		applicationName = id;
	}
	
	public String getId()
	{
		return m_id;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
  
	public String getPath() {
		return path;
	}
  
	private File getFileHandle() throws RuntimeException {
	   
	  //get the file from the domain directory
	  String domainHome = System.getProperty("wlevs.domain.home");
	  m_filePath = domainHome + File.separator + path;
	  File f = new File(m_filePath);	  
	  
	  if (f.exists()){
		  return f ;
	  
	  } else {
		  //get the default file in the application bundle
		  m_filePath = domainHome + File.separator + "applications" + File.separator + applicationName + File.separator + path;
	      File f1 = new File(m_filePath);	  
	  
		  if (f1.exists()){
			  return f1 ;
		  } else {			 
			  
			  throw new RuntimeException("cannot find the busstop data file : " + m_filePath);
		  
		  }
	  }
	  
  }

  public void setEventType(String typ)
  {
    m_eventTypeName = typ;
  }

  public void setInitialDelay(int v)
  {
    m_initialDelay = v;
  }

  public void setSleep(int v)
  {
    m_sleep = v;
  }

  public void setRepeat(int n)
  {
    m_repeat = n;
  }

  @Service(filter = EventTypeRepository.SERVICE_FILTER)
  public void setEventTypeRepository(EventTypeRepository etr)
  {
    m_etr = etr;
  }

  public void setShow(boolean b)
  {
    m_show = b;
  }

  public void run()
  {
	
	//System.out.println("Application Identity is: " + applicationName );

	m_stopped = false;

    // Need to store running thread so that it can be interrupted in case of suspend.
    m_runningThread = Thread.currentThread();

    if (m_etr == null)
    {
      throw new RuntimeException("EventTypeRepoitory is not set");
    }

    m_eventType = m_etr.getEventType(m_eventTypeName);
    if (m_eventType == null)
    {
      throw new RuntimeException("EventType(" + m_eventType + ") is not found.");
    }

    if (m_initialDelay > 0)
    {
      try
      {
        s_logger.info("Sleeping for : " + m_initialDelay);
        Thread.sleep(m_initialDelay);
      } catch (InterruptedException e)
      {
        e.printStackTrace();
      }
    }

    synchronized (this)
    {
      while (!m_start)
      {
        try
        {
          this.wait();
        } catch (InterruptedException e)
        {
        }
      }
    }
    BufferedReader reader = null;

    while ((m_repeat != 0) && (!m_stopped))
    {
      try
      {
        m_lineno = 0;
        reader = new BufferedReader(new FileReader(getFileHandle()));
        s_logger.info("fileSource " + m_filePath);
      } catch (Exception e)
      {
        s_logger.warn(e.toString());
        m_stopped = true;
        break;
      }
      while (!isStopped())
      {
        try
        {
          Object ev = null;
          ev = readLine(reader);
          if (ev == null)
          {
            reader.close();
            break;
          }
          m_eventSender.sendInsertEvent(ev);
          if (m_show)
          {
            System.out.println(ev.toString());
          }
        } catch (Exception e)
        {
          s_logger.fatal("Failed to get tuple from " + m_filePath + ":" + m_lineno + "\n" + e.toString() + "\n");
          m_stopped = true;
          break;
        }
        if (m_sleep > 0)
        {
          try
          {
            synchronized (this)
            {
              // We need to save thread so that it can be interrupted in case of
              // suspend.
              wait(m_sleep);
            }
          } catch (InterruptedException e)
          {
            s_logger.warn(e);
            break;
          }
        }
      } // while
      if (m_repeat > 0)
        m_repeat--;
    } // while
    s_logger.info("FileAdaptor  " + hashCode() + " stopped. " + m_filePath);
  }

  public void setEventSender(StreamSender sender)
  {
    m_eventSender = sender;
  }

  public synchronized void suspend() throws Exception
  {
    m_stopped = true;
    if (m_runningThread != null && m_runningThread.isAlive())
    {
      if (s_logger.isDebugEnabled())
      {
        s_logger.debug("Interrupting thread = " + m_runningThread.getId());
      }
      m_runningThread.interrupt();
    }
  }

  private synchronized boolean isStopped()
  {
    return m_stopped;
  }

  protected Object readLine(BufferedReader reader) throws Exception
  {
    if (m_done)
      return null;
    EventProperty idProperty = m_eventType.getProperty("busId");
    EventProperty seqProperty = m_eventType.getProperty("seq");
    EventProperty geomProperty = m_eventType.getProperty("geom");
    String line = null;
    int id = 0;
    int seq = 0;
    double x, y;
    x = y = 0;
    while ((line = reader.readLine()) != null)
    {
      m_lineno++;
      if (m_hasSchema && m_lineno == 1)
      {
        // skip the schema line
        continue;
      }
      String trimmed = line.trim();
      if (trimmed.length() == 0)
      {
        continue;
      }
      if (trimmed.charAt(0) == 'h')
      {
        continue;
      }
      String[] s = trimmed.split(",");
      if (s.length == 0)
      {
        continue;
      }
      for (int i = 0; i < s.length; i++)
      {
        String itemstr = s[i].trim();
        switch (i)
        {
        case 0:
          if (m_hasTs)
          {
            String[] t = itemstr.split(" ");
            if (t.length != 2)
            {
              throw new Exception("invalid format");
            }
            id = Integer.parseInt(t[1]);
          } else
          {
            id = Integer.parseInt(itemstr);
          }
          break;
        case 1:
          seq = Integer.parseInt(itemstr);
          break;
        case 2:
          x = Double.parseDouble(itemstr);
          break;
        case 3:
          y = Double.parseDouble(itemstr);
          break;
        }
      }
      JGeometry geom = Geometry.createPoint(8307, x, y);
      if (m_doBuffer)
      {
        geom = geom.buffer(m_buffer, SMA, ROF, DEFAULT_ARC_TOLERANCE);
        //http://download.oracle.com/docs/cd/B28359_01/appdev.111/b28401/oracle/spatial/geometry/JGeometry.html
      }
      Object event = m_eventType.createEvent();
      idProperty.setValue(event, id);
      seqProperty.setValue(event, seq);
      geomProperty.setValue(event, geom);
      return event;
    }
    m_done = true;
    return null;
  }

  @Override
  public void onInsertEvent(Object event) throws EventRejectedException
  {
    if (!m_start)
    {
      m_start = true;
      synchronized (this)
      {
        notify();
      }
    }
  }
}
