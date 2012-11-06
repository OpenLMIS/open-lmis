package org.openlmis.admin.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.server.ResultActions;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import java.util.ArrayList;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.server.setup.MockMvcBuilders.standaloneSetup;


@ContextConfiguration(locations = "classpath*:/applicationContext-admin-web.xml")

@RunWith(SpringJUnit4ClassRunner.class)
public class RnRTemplateControllerIT {

    @Autowired
    RnRTemplateController controller;

    @Test
    public void shouldGetAllMasterRnRColumns() throws Exception {
        ResultActions resultActions = standaloneSetup(controller).setViewResolvers(contentNegotiatingViewResolver()).build()
                .perform(get("/admin/rnr/master/columns.json"));

        assertEquals("{\"rnrColumnList\":[{\"id\":1,\"name\":\"Medicine_Name\",\"description\":\"First test medicine\"," +
                                        "\"position\":1,\"label\":\"Medicine Name\",\"defaultValue\":\"M\"," +
                                        "\"dataSource\":\"Derived\",\"formula\":\"a+b+c\",\"indicator\":\"X\",\"used\":false,\"visible\":false}]}", resultActions.andReturn().getResponse().getContentAsString());

//        resultActions.andExpect(content().type(MediaType.APPLICATION_JSON_VALUE))
//                .andExpect(content().string("{\"rnrColumnList\":[{\"id\":1,\"name\":\"Medicine_Name\",\"description\":\"First test medicine\"," +
//                        "\"position\":1,\"label\":\"Medicine Name\",\"defaultValue\":\"M\"," +
//                        "\"dataSource\":\"Derived\",\"formula\":\"a+b+c\",\"indicator\":\"X\",\"used\":false,\"visible\":false}]}"));
    } // TODO : IT test should setup its own data

    private ContentNegotiatingViewResolver contentNegotiatingViewResolver() {
        ContentNegotiatingViewResolver viewResolver = new ContentNegotiatingViewResolver();
        viewResolver.setMediaTypes(new HashMap<String, String>() {{
            put("json", "application/json");
        }});
        viewResolver.setDefaultViews(new ArrayList<View>() {{
            add(new MappingJacksonJsonView());
        }});
        return viewResolver;
    }
}
