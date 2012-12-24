package org.openlmis.core.repository.helper;

import org.openlmis.core.domain.BaseModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommaSeparator<T extends BaseModel> {

    public String commaSeparateIds(List<T> list) {
        List<Integer> ids = new ArrayList<>();

        for (T t : list) {
            ids.add(t.getId());
        }
        return ids.toString().replace("[", "{").replace("]", "}");
    }
}
