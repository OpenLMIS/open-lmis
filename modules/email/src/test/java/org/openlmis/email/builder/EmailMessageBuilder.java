/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package org.openlmis.email.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.email.domain.EmailMessage;

import static com.natpryce.makeiteasy.Property.newProperty;

public class EmailMessageBuilder {

  public static final Property<EmailMessage, String> from = newProperty();
  public static final Property<EmailMessage, String> to = newProperty();
  public static final Property<EmailMessage, String> subject = newProperty();
  public static final Property<EmailMessage, String> text = newProperty();


  public static final Instantiator<EmailMessage> defaultEmailMessage = new Instantiator<EmailMessage>() {

    @Override
    public EmailMessage instantiate(PropertyLookup<EmailMessage> lookup) {

      EmailMessage message = new EmailMessage();
      message.setTo(lookup.valueOf(to, "to@openlmis.org"));
      message.setSubject(lookup.valueOf(subject, "Test Email"));
      message.setText(lookup.valueOf(text, "Test Email Text"));

      return message;
    }
  };
}

