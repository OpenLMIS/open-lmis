/*
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 * Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;

import org.openlmis.core.domain.Role;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.service.ProgramService;

import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.report.mapper.UserSummaryReportMapper;
import org.openlmis.report.model.ReportData;

import org.openlmis.report.model.dto.Program;
import org.openlmis.report.model.params.UserSummaryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class UserSummaryReportProvider extends ReportDataProvider {
    private UserSummaryReportMapper reportMapper;

    private UserSummaryParams userSummaryParam = null;

    @Autowired
    private ProgramService programService;

    @Autowired
    private SupervisoryNodeService supervisoryNodeService;
    @Autowired
    private RoleRightsService roleRightsService;


    @Autowired
    public UserSummaryReportProvider(UserSummaryReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getResultSet(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

        return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
    }

    @Override
    public List<? extends ReportData> getReportBody(Map<String, String[]> filterCriteria, Map<String, String[]> sortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), sortCriteria, rowBounds);
    }

    public UserSummaryParams getReportFilterData(Map<String, String[]> filterCriteria) {

        if (filterCriteria != null) {
            userSummaryParam = new UserSummaryParams();
            Long userId = StringUtils.isBlank(filterCriteria.get("roleId")[0]) ? 0 : Long.parseLong(filterCriteria.get("roleId")[0]);
            if (filterCriteria != null) {
                userSummaryParam = new UserSummaryParams();
                userSummaryParam.setRoleId(StringUtils.isBlank(filterCriteria.get("roleId")[0]) ? 0 : Long.parseLong(filterCriteria.get("roleId")[0])); //defaults to 0
                userSummaryParam.setProgramId(StringUtils.isBlank(filterCriteria.get("programId")[0]) ? 0 : Long.parseLong(filterCriteria.get("programId")[0]));
                userSummaryParam.setSupervisoryNodeId(StringUtils.isBlank(filterCriteria.get("supervisoryNodeId")[0]) ? 0 : Long.parseLong(filterCriteria.get("supervisoryNodeId")[0]));
            }

        }

        return userSummaryParam;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        UserSummaryParams userSummaryParams = this.getReportFilterData(params);
        StringBuilder filterString = new StringBuilder();
        Program program = null;
        Role role = null;
        String programName="";
        String roleName="";
        String superVisoryName="";
        SupervisoryNode supervisoryNode = null;
        if (userSummaryParams.getProgramId()==0) {
            filterString.append("Program : All");
        }else {
            program= this.reportMapper.getProgram(userSummaryParams.getProgramId());
            programName=program==null?"": program.getName();
            filterString.append("Program : ").append(programName);
        }
        if (userSummaryParams.getRoleId()==0) {
            filterString.append(", Role : All");

        }else {
            role= this.reportMapper.getRole(userSummaryParams.getRoleId());
            roleName=role==null?"": role.getName();
            filterString.append(", Role : ").append(roleName);
        }
        if (userSummaryParams.getSupervisoryNodeId()==0) {
            filterString.append(", Supervisory Node : All");
        }else {
            supervisoryNode= this.reportMapper.getSuperVisoryNode(userSummaryParams.getSupervisoryNodeId());
            superVisoryName=supervisoryNode==null?"": supervisoryNode.getName();
            filterString.append(", Supervisory Node : ").append(superVisoryName);
        }
        return filterString.toString();

    }

    public List<HashMap> getUserAssignments() {
        return reportMapper.getUserRoleAssignments();
    }


}