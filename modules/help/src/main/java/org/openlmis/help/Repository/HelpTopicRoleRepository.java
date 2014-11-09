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

import org.openlmis.core.domain.Role;
import org.openlmis.help.Repository.mapper.HelpTopicRoleMapper;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.help.domain.HelpTopicRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class HelpTopicRoleRepository {
    @Autowired
    private HelpTopicRoleMapper mapper;
    public void addHelpTopicRoleList(List<HelpTopicRole> helpTopicRoleList){
        for(HelpTopicRole helpTopicRole: helpTopicRoleList){
            this.mapper.insert(helpTopicRole);
        }
    }
    public void addHelpTopicRole(HelpTopicRole helpTopicRole){
        this.mapper.insert(helpTopicRole);
    }
    public List<HelpTopicRole> loadHelpTopicRoleList(HelpTopic helpTopic){
        return this.mapper.loadHelpTopicRolesAssignment(helpTopic.getId());
    }
    public List<Role> loadRolesNotAssignedForHelpTopic(HelpTopic helpTopic){
        return this.mapper.loadRolesNotAssignedForHelpTopic(helpTopic.getId());
    }

    public void removeTopicRoleList(List<HelpTopicRole> removedHelpTopicRoleList) {
        for(HelpTopicRole helpTopicRole: removedHelpTopicRoleList){
            this.mapper.delete(helpTopicRole.getId());
        }
    }
}
