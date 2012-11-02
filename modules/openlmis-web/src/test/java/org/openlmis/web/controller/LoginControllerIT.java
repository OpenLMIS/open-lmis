package org.openlmis.web.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;

@ContextConfiguration(locations = "classpath*:applicationTestContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginControllerIT {


    @Autowired
    private LoginController loginController;

    @Test
    public void shouldLoadLoginPage() throws Exception {
        standaloneSetup(loginController).build()
                .perform(get("/login"))
                .andExpect(status().isOk());
    }
}
