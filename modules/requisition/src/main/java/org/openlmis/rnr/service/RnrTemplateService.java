package org.openlmis.rnr.service;

import org.openlmis.rnr.repository.RnrTemplateRepository;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
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


    public void saveRnRTemplateForProgram(String programCode, List<RnrColumn> rnrColumns) {
        if (rnrRepository.isRnRTemPlateDefinedForProgram(programCode)) {
            rnrRepository.updateAllProgramRnRColumns(programCode, rnrColumns);
        } else {
            rnrRepository.insertAllProgramRnRColumns(programCode, rnrColumns);
        }
    }

    public List<RnrColumn> fetchVisibleRnRColumns(String programCode) {
        return rnrRepository.fetchVisibleProgramRnRColumns(programCode);
    }

}
