package org.openlmis.help.Repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.help.domain.HelpContent;
import org.openlmis.help.domain.HelpTopic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by seifu on 10/20/2014.
 */
@Deprecated
@Repository
public interface HelpContentMapper {
    @Insert({"INSERT INTO elmis_help",
            "(helpTopicId, name, htmlcontent, imagelink, createddate, createdby, modifiedby, modifieddate) ",
            "VALUES(#{helpTopic.id}, #{name}, #{htmlContent}, #{imageLink}, #{createdDate}, #{createdBy}, #{modifiedBy}, #{modifiedDate})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(HelpContent helpContent);

    /*

     */
    @Select("SELECT eh.* FROM elmis_help eh INNER JOIN elmis_help_topic et on eh.helpTopicId=et.id  ")
    @Results({
            @Result(column = "helpTopicId", property = "helpTopic.id")

    })
    List<HelpContent> getHelpContentList();

    /*

     */
    @Select("SELECT * FROM elmis_help where helptopicid = #{id} ")
    List<HelpContent> getHelpTopcicContentList(@Param(value = "id") Long id);

    /*

    */
    @Select("SELECT * FROM elmis_help where id = #{id} ")
    HelpContent get(@Param(value = "id") Long id);

    @Update("UPDATE elmis_help" +
            "   SET name= #{name}," +
            " modifiedby=#{modifiedBy}, " +
            "htmlcontent=#{htmlContent}, " +
            "imagelink=#{imageLink}, " +
            "modifieddate=#{modifiedDate}," +
            " helptopicid=#{helpTopic.id}" +
            " WHERE id=#{id};")
    void update(HelpContent helpContent);
}
