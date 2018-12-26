package org.openlmis.core.repository;

import org.openlmis.core.domain.ReportType;
import org.openlmis.core.repository.mapper.ReportTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportTypeRepository {

    private ReportTypeMapper reportTypeMapper;

    @Autowired
    public ReportTypeRepository(ReportTypeMapper reportTypeMapper) {
        this.reportTypeMapper = reportTypeMapper;
    }

    public List<ReportType> getAll() {
        return reportTypeMapper.getAll();
    }

    public ReportType getByCode(String code) {
        return reportTypeMapper.getByCode(code);
    }
}