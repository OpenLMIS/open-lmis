package org.openlmis.rnr.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.rnr.domain.RnrColumn;
import org.openlmis.rnr.domain.RnrColumnType;
import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class RnrTemplateService {

    private RnrTemplateRepository rnrRepository;

    @Autowired
    public RnrTemplateService(RnrTemplateRepository rnrRepository) {
        this.rnrRepository = rnrRepository;
    }

    public List<RnrColumn> fetchAllRnRColumns(String programCode) {
        List<RnrColumn> rnrColumns;
        if (rnrRepository.isRnRTemPlateDefinedForProgram(programCode)) {
            rnrColumns = rnrRepository.fetchProgramRnrColumns(programCode);
        } else {
            rnrColumns = rnrRepository.fetchAllMasterRnRColumns();
        }
        return rnrColumns == null ? new ArrayList<RnrColumn>() : rnrColumns;
    }


    public List<DependencyError> saveRnRTemplateForProgram(String programCode, List<RnrColumn> rnrColumns) {
        List<DependencyError> errors = validateDependencies(rnrColumns);
        if(!(errors==null || errors.isEmpty())){
            return errors;
        }

        if (rnrRepository.isRnRTemPlateDefinedForProgram(programCode)) {
            rnrRepository.updateAllProgramRnRColumns(programCode, rnrColumns);
        } else {
            rnrRepository.insertAllProgramRnRColumns(programCode, rnrColumns);
        }
        return null;
    }

    private List<DependencyError> validateDependencies(List<RnrColumn> rnrColumns) {
        return validateReferentialDependencies(rnrColumns);
    }

    private List<DependencyError> validateReferentialDependencies(List<RnrColumn> rnrColumns) {
        List<DependencyError> errors = new ArrayList<>();
        for (RnrColumn rnrColumn : rnrColumns) {
            if (!rnrColumn.isVisible()) continue;
            if (((rnrColumn.getName().equals("quantityDispensed")) || (rnrColumn.getName().equals("quantityRequested")))
                    && rnrColumn.getSelectedColumnType().equals(RnrColumnType.User_Input))
                continue;
            List<RnrColumn> referentialDependencies = rnrColumn.getDependencies();
            for (RnrColumn referentialDependency : referentialDependencies) {
                RnrColumn referredRnrColumn = getRnrColumnByName(referentialDependency.getName(), rnrColumns);
                if(!(referredRnrColumn.isVisible() || referredRnrColumn.getSelectedColumnType() == RnrColumnType.Calculated)){
                    errors.add(new DependencyError(rnrColumn, "error"));
                }
            }
        }
        return errors;
    }

    private RnrColumn getRnrColumnByName(String name, List<RnrColumn> rnrColumns) {
        for(RnrColumn rnrColumn : rnrColumns){
            if(rnrColumn.getName().equals(name)){
                return rnrColumn;
            }
        }
        return null;
    }

    public List<RnrColumn> fetchVisibleRnRColumns(String programCode) {
        return rnrRepository.fetchVisibleProgramRnRColumns(programCode);
    }

    @Data
    private class DependencyError {

        private RnrColumn rnrColumn;
        private String error;

        public DependencyError(RnrColumn rnrColumn, String error) {
            this.rnrColumn = rnrColumn;
            this.error = error;
        }
    }
}
