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
package org.openlmis.odkapi.repository;

import org.openlmis.odkapi.domain.ODKSubmission;
import org.openlmis.odkapi.repository.mapper.ODKSubmissionMapper;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public class ODKSubmissionRepository {
    @Autowired
    private ODKSubmissionMapper odkSubmissionMapper;

    public void insert(ODKSubmission odkSubmission)
    {
        odkSubmissionMapper.insert(odkSubmission);
    }

    public Long getLastSubmissionId()
    {
        return odkSubmissionMapper.getLastSubmissionId();
    }
}
