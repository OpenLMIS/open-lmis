/*
 * Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 *
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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
