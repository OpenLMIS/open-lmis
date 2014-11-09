/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */
package org.openlmis.help.service;

import org.openlmis.help.Repository.HelpContentRepository;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Deprecated
@Component
public class HelpContentService {
    @Autowired
    private HelpContentRepository repository;
    public void addNewHelpContent(HelpContent helpContent){
        this.repository.add(helpContent);
    }
    public List<HelpContent> loadAllHelpContentList(){
        return this.repository.getHelpContentList();
    }
    public List<HelpContent> loadHelpTopicHelpContentList(HelpTopic helpTopic){
        return this.repository.getHelpContentList(helpTopic);

    }
    public HelpContent getHelpContentById(Long id){
        return this.repository.get(id);
    }


    public void updateHelpContent(HelpContent helpContent) {
        this.repository.updateHelpContent(helpContent);
    }
}
