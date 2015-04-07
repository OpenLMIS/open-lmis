package org.openlmis.vaccine.domain.smt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.core.domain.Facility;
import org.openlmis.upload.Importable;
import org.openlmis.vaccine.domain.smt.StorageType;
import org.openlmis.vaccine.domain.smt.Temperature;

/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Deprecated
public class VaccineStorage extends BaseModel implements Importable {
    /*
    to be changed to storageType look up value
     */
    private StorageType storageType;
    private String location;
    private String name;
    private int grossCapacity;
    private int netCapacity;
    private Temperature temperature;
    private String dimension;
    private Facility facility;



}
