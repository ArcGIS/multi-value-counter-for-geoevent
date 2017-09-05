/*
  Copyright 2017 Esri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.​

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
*/

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
