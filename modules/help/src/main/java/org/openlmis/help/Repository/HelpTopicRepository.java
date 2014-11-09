
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

import org.openlmis.help.Repository.mapper.HelpTopicMapper;
import org.openlmis.help.domain.HelpTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class HelpTopicRepository {
@Autowired
    private HelpTopicMapper mapper;
    public Long insert(HelpTopic helpTopic){
        Long id;

        id= this.mapper.insert(helpTopic);
//        helpTopic.setId(id);
        return id;
    }
    public List<HelpTopic> getHelpTopicList() {
       return this.mapper.getList();
    }

    public HelpTopic get(Long id) {
        return this.mapper.get(id);
    }
    public List<HelpTopic> getUserRoleHelpTopicList(Long loggedUserId) {
        return this.mapper.loadUserRolHelpTopicList(loggedUserId);
    }
    public void createRootHelpTopic(HelpTopic helpTopic){
        this.mapper.createRootHelpTopic(helpTopic);
    }
    public List<HelpTopic> loadRootHelpTopicList(){
        return this.mapper.loadRootHelptopicList();
    }
    public List<HelpTopic> loadChildrenOfHelpTopic(HelpTopic helpTopic){
        return this.mapper.getHelpTopicChildrenList(helpTopic.getId());

    }

    public void update(HelpTopic helpTopic) {
        this.mapper.updateHelpTopic(helpTopic);
    }

    public List<HelpTopic> loadRootRoleHelpTopicList(Long loggedUserId) {
        return this.mapper.loadRoleRootHelptopicList(loggedUserId);
    }
public List<HelpTopic> loadChildrenOfHelpTopic(Long loggedUserId, HelpTopic parentHTopic) {
    return this.mapper.getRoleHelpTopicChildrenList(loggedUserId,parentHTopic.getId());
    }

}
