package com.esri.geoevent.processor.multivaluecounter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.component.ComponentException;
import com.esri.ges.messaging.Messaging;
import com.esri.ges.processor.GeoEventProcessor;
import com.esri.ges.processor.GeoEventProcessorServiceBase;

public class MultivalueCounterService extends GeoEventProcessorServiceBase
{
  private Messaging messaging;
  final private static Log LOG = LogFactory.getLog(MultivalueCounterService.class);

  public MultivalueCounterService()
  {
    definition = new MultivalueCounterDefinition();
    LOG.info("EventCountNCategoriesService instantiated.");
  }

  @Override
  public GeoEventProcessor create() throws ComponentException
  {
    MultivalueCounter detector = new MultivalueCounter(definition);
    detector.setMessaging(messaging);
    return detector;
  }

  public void setMessaging(Messaging messaging)
  {
    this.messaging = messaging;
  }
}