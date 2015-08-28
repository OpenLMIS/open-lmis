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
package org.openlmis.odkapi.repository;

import org.openlmis.odkapi.domain.ODKAccount;
import org.openlmis.odkapi.repository.mapper.ODKAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class ODKAccountRepository {
    @Autowired
    private ODKAccountMapper odkAccountMapper;

    public ODKAccount getODKAccountById(Long id)
   {
       return odkAccountMapper.getODKAccountById(id);
   }

    public ODKAccount getODKAccountByDeviceId(String deviceId)
    {
        return odkAccountMapper.getODKAccountByDeviceId(deviceId);
    }

    public ODKAccount getODKAccountBySIMSerial(String SIMSerial)
    {
        return odkAccountMapper.getODKAccountBySIMSerial(SIMSerial);
    }

    public ODKAccount getODKAccountByPhoneNumber(String phoneNumber)
    {
        return odkAccountMapper.getODKAccountByPhoneNumber(phoneNumber);
    }

    public ODKAccount getODKAccountBySubscriberId(String subscriberId)
    {
        return odkAccountMapper.getODKAccountBySubscriberId(subscriberId);
    }

    public ODKAccount getODKAccountByODKUserName(String odkUserName)
    {
        return odkAccountMapper.getODKAccountByODKUserName(odkUserName);
    }

    public ODKAccount getODKAccountByODKEmail(String odkEMail)
    {
        return odkAccountMapper.getODKAccountByODKEmail(odkEMail);
    }
}

