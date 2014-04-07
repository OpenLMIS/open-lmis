/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
/**
 * Created with IntelliJ IDEA.
 * User: Messay Yohannes <deliasmes@gmail.com>
 * To change this template use File | Settings | File Templates.
 */
package org.openlmis.odkapi.domain;

import javax.xml.bind.annotation.*;
import java.util.List;
import org.openlmis.odkapi.domain.ODKXFormDTO;


@XmlRootElement(name = "xforms")
public class ODKXFormList {

    private String xmlns = "http://openrosa.org/xforms/xformsList";

    @XmlAttribute(name="xmlns")
    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }
    @XmlElement(name = "xform",required = true)
    public List<ODKXFormDTO> odkxFormList;

    public List<ODKXFormDTO> getData() {
        return odkxFormList;
    }

    public ODKXFormList(String xmlns,List<ODKXFormDTO> odkxFormList) {
        this();
        this.xmlns = xmlns;
        this.odkxFormList = odkxFormList;
    }


    public ODKXFormList() {
    }

}

