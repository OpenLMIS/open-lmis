/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
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
    private byte adequateStorageSpace;
    private byte adequateShelves;
    private byte storeRoomClean;
    private byte productsArrangedAppropriately;
    private byte productsStoredIssued;
    private byte medicinesStoredSeparately;
    private byte coldChainFollowed;
    private byte productsFreeFromDusts;
    private byte productsFreeFromMoisture;
    private byte productsFreeFromSunlight;
    private byte storeRoomPreventedFromInfestation;
    private byte adequateSecurity;
    private byte fireExtinguisherAvailable;
    private byte storeRoomConditionConductive;
    private byte controlForUnauthorizedPersonnel;
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
