package org.openlmis.core.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.core.exception.DataException;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessingSchedule {
    private Integer id;

    private String code;
    private String name;
    private String description;
    private Integer modifiedBy;
    private Date modifiedDate;

    public ProcessingSchedule(String code, String name) {
        this(code, name, null);
    }

    public ProcessingSchedule(String code, String name, String description){
        this(null, code, name, description, null, null);
    }

    public void validate() {
        if (code == null || code.isEmpty()) throw new DataException("Schedule can not be saved without its code.");
        if (name == null || name.isEmpty())
            throw new DataException("Schedule can not be saved without its name.");
    }
}
