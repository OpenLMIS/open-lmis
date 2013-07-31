package org.openlmis.core.service;

import lombok.NoArgsConstructor;
import org.openlmis.core.repository.ProcessingPeriodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * e-lmis
 * Created by: Elias Muluneh
 * Date: 7/31/13
 * Time: 5:07 AM
 */
@Service
@NoArgsConstructor
public class ProcessingPeriodService {

    @Autowired
    ProcessingPeriodRepository repository;

    public Long getIdByName(String name){
        return repository.getIdByName( name );
    }
}
