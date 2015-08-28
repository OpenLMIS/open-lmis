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

import org.openlmis.core.domain.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)

public class ODKProofOfDeliverySubmissionData extends BaseModel {

    private Long ODKSubmissionId;
    private Long rnrId;
    private Long productId;
    private String productCode;
    private int quantityReceived;
    private boolean allQuantityDelivered;
    private int actualQuantityDelivered;
    private int discrepancyAmount;
    private String commentForShortfallItem;
    private ArrayList<byte[]> proofOfDeliveryPictures;
    private byte[] firstPictureOfDeliveredCartoons;
    private byte[] secondPictureOfDeliveredCartoons;
    private byte[] thirdPictureOfDeliveredCartoons;
    private String receivedBy;

}
