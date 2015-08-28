/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openlmis.odkapi.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ODKStorageSurveySubmission extends BaseModel {

    private Long ODKSubmissionId;
    private Long facilityId;
    private int adequateStorageSpace;
    private int adequateShelves;
    private int storeRoomClean;
    private int productsArrangedAppropriately;
    private int productsStoredIssued;
    private int medicinesStoredSeparately;
    private int coldChainFollowed;
    private int productsFreeFromDusts;
    private int productsFreeFromMoisture;
    private int productsFreeFromSunlight;
    private int storeRoomPreventedFromInfestation;
    private int adequateSecurity;
    private int fireExtinguisherAvailable;
    private int storeRoomConditionConductive;
    private int controlForUnauthorizedPersonnel;
    private double totalPercentage;

    // GPS and facility pictures
    private Double GPSLatitude;
    private Double GPSLongitude;
    private Double GPSAltitude;
    private Double GPSAccuracy;
    private ArrayList<byte[]> facilityPictures;
    private byte[] firstPicture;
    private byte[] secondPicture;
    private byte[] thirdPicture;

    private String comment;


}
