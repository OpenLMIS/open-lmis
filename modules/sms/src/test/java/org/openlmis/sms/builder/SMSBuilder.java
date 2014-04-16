/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.sms.builder;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.openlmis.sms.domain.SMS;


import static com.natpryce.makeiteasy.Property.newProperty;

public class SMSBuilder {

  public static final Property<SMS, String> PhoneNumber = newProperty();
  public static final Property<SMS, String> Message = newProperty();
  public static final Property<SMS, String> Direction = newProperty();
  public static final Property<SMS, Boolean> Sent = newProperty();


  public static final Instantiator<SMS> defaultSMS = new Instantiator<SMS>() {

    @Override
    public SMS instantiate(PropertyLookup<SMS> lookup) {

      SMS sms = new SMS();
      sms.setPhoneNumber(lookup.valueOf(PhoneNumber, "12345"));
      sms.setMessage(lookup.valueOf(Message,"Test Text Message"));
      sms.setSent(lookup.valueOf(Sent,false));
      sms.setDirection(lookup.valueOf(Direction,"O"));
      return sms;
    }
  };
}

