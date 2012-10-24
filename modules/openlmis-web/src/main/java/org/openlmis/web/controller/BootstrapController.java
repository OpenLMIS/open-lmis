package org.openlmis.web.controller;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BootstrapController {

    public final String CONTEXT_PATH = "contextPath";

    @ResponseBody
    @RequestMapping(value = "properties.json", method = RequestMethod.GET)
    public String properties(HttpServletRequest request) throws JSONException {
        JSONObject properties = new JSONObject().put(CONTEXT_PATH, request.getContextPath());
        return "openlmis.initProperties(" + properties.toString() + ")";
    }

}
