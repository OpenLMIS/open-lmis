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
package org.openlmis.odkapi.repository.mapper;

import org.apache.ibatis.annotations.Select;
import org.openlmis.odkapi.domain.ODKAccount;
import org.springframework.stereotype.Repository;

@Repository
public interface ODKAccountMapper
{
    @Select("SELECT * FROM odk_account where id = #{id}")
    public ODKAccount getODKAccountById(Long id);

    @Select("SELECT * FROM odk_account where deviceId = #{deviceId}")
    public ODKAccount getODKAccountByDeviceId(String deviceId);

    @Select("SELECT * FROM odk_account where SIMSerial = #{simSerial}")
    public ODKAccount getODKAccountBySIMSerial(String simSerial);

    @Select("SELECT * FROM odk_account where phoneNumber = #{phoneNumber}")
    public ODKAccount getODKAccountByPhoneNumber(String phoneNumber);

    @Select("SELECT * FROM odk_account where subscriberId = #{subscriberId}")
    public ODKAccount getODKAccountBySubscriberId(String subscriberId);

    @Select("SELECT * FROM odk_account where ODKUserName = #{odkUserName}")
    public ODKAccount getODKAccountByODKUserName(String odkUserName);

    @Select("SELECT * FROM odk_account where ODKEmail = #{odkEMail}")
    public ODKAccount getODKAccountByODKEmail(String odkEMail);

}
