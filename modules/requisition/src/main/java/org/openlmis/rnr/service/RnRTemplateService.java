package org.openlmis.rnr.service;

import org.openlmis.rnr.dao.RnrRepository;
import org.openlmis.rnr.domain.RnrColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RnRTemplateService {
    private RnrRepository rnrRepository;

    @Autowired
    public RnRTemplateService(RnrRepository rnrRepository) {
        this.rnrRepository = rnrRepository;
    }

    public List<RnrColumn> fetchAllRnRColumns(int programId) {
        List<RnrColumn> rnrColumns;
        if (rnrRepository.isRnRTemPlateDefinedForProgram(programId)) {
            rnrColumns= rnrRepository.fetchRnrColumnsDefinedForAProgram(programId);
        }else
        {
            rnrColumns = rnrRepository.fetchAllMasterRnRColumns();
        }
        return rnrColumns==null ? new ArrayList<RnrColumn>(): rnrColumns;
    }


    public void saveRnRTemplateForProgram(Integer programId, List<RnrColumn> rnrColumns) {
        if(rnrRepository.isRnRTemPlateDefinedForProgram(programId)){
            rnrRepository.updateAllProgramRnRColumns(programId, rnrColumns);
        }else{
            rnrRepository.insertAllProgramRnRColumns(programId, rnrColumns);
        }
    }
}
