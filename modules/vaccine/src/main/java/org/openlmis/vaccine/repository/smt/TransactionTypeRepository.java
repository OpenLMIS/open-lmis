/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.smt;

import org.openlmis.vaccine.domain.smt.TransactionType;
import org.openlmis.vaccine.repository.mapper.smt.TransactionTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Deprecated
public class TransactionTypeRepository {
    @Autowired
    private TransactionTypeMapper transactionTypeMapper;

    public TransactionType getByName(String name){
        return transactionTypeMapper.getByName(name);
    }

    public void delete(Long id){

        transactionTypeMapper.delete(id);
    }

    public void insert(TransactionType transactionType){
        transactionTypeMapper.insert(transactionType);
    }

    public void update(TransactionType transactionType){
        transactionTypeMapper.update(transactionType);
    }

    public List<TransactionType> getList(){
         return transactionTypeMapper.getList();
    }


    public TransactionType get(Long id) {
        return transactionTypeMapper.getById(id);
    }

    public List<TransactionType> search(String param) {
        return transactionTypeMapper.search(param);
    }
}
