/**
 * Copyright 2014 DNS Belgium vzw
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.dnsbelgium.rdap.jackson;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DateTimeDeserializer extends JsonDeserializer<DateTime> {

  private static final Logger LOGGER = LoggerFactory.getLogger(DateTimeDeserializer.class);

  @Override
  public DateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    if (jp.getText() == null) {
      return null;
    }
    try {
      return DateTime.parse(jp.getText(), ISODateTimeFormat.dateTimeNoMillis());
    } catch (IllegalArgumentException iae) {
      LOGGER.warn(String.format("Cannot parse '%s' as ISO date. Returning null", jp.getText()), iae);
      return null;
    }
  }
}