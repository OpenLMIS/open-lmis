/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.message;

import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Map;

/**
 * Extends {@link org.springframework.context.MessageSource} interface to provide a way to get all key/message pairs
 * known.
 */
public interface ExposedMessageSource extends MessageSource {
    /**
     * Returns all key/message pairs for the given locale.
     * @param locale the desired locale of the messages.
     * @return a map of all key/message pairs for the given locale.
     */
    public Map<String,String> getAll(Locale locale);
}
