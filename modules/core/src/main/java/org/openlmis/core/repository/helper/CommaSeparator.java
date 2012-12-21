package org.openlmis.core.repository.helper;

import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.List;

public class CommaSeparator<T extends BaseModel> {

    public String commaSeparate(List<T> list) {
        List<Integer> ids = new ArrayList<>();

        for (T t : list) {
            ids.add(t.getId());
        }
        return ids.toString().replace("[", "{").replace("]", "}");
    }
}
