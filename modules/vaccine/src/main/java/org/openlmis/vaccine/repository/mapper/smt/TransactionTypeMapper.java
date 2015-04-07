/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 *   Copyright © 2013 VillageReach
 *
 *   This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *    
 *   This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *   You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.openlmis.vaccine.domain.smt.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionTypeMapper {

    @Select("Select * from transaction_types where id = #{id}")
    TransactionType getById(Long id);

    @Select("Select * from transaction_types where name = #{name}")
    TransactionType getByName(String name);

    @Select("INSERT INTO transaction_types(\n" +
            "            name, createdby, createddate)\n" +
            "    VALUES (#{name}, #{createdBy},  COALESCE(#{createdDate}, NOW()) );\n")
    void insert(TransactionType transactionType);

    @Update("UPDATE transaction_types\n" +
            "   SET name=#{name}, modifiedby=#{modifiedBy}, modifiedDate=#{modifiedDate}\n" +
            " WHERE id= #{id} ")
    void update(TransactionType transactionType);

    @Delete("delete from transaction_types where id = #{id}")
    void delete(Long id);

    @Select("select * from transaction_types")
    List<TransactionType> getList();

    @Select("select * from transaction_types where LOWER(name) LIKE  '%'|| LOWER(#{param}) ||'%'")
    List<TransactionType> search(String param);
}
