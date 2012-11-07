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
        int existingProgramId = 1;
        ResultActions resultActions = standaloneSetup(controller).setViewResolvers(contentNegotiatingViewResolver()).build()
                .perform(get("/admin/rnr/"+ existingProgramId +"/columns.json"));

        assertEquals("{\"rnrColumnList\":[{\"id\":1,\"name\":\"foo\",\"description\":\"foo is a column\",\"position\":1,\"label\":\"Foo\",\"defaultValue\":\"foo\"," +
                "\"dataSource\":\"Derived\",\"formula\":\"a+b+c\",\"indicator\":\"F\",\"used\":false,\"visible\":false}," +
                "{\"id\":2,\"name\":\"bar\",\"description\":\"bar is not foo\",\"position\":1,\"label\":\"Bar\",\"defaultValue\":\"bar\",\"dataSource\":\"Derived\"," +
                "\"formula\":\"a+b+c\",\"indicator\":\"B\",\"used\":true,\"visible\":false}]}", resultActions.andReturn().getResponse().getContentAsString());
    }

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
