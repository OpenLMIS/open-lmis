/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.help.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.openlmis.core.domain.BaseModel;
import org.openlmis.upload.Importable;
import org.openlmis.upload.annotation.ImportField;


@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelpContent extends BaseModel{

    private HelpTopic helpTopic;
//    private
    private String name;
    private String htmlContent;
    private String imageLink;
//    @JsonIgnore
//    @Override
//    public String toString() {
//        final StringBuffer sb = new StringBuffer("HelpContent{");
//        sb.append("id=").append(id);
//
//        sb.append("helpTopic=").append(helpTopic.getId());
//        sb.append(", name='").append(name).append('\'');
//        sb.append(", htmlContent='").append(htmlContent).append('\'');
//        sb.append(", imageLink='").append(imageLink).append('\'');
//        sb.append('}');
//        return sb.toString();
//    }
}
