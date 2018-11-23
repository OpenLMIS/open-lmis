package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.openlmis.core.domain.ReportType;
import org.openlmis.core.repository.ReportTypeRepository;
import org.openlmis.restapi.domain.ProgramDTO;
import org.openlmis.restapi.domain.ReportTypeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestReportTypeService {

    private ReportTypeRepository reportTypeRepository;

    @Autowired
    public RestReportTypeService(ReportTypeRepository reportTypeRepository) {
        this.reportTypeRepository = reportTypeRepository;
    }

    public List<ReportTypeDTO> getAllReportType() {
        List<ReportType> reportTypes = reportTypeRepository.getAll();
        return FluentIterable.from(reportTypes).transform(new Function<ReportType, ReportTypeDTO>() {
            @Override
            public ReportTypeDTO apply(ReportType source) {
                ProgramDTO program = new ProgramDTO();
                BeanUtils.copyProperties(source.getProgram(), program);
                ReportTypeDTO target = new ReportTypeDTO();
                BeanUtils.copyProperties(source, target);
                target.setProgram(program);
                return target;
            }
        }).toList();
    }

}
