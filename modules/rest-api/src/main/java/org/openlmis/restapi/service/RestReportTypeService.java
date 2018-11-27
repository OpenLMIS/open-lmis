package org.openlmis.restapi.service;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.openlmis.core.domain.Program;
import org.openlmis.core.domain.ProgramSupported;
import org.openlmis.core.domain.ReportType;
import org.openlmis.core.repository.ReportTypeRepository;
import org.openlmis.core.service.ProgramSupportedService;
import org.openlmis.restapi.domain.ProgramDTO;
import org.openlmis.restapi.domain.ReportTypeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestReportTypeService {

    private ReportTypeRepository reportTypeRepository;

    private ProgramSupportedService programSupportedService;

    @Autowired
    public RestReportTypeService(ReportTypeRepository reportTypeRepository, ProgramSupportedService programSupportedService) {
        this.reportTypeRepository = reportTypeRepository;
        this.programSupportedService = programSupportedService;

    }

    public List<ReportTypeDTO> getReportTypeByFacilityId(Long facilityId) {
        List<ProgramSupported> programSupportedList = filterInvalidData(programSupportedService.getAllByFacilityId(facilityId));
        return FluentIterable.from(programSupportedList).transform(new Function<ProgramSupported, ReportTypeDTO>() {
            @Override
            public ReportTypeDTO apply(ProgramSupported input) {
                ReportTypeDTO reportType = new ReportTypeDTO();
                reportType.setActive(input.isReportActive());
                reportType.setStartTime(input.getReportStartDate());
                ReportType reportTypeDomain = input.getReportType();
                reportType.setCode(reportTypeDomain.getCode());
                reportType.setId(reportTypeDomain.getId());
                reportType.setName(reportTypeDomain.getName());
                reportType.setDescription(reportTypeDomain.getDescription());
                Program program = input.getProgram();
                reportType.setProgram(new ProgramDTO(program.getId(), program.getCode(), program.getName()));
                return reportType;
            }

        }).toList();
    }

    private List<ProgramSupported> filterInvalidData(List<ProgramSupported> programSupportedList) {
       return (List<ProgramSupported>) CollectionUtils.select(programSupportedList, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                ProgramSupported programSupported = (ProgramSupported) o;
                return programSupported.getReportType() != null;
            }
        });
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
