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

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implements {@link org.openlmis.core.message.ExposedMessageSource} and
 * extends {@link org.springframework.context.support.ReloadableResourceBundleMessageSource} to provide a way to get
 * all key/message pairs known.
 */
@Component
public class ExposedMessageSourceImpl extends ReloadableResourceBundleMessageSource implements ExposedMessageSource {

	private static final String MESSAGES_FILE_NAME = "messages";
	private static final String CLASS_PATH = "classpath:";
	private static final String MESSAGES_EN_MOZ_FILE_NAME = "messages_en_MOZ";
	private static final String MESSAGES_PT_MOZ_FILE_NAME = "messages_pt_MOZ";

	protected Properties getAllPropertiesByFileName(String fileName) {
		clearCacheIncludingAncestors();
		PropertiesHolder propertiesHolder = getProperties(CLASS_PATH + fileName);
		Properties properties = propertiesHolder.getProperties();

		return properties;
	}


	public Map<String, String> getAll(Locale locale) {
		Properties pMoz = null;
		if (locale.toString().equals("en")) {
			pMoz = getAllPropertiesByFileName(MESSAGES_EN_MOZ_FILE_NAME);
		} else if (locale.toString().equals("pt")){
			pMoz = getAllPropertiesByFileName(MESSAGES_PT_MOZ_FILE_NAME);
        }

		Properties baseProperties = getAllPropertiesByFileName(MESSAGES_FILE_NAME);
		Properties localeProperties = getAllPropertiesByFileName(MESSAGES_FILE_NAME + "_" + locale.toString());

		Properties mergedProperties = new Properties();
		mergedProperties.putAll(baseProperties);
		mergedProperties.putAll(localeProperties);
		mergedProperties.putAll(pMoz);

		return convertPropertiesToMap(mergedProperties);
	}

	private Map<String, String> convertPropertiesToMap(Properties mergedProperties) {
		Enumeration<String> keys = (Enumeration<String>) mergedProperties.propertyNames();
		Map<String, String> asMap = new HashMap<>();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			asMap.put(key, mergedProperties.getProperty(key));
		}
		return asMap;
	}
}