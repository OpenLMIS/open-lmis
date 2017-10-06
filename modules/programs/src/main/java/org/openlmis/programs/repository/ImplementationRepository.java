package org.openlmis.programs.repository;

import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.Treatment;
import org.openlmis.programs.repository.mapper.ImplementationMapper;
import org.openlmis.programs.repository.mapper.TreatmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ImplementationRepository {
    @Autowired
    private ImplementationMapper implementationMapper;
    @Autowired
    private TreatmentMapper treatmentMapper;

    public List<Implementation> save(List<Implementation> implementations) {
        implementationMapper.bulkInsert(implementations);
        insertTreatmentsForImplementations(implementations);
        return implementations;
    }

    private void insertTreatmentsForImplementations(List<Implementation> implementations) {
        List<Treatment> treatments = new ArrayList<>();
        for (Implementation implementation : implementations) {
            for (Treatment treatment : implementation.getTreatments()){
                treatment.setImplementation(implementation);
                treatments.add(treatment);
            }
        }
        treatmentMapper.bulkInsert(treatments);
    }
}
