package org.openlmis.rnr.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:applicationContext-requisition.xml")
public class RnrColumnMapperIT {

    @Autowired
    RnrColumnMapper rnrColumnMapper;

    @Test
    public void shouldSetMasterColumnNameAsLabel() throws Exception {
        RnrColumn rnrColumn = rnrColumnMapper.fetchAllMasterRnRColumns().get(0);

        assertThat(rnrColumn.getLabel(), is(rnrColumn.getName()));
    }
}
