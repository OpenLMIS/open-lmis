package org.openlmis.vaccine.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;

/**
 * Created by seifu on 11/12/2014.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class VaccineStorage extends BaseModel implements Importable {
    /*
    to be changed to storageType look up value
     */
    private StorageType storageTypeId;
    private String location;
    private int grossCapacity;
    private int netCapacity;
    /*
    to be changed to Tempreture look up value
     */
    private Temprature tempretureId;

}
