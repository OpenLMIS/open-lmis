package org.openlmis.programs.repository;

import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;
import org.openlmis.programs.repository.mapper.MalariaProgramMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class MalariaProgramRepository {
    @Autowired
    private MalariaProgramMapper malariaProgramMapper;
    @Autowired
    private ImplementationRepository implementationRepository;

    public MalariaProgram save(MalariaProgram malariaProgram) {
        malariaProgramMapper.insert(malariaProgram);
        saveImplementations(malariaProgram);
        return malariaProgram;
    }

    private void saveImplementations(MalariaProgram malariaProgram) {
        for (Implementation implementation : malariaProgram.getImplementations()) {
            implementation.setMalariaProgram(malariaProgram);
        }
        implementationRepository.save(malariaProgram.getImplementations());
    }
}
