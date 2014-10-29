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

import org.openlmis.core.domain.Role;
import org.openlmis.core.service.RoleRightsService;
import org.openlmis.help.Repository.HelpContentRepository;
import org.openlmis.help.Repository.HelpTopicRepository;
import org.openlmis.help.Repository.HelpTopicRoleRepository;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.openlmis.help.domain.HelpTopicRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class HelpTopicService {
    @Autowired
    private HelpTopicRepository repository;
    @Autowired
    private RoleRightsService rightsService;
    @Autowired
    private HelpTopicRoleRepository roleRepository;
    @Autowired
    private HelpContentRepository contentRepository;
    private List<HelpTopic> roleHelpTopicList;

    public void addHelpTopic(HelpTopic helpTopic) {
        Long topicId = this.repository.insert(helpTopic);
        if(helpTopic.isCategory()) {
            List<HelpTopicRole> helpTopicRoleList = helpTopic.getRoleList();
            for (HelpTopicRole helpTopicRole : helpTopicRoleList) {

                if (helpTopicRole.isCurrentlyAssigned()) {
//                helpTopic.setId(topicId);
                    //System.out.println(" topic id is " + helpTopic.getId());
                    helpTopicRole.setHelpTopic(helpTopic);
                    this.roleRepository.addHelpTopicRole(helpTopicRole);
                }

            }
        }

    }

    public List<HelpTopic> getHelpTopicList() {
        return this.repository.getHelpTopicList();
    }

    public HelpTopic get(Long id) {
        HelpTopic helpTopic = this.repository.get(id);
        List<HelpTopicRole> helpTopicRoleList = this.loadHelptopicRolesAssigmentInfo(helpTopic);
        helpTopic.setRoleList(helpTopicRoleList);
        return helpTopic;
    }

    public HelpTopic intializeHelpTopicForCreate() {
        HelpTopic helpTopic = new HelpTopic();
        List<HelpTopicRole> helpTopicRoleList = new ArrayList<>();
        List<Role> roleList = this.rightsService.getAllRoles();
        for (Role role : roleList) {
            HelpTopicRole helpTopicRole = new HelpTopicRole();
//    helpTopicRole.setHelpTopic(helpTopic);
            helpTopicRole.setUserRole(role);
            helpTopicRoleList.add(helpTopicRole);
        }
        helpTopic.setRoleList(helpTopicRoleList);
        return helpTopic;
    }

    public List<HelpTopic> getUserRoleHelpTopicList(Long loggedUserId) {
        List<HelpTopic> userHelpTopicList = this.repository.getUserRoleHelpTopicList(loggedUserId);
        //System.out.println(" uz " + loggedUserId + " list");
        for (HelpTopic helpTopic : userHelpTopicList) {
            List<HelpContent> helpContentList = contentRepository.getHelpContentList(helpTopic);
            helpTopic.setHelpContentList(helpContentList);
        }

        return userHelpTopicList;
    }

    public List<HelpTopic> buildRoleHelpTopicTree(Long loggedUserId,HelpTopic parentHTopic, boolean isRootTopicLoad) {
        List<HelpTopic> childHelpTopicList = null;
        if (isRootTopicLoad) {
            this.roleHelpTopicList = new ArrayList<>();
            childHelpTopicList = this.repository.loadRootRoleHelpTopicList(loggedUserId);
        } else {
            childHelpTopicList = this.repository.loadChildrenOfHelpTopic(loggedUserId,parentHTopic);
        }
        this.roleHelpTopicList.addAll(childHelpTopicList);
        for (HelpTopic helpTopic : childHelpTopicList) {
            childHelpTopicList = this.buildRoleHelpTopicTree(loggedUserId,helpTopic, false);
        }
        return this.roleHelpTopicList;
    }
    public List<HelpTopicRole> loadHelptopicRolesAssigmentInfo(HelpTopic helpTopic) {

        List<HelpTopicRole> rolesAssignedList = this.roleRepository.loadHelpTopicRoleList(helpTopic);

        List<Role> rolesNotAssigned = this.roleRepository.loadRolesNotAssignedForHelpTopic(helpTopic);

        for (Role role : rolesNotAssigned) {
            HelpTopicRole helpTopicRole = new HelpTopicRole();
            helpTopicRole.setUserRole(role);
            rolesAssignedList.add(helpTopicRole);
        }

        return rolesAssignedList;
    }

    public void updateHelpTopicRole(HelpTopic helpTopic) {
        if (helpTopic.isCategory()) {
            List<HelpTopicRole> helpTopicRoleList = helpTopic.getRoleList();
            List<HelpTopicRole> newlyTopicRoleList = new ArrayList<>();
            List<HelpTopicRole> removedHelpTopicRoleList = new ArrayList<>();
            for (HelpTopicRole helpTopicRole : helpTopicRoleList) {
                if (helpTopicRole.isCurrentlyAssigned() && !helpTopicRole.isPrevioslyAssigned()) {
                    newlyTopicRoleList.add(helpTopicRole);
                } else if (!helpTopicRole.isCurrentlyAssigned() && helpTopicRole.isPrevioslyAssigned()) {
                    removedHelpTopicRoleList.add(helpTopicRole);
                }

            }
            for (HelpTopicRole helpTopicRole : newlyTopicRoleList) {
                helpTopicRole.setHelpTopic(helpTopic);
                this.roleRepository.addHelpTopicRole(helpTopicRole);
            }
            this.roleRepository.removeTopicRoleList(removedHelpTopicRoleList);
        }
        this.repository.update(helpTopic);

    }

    /*

     */
    public void createRootHelpTopic(HelpTopic helpTopic) {
        this.repository.createRootHelpTopic(helpTopic);
    }

    public List<HelpTopic> buildHelpTopicTree(HelpTopic parentHTopic, boolean isRootTopicLoad) {
        List<HelpTopic> childHelpTopicList = null;
        if (isRootTopicLoad) {
            this.helpTopicList = new ArrayList<>();
            childHelpTopicList = this.repository.loadRootHelpTopicList();
        } else {
            childHelpTopicList = this.repository.loadChildrenOfHelpTopic(parentHTopic);
        }
        this.helpTopicList.addAll(childHelpTopicList);
        for (HelpTopic helpTopic : childHelpTopicList) {
            childHelpTopicList = this.buildHelpTopicTree(helpTopic, false);
        }
        return this.helpTopicList;
    }

    public List<HelpTopic> loadChildrenOfHelpTopic(HelpTopic helpTopic) {
        return this.loadChildrenOfHelpTopic(helpTopic);

    }

    private List<HelpTopic> helpTopicList;
}
