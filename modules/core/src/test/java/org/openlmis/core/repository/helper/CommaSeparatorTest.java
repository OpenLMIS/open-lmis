/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.core.repository.helper;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.db.categories.UnitTests;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Category(UnitTests.class)
public class CommaSeparatorTest {

    @Test
    public void shouldGetIdsInCommaSeparatedForm() {
        List<BaseModel> models = new ArrayList<>();
        models.add(model(1L));
        models.add(model(2L));
        CommaSeparator commaSeparator = new CommaSeparator();
        String result = commaSeparator.commaSeparateIds(models);
        assertThat(result, is("{1, 2}"));
    }

    private BaseModel model(final Long i) {
        return new BaseModel() {
            @Override
            public Long getId() {
                return i;
            }
        };
    }
}
