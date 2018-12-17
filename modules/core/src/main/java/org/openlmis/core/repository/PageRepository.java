package org.openlmis.core.repository;

import org.openlmis.core.repository.mapper.PageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class PageRepository {

    private PageMapper pageMapper;

    @Autowired
    public PageRepository(PageMapper pageMapper) {
        this.pageMapper = pageMapper;
    }

    public Integer getPageInfo(String tableName, Date fromStartDate) {
        return pageMapper.getPageInfo(tableName, fromStartDate);
    }
}
