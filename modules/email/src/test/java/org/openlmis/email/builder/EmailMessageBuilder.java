/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.email.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.springframework.mail.SimpleMailMessage;

import static com.natpryce.makeiteasy.Property.newProperty;

public class EmailMessageBuilder {

  public static final Property<SimpleMailMessage, String> receiver = newProperty();
  public static final Property<SimpleMailMessage, String> subject = newProperty();
  public static final Property<SimpleMailMessage, String> content = newProperty();


  public static final Instantiator<SimpleMailMessage> defaultEmailMessage = new Instantiator<SimpleMailMessage>() {

    @Override
    public SimpleMailMessage instantiate(PropertyLookup<SimpleMailMessage> lookup) {

      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(lookup.valueOf(receiver, "to@openlmis.org"));
      message.setSubject(lookup.valueOf(subject, "Test Email"));
      message.setText(lookup.valueOf(content, "Test Email Text"));

      return message;
    }
  };
}

