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
      message.setFrom(lookup.valueOf(from, "from@openlmis.org"));
      message.setTo(lookup.valueOf(to, "to@openlmis.org"));
      message.setSubject(lookup.valueOf(subject, "Test Email"));
      message.setText(lookup.valueOf(text, "Test Email Text"));

      return message;
    }
  };
}

