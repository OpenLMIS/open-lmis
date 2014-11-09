/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.help.Repository;

import org.openlmis.help.Repository.mapper.HelpContentMapper;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
@Deprecated
public class HelpContentRepository {
    @Autowired
    private HelpContentMapper mapper;
    public void add(HelpContent helpContent){
        //System.out.println(" help content to be save is "+helpContent);
        this.mapper.insert(helpContent);
    }
    public List<HelpContent> getHelpContentList(){
       return this.mapper.getHelpContentList();
    }
    public List<HelpContent> getHelpContentList(HelpTopic helpTopic){
       return this.mapper.getHelpTopcicContentList( helpTopic.getId());
    }
    public HelpContent get(Long id){
        return this.mapper.get(id);
    }

    public void updateHelpContent(HelpContent helpContent) {
        this.mapper.update(helpContent);
    }
}
