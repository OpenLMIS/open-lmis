

package org.openlmis.vaccine.repository.mapper.smt;

import org.apache.ibatis.annotations.*;
import org.openlmis.vaccine.domain.Status;
import org.openlmis.vaccine.domain.smt.TransactionType;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Deprecated
public interface StatusMapper {

    @Select("Select * from received_status")
    @Results({
            @Result(column = "transactionTypeId", javaType = TransactionType.class, property = "transactionType",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.TransactionTypeMapper.getById"))
    })
    List<Status> getAll();

    @Select("select * from received_status where id = #{id}")
    Status getById(Long id);

    @Select("select * from received_status where id = #{id}")
    @Results({
            @Result(column = "transactionTypeId", javaType = TransactionType.class, property = "transactionType",
                    one = @One(select = "org.openlmis.vaccine.repository.mapper.smt.TransactionTypeMapper.getById"))
    })
    Status get(Long id);

    @Select("INSERT INTO received_status(\n" +
            "            name, transactiontypeid, createdby, createddate)\n" +
            "    VALUES (#{name}, #{transactionType.id}, #{createdBy},  COALESCE(#{createdDate}, NOW()) );\n")
    void insert(Status receivedStatus);

    @Update("UPDATE received_status\n" +
            "   SET name=#{name}, transactiontypeid=#{transactionType.id}, modifiedby=#{modifiedBy}, modifiedDate=#{modifiedDate}\n" +
            " WHERE id= #{id} ")
    void update(Status receivedStatus);

    @Delete("delete from received_status where id = #{id}")
    void delete(Long id);

    @Select("select * from received_status")
    List<Status> getList();

    @Select("select * from received_status where LOWER(name) LIKE  '%'|| LOWER(#{param}) ||'%'")
    List<Status> search(String param);
}
