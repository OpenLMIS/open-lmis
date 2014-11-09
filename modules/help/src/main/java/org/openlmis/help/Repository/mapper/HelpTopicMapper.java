package org.openlmis.help.Repository.mapper;

import org.apache.ibatis.annotations.*;
import org.openlmis.help.domain.HelpTopic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by seifu on 10/20/2014.
 */
@Repository
public interface HelpTopicMapper {
    @Insert({"INSERT INTO elmis_help_topic",
            "( level, name, created_by, modifiedby, modifiedDate) ",
            "VALUES",
            "( #{level}, #{name}, #{createdBy}, #{modifiedBy}, #{modifiedDate}) )"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long createRootHelpTopic(HelpTopic helpTopic);

    /*

     */
    /*

     */
    @Insert({"INSERT INTO elmis_help_topic",
            "( level, name, created_by, modifiedby, modifiedDate, parent_help_topic_id, html_content, is_category) ",
            "VALUES",
            "( #{level}, #{name}, #{createdBy}, #{modifiedBy}, #{modifiedDate} , #{parentHelpTopic},#{htmlContent},#{category})"})
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    Long insert(HelpTopic helpTopic);

    /*

     */
    @Update("UPDATE elmis_help_topic " +
            "   SET name= #{name}," +
            " level= #{level}," +
            " modifiedby=#{modifiedBy}, " +
            "html_content=#{htmlContent}, " +
            "modifieddate=#{modifiedDate}" +
            " WHERE id=#{id};")
    void updateHelpTopic(HelpTopic HelpTopic);

    /*
    this selects all help topics
     */
    @Select("SELECT * FROM elmis_help_topic  ")
    List<HelpTopic> getList();

    /*

     */
    @Select("SELECT * FROM elmis_help_topic where parent_help_topic_id is null ")
    @Results({
            @Result(column = "parent_help_topic_id", property = "parentHelpTopic"),
            @Result(column = "is_category", property = "category")

    })
    List<HelpTopic> loadRootHelptopicList();

    /*

     */
    @Select("SELECT * FROM elmis_help_topic where parent_help_topic_id=#{parentId}  ")
    @Results({
            @Result(column = "parent_help_topic_id", property = "parentHelpTopic"),
            @Result(column = "html_content", property = "htmlContent"),
            @Result(column = "is_category", property = "category")

    })
    List<HelpTopic> getHelpTopicChildrenList(@Param(value = "parentId") Long parentId);

    @Select("SELECT * FROM elmis_help_topic where id =#{id} ")
    @Results({
            @Result(column = "parent_help_topic_id", property = "parentHelpTopic"),
            @Result(column = "html_content", property = "htmlContent"),
            @Result(column = "is_category", property = "category")

    })
    HelpTopic get(Long id);

    @Select("SELECT DISTINCT " +
            "  elmis_help_topic.* " +
            "FROM " +
            "  public.elmis_help_topic " +
            "   INNER JOIN public.elmis_help_topic_roles " +
            " ON elmis_help_topic_roles.help_topic_id = elmis_help_topic.id" +
            "  INNER JOIN public.roles" +
            " ON elmis_help_topic_roles.role_id = roles.id " +
            "  INNER JOIN public.role_assignments" +
            " ON role_assignments.roleid = roles.id " +
            "  INNER JOIN public.users " +
            " ON  role_assignments.userid = users.id " +
            "WHERE public.users.id =#{id} ")
    List<HelpTopic> loadUserRolHelpTopicList(@Param(value = "id") Long id);

    @Select("SELECT DISTINCT " +
            "  elmis_help_topic.* " +
            "FROM " +
            "  public.elmis_help_topic " +
            "   INNER JOIN public.elmis_help_topic_roles " +
            " ON elmis_help_topic_roles.help_topic_id = elmis_help_topic.id" +
            "  INNER JOIN public.roles" +
            " ON elmis_help_topic_roles.role_id = roles.id " +
            "  INNER JOIN public.role_assignments" +
            " ON role_assignments.roleid = roles.id " +
            "  INNER JOIN public.users " +
            " ON  role_assignments.userid = users.id " +
            "WHERE public.users.id =#{id} and parent_help_topic_id is null ")
    @Results({
            @Result(column = "parent_help_topic_id", property = "parentHelpTopic"),
            @Result(column = "is_category", property = "category")

    })
    List<HelpTopic> loadRoleRootHelptopicList(Long loggedUserId);
    @Select("SELECT DISTINCT " +
            "  elmis_help_topic.* " +
            "FROM " +
            "  public.elmis_help_topic " +
            "   LEFT OUTER JOIN public.elmis_help_topic_roles " +
            " ON elmis_help_topic_roles.help_topic_id = elmis_help_topic.id" +
            "  LEFT OUTER JOIN public.roles" +
            " ON elmis_help_topic_roles.role_id = roles.id " +
            "  LEFT OUTER JOIN public.role_assignments" +
            " ON role_assignments.roleid = roles.id " +
            "  LEFT OUTER JOIN public.users " +
            " ON  role_assignments.userid = users.id " +
            "WHERE (public.users.id =#{id} or is_category=false ) and parent_help_topic_id =#{parentId} ")
    @Results({
            @Result(column = "parent_help_topic_id", property = "parentHelpTopic"),
            @Result(column = "html_content", property = "htmlContent"),
            @Result(column = "is_category", property = "category")
    })
    List<HelpTopic> getRoleHelpTopicChildrenList(@Param(value = "id")Long loggedUserId,@Param(value = "parentId") Long parentId);
}
