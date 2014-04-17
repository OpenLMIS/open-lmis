/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;

import org.openlmis.core.service.ProgramService;

import org.openlmis.core.service.RoleRightsService;
import org.openlmis.core.service.SupervisoryNodeService;
import org.openlmis.report.mapper.UserSummaryReportMapper;
import org.openlmis.report.model.ReportData;

import org.openlmis.report.model.params.UserSummaryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@NoArgsConstructor
public class UserSummaryReportProvider extends ReportDataProvider{
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
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);

        return reportMapper.getReport(getReportFilterData(filterCriteria), null, rowBounds);
    }

    @Override
    public List<? extends ReportData> getMainReportData(Map<String, String[]> filterCriteria, Map<String, String[]> SortCriteria, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page - 1) * pageSize, pageSize);
        return reportMapper.getReport(getReportFilterData(filterCriteria), SortCriteria, rowBounds);
    }

    public UserSummaryParams getReportFilterData(Map<String, String[]> filterCriteria) {

        if (filterCriteria != null) {
            //  userSummaryParam = new UserSummaryParams();
            //userSummaryParam.setRoleId(StringUtils.isBlank(filterCriteria.get("role")[0]) ? 0 : Long.parseLong(filterCriteria.get("role")[0])); //defaults to 0
            //userSummaryParam.setProgramId(StringUtils.isBlank(filterCriteria.get("program")[0]) ? 0 : Long.parseLong(filterCriteria.get("program")[0]));
            // userSummaryParam.setSupervisoryNodeId(StringUtils.isBlank(filterCriteria.get("supervisoryNode")[0]) ? 0 : Long.parseLong(filterCriteria.get("supervisoryNode")[0]));

            // summarize the filters now.
           /* String summary = "Program: "
                    .concat(programService.getById(userSummaryParam.getProgramId()).getName())
                    .concat("\nRole:")
                    .concat(roleRightsService.getRole(userSummaryParam.getRoleId()).getName());

            if(userSummaryParam.getSupervisoryNodeId() != 0){
                summary.concat("\nSupervisoryNodes: ")
                        .concat(supervisoryNodeService.getParent(userSummaryParam.getSupervisoryNodeId()).getName());
            }
             */


        }

        return userSummaryParam;
    }

    @Override
    public String getFilterSummary(Map<String, String[]> params) {
        return getReportFilterData(params).toString();

    }


}