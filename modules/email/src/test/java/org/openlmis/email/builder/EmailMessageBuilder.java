/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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

