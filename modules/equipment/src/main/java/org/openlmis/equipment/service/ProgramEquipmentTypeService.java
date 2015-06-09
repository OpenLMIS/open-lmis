package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.ProgramEquipmentType;
import org.openlmis.equipment.repository.ProgramEquipmentTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramEquipmentTypeService {

  @Autowired
  ProgramEquipmentTypeRepository programEquipmentTypeRepository;

  public List<ProgramEquipmentType> getByProgramId(Long programId) {
    return programEquipmentTypeRepository.getByProgramId(programId);
  }

  public void Save(ProgramEquipmentType programEquipmentType) {
    if (programEquipmentType.getId() == null) {
      programEquipmentTypeRepository.insert(programEquipmentType);
    } else {
      programEquipmentTypeRepository.update(programEquipmentType);
    }
  }

  public void remove(Long programEquipmentId) {
    programEquipmentTypeRepository.remove(programEquipmentId);
  }


}
