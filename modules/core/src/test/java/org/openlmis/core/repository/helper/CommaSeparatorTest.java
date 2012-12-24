package org.openlmis.core.repository.helper;

import org.junit.Test;
import org.openlmis.core.domain.BaseModel;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CommaSeparatorTest {

    @Test
    public void shouldGetIdsInCommaSeparatedForm() {
        List<BaseModel> models = new ArrayList<>();
        models.add(model(1));
        models.add(model(2));
        CommaSeparator commaSeparator = new CommaSeparator();
        String result = commaSeparator.commaSeparateIds(models);
        assertThat(result, is("{1, 2}"));
    }

    private BaseModel model(final int i) {
        return new BaseModel() {
            @Override
            public Integer getId() {
                return i;
            }
        };
    }
}
