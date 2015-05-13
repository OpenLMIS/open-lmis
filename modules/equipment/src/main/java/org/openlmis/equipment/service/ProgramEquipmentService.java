package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.repository.ProgramEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramEquipmentService {

  @Autowired
  ProgramEquipmentRepository programEquipmentRepository;

  public List<ProgramEquipmentType> getByProgramId(Long programId) {
    return programEquipmentRepository.getByProgramId(programId);
  }

  public void Save(ProgramEquipmentType programEquipmentType) {
    if (programEquipmentType.getId() == null) {
      programEquipmentRepository.insert(programEquipmentType);
    } else {
      programEquipmentRepository.update(programEquipmentType);
    }
  }

  public void remove(Long programEquipmentId) {
    programEquipmentRepository.remove(programEquipmentId);
  }


}
