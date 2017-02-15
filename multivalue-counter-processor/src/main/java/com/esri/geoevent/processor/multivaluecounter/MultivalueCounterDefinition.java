package com.esri.geoevent.processor.multivaluecounter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.property.LabeledValue;
import com.esri.ges.core.property.PropertyDefinition;
import com.esri.ges.core.property.PropertyType;
import com.esri.ges.processor.GeoEventProcessorDefinitionBase;

public class MultivalueCounterDefinition extends GeoEventProcessorDefinitionBase
{
  final private static Log LOG = LogFactory.getLog(MultivalueCounterDefinition.class);

  public MultivalueCounterDefinition()
  {
    try
    {
      propertyDefinitions.put("geometryField", new PropertyDefinition("geometryField", PropertyType.String, "GEOMETRY", "Geometry Field Name", "Geometry Field Name", false, false));
      propertyDefinitions.put("multivalueField", new PropertyDefinition("multivalueField", PropertyType.String, "TRACK_ID", "Multivalue Field Name", "Multivalue Field Name", false, false));
      GeoEventDefinition ged = new DefaultGeoEventDefinition();
      ged.setName("MultivalueCounter");
      List<FieldDefinition> fds = new ArrayList<FieldDefinition>();
      fds.add(new DefaultFieldDefinition("trackId", FieldType.String, "TRACK_ID"));
      fds.add(new DefaultFieldDefinition("valueList", FieldType.String));
      fds.add(new DefaultFieldDefinition("valueCount", FieldType.Long));
      fds.add(new DefaultFieldDefinition("lastReceived", FieldType.Date));
      fds.add(new DefaultFieldDefinition("geometry", FieldType.Geometry, "GEOMETRY"));
      ged.setFieldDefinitions(fds);
      geoEventDefinitions.put(ged.getName(), ged);
    }
    catch (Exception e)
    {
      LOG.error("Error setting up Multivalue Counter Definition.", e);
    }
  }

  @Override
  public String getVersion()
  {
    return "10.5.0";
  }

  @Override
  public String getDomain()
  {
    return "com.esri.geoevent.processor";
  }

  @Override
  public String getName()
  {
    return "MultivalueCounter";
  }

  @Override
  public String getLabel()
  {
    return "Multivalue Counter";
  }

  @Override
  public String getDescription()
  {
    return "Counting number of values in a multivalue field.";
  }

  @Override
  public String getContactInfo()
  {
    return "mpilouk@esri.com";
  }

}
