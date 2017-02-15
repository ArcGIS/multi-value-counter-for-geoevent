package com.esri.geoevent.processor.multivaluecounter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.core.geometry.MapGeometry;
import com.esri.ges.core.Uri;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldExpression;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.core.validation.ValidationException;
import com.esri.ges.messaging.EventDestination;
import com.esri.ges.messaging.EventUpdatable;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.GeoEventProducer;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.processor.GeoEventProcessorBase;
import com.esri.ges.processor.GeoEventProcessorDefinition;

public class MultivalueCounter extends GeoEventProcessorBase implements GeoEventProducer, EventUpdatable
{
  private static final Log                     log          = LogFactory.getLog(MultivalueCounter.class);

  private Messaging                            messaging;
  private GeoEventCreator                      geoEventCreator;
  private GeoEventProducer                     geoEventProducer;
  private String                               multivalueField;
  private Uri                                  definitionUri;
  private String                               definitionUriString;

  final Object                                 lock1        = new Object();

  class Counter
  {
    private String      trackId;
    private String      values; //multivalue
    private MapGeometry geometry;
    private int         count;

    public Counter()
    {

    }

    public void setTrackId(String trackId)
    {
      this.trackId = trackId;
    }
    
    public String getTrackId()
    {
      return trackId;
    }
    
    public void setCount(int count)
    {
      this.count = count;
    }
    
    public int getCurrentCount()
    {
      return count;
    }

    public MapGeometry getGeometry()
    {
      return geometry;
    }

    public void setGeometry(MapGeometry geometry2)
    {
      this.geometry = geometry2;
    }

    public String getValues()
    {
      return values;
    }

    public void setValues(String values)
    {
      this.values = values;
    }
  }

  protected MultivalueCounter(GeoEventProcessorDefinition definition) throws ComponentException
  {
    super(definition);
    log.info("MultivalueCounter instantiated.");
  }

  public void afterPropertiesSet()
  {
    multivalueField = getProperty("multivalueField").getValueAsString();
  }

  @Override
  public void setId(String id)
  {
    super.setId(id);
    geoEventProducer = messaging.createGeoEventProducer(new EventDestination(id + ":event"));

  }

  @Override
  public GeoEvent process(GeoEvent geoEvent) throws Exception
  {
    String trackId = geoEvent.getTrackId();
    MapGeometry geometry = geoEvent.getGeometry();
    String multivalue = (String) geoEvent.getField(new FieldExpression(multivalueField)).getValue();

    if (multivalue == null)
    {
      log.info("Input field value null detected while processing MultivalueCounter");
      return null;
    }

    // Need to synchronize the Concurrent Map on write to avoid wrong
    // counting
    try
    {
        String[] valArr = multivalue.split(",");
          
        Counter cnters = new Counter();
        cnters.setTrackId(trackId);
        cnters.setCount(valArr.length);
        cnters.setValues(multivalue);
        cnters.setGeometry(geometry);
        try
        {
          send(createCounterGeoEvent(multivalue, cnters));
        }
        catch (MessagingException e)
        {
          log.error("Error sending update GeoEvent for " + multivalue, e);
        }
    }
    catch(Exception ex)
    {
      log.error("Failed to process MultivalueCounter: " + ex.getMessage());
    }

    return null;
  }

  @Override
  public List<EventDestination> getEventDestinations()
  {
    return (geoEventProducer != null) ? Arrays.asList(geoEventProducer.getEventDestination()) : new ArrayList<EventDestination>();
  }

  @Override
  public void validate() throws ValidationException
  {
    super.validate();
    List<String> errors = new ArrayList<String>();
    if (errors.size() > 0)
    {
      StringBuffer sb = new StringBuffer();
      for (String message : errors)
        sb.append(message).append("\n");
      throw new ValidationException(this.getClass().getName() + " validation failed: " + sb.toString());
    }
  }

  @Override
  public void onServiceStart()
  {
    if (definition != null)
    {
      definitionUri = definition.getUri();
      definitionUriString = definitionUri.toString();
    }
  }

  @Override
  public void onServiceStop()
  {
    super.onServiceStop();
  }

  @Override
  public void shutdown()
  {
    super.shutdown();
  }

  @Override
  public EventDestination getEventDestination()
  {
    return (geoEventProducer != null) ? geoEventProducer.getEventDestination() : null;
  }

  @Override
  public void send(GeoEvent geoEvent) throws MessagingException
  {
    // Try to get it again
    /*
     * if (geoEventProducer == null) { destination = new
     * EventDestination(getId() + ":event"); geoEventProducer =
     * messaging.createGeoEventProducer(destination); }
     */
    if (geoEventProducer != null && geoEvent != null)
    {
      geoEventProducer.send(geoEvent);
    }
  }

  public void setMessaging(Messaging messaging)
  {
    this.messaging = messaging;
    geoEventCreator = messaging.createGeoEventCreator();
  }

  private GeoEvent createCounterGeoEvent(String values, Counter counter) throws MessagingException
  {
    GeoEvent counterEvent = null;
    if (geoEventCreator != null && definitionUriString != null && definitionUri != null)
    {
      try
      {
        counterEvent = geoEventCreator.create("MultivalueCounter", definitionUriString);
        counterEvent.setField(0, counter.getTrackId());
        counterEvent.setField(1, counter.getValues());
        counterEvent.setField(2, counter.getCurrentCount());
        counterEvent.setField(3, new Date());
        counterEvent.setField(4, counter.getGeometry());
        counterEvent.setProperty(GeoEventPropertyName.TYPE, "event");
        counterEvent.setProperty(GeoEventPropertyName.OWNER_ID, getId());
        counterEvent.setProperty(GeoEventPropertyName.OWNER_URI, definitionUri);
      }
      catch (FieldException e)
      {
        counterEvent = null;
        log.error("Failed to create MultivalueCounter GeoEvent: " + e.getMessage());
      }
    }
    return counterEvent;
  }

  @Override
  public void disconnect()
  {
    if (geoEventProducer != null)
      geoEventProducer.disconnect();
  }

  @Override
  public String getStatusDetails()
  {
    return (geoEventProducer != null) ? geoEventProducer.getStatusDetails() : "";
  }

  @Override
  public void init() throws MessagingException
  {
    ;
  }

  @Override
  public boolean isConnected()
  {
    return (geoEventProducer != null) ? geoEventProducer.isConnected() : false;
  }

  @Override
  public void setup() throws MessagingException
  {
    ;
  }

  @Override
  public void update(Observable o, Object arg)
  {
    ;
  }
}
