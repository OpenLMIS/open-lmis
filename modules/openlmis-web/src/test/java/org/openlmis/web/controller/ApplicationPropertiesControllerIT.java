package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;


@ContextConfiguration(locations = "classpath*:/applicationTestContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class ApplicationPropertiesControllerIT {

    @Autowired
    BootstrapController bootstrapController;

    @Test
    public void shouldReturnContextPathInProperties() throws Exception {
        standaloneSetup(bootstrapController).build()
                .perform(get("/openlmis/properties.json").contextPath("/openlmis").servletPath("/all.json"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"contextPath\":\"/openlmis\"}"));
    }

}
