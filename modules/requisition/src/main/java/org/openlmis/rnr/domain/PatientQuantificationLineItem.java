package org.openlmis.rnr.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;

@Data
@JsonDeserialize
@AllArgsConstructor
@NoArgsConstructor
public class PatientQuantificationLineItem extends LineItem {

    private String category;
    private Integer total;

    @Override
    public boolean compareCategory(LineItem lineItem) {
        return false;
    }

    @Override
    public String getCategoryName() {
        return category;
    }

    @Override
    public String getValue(String columnName) throws NoSuchFieldException, IllegalAccessException {
        Field field = PatientQuantificationLineItem.class.getDeclaredField(columnName);
        field.setAccessible(true);
        Object fieldValue = field.get(this);
        String value = (fieldValue == null) ? "" : fieldValue.toString();
        return value;
    }

    @Override
    public boolean isRnrLineItem() {
        return false;
    }
}
