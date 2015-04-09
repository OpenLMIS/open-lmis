package org.openlmis.equipment.service;

import org.openlmis.equipment.domain.ProgramEquipment;
import org.openlmis.equipment.repository.ProgramEquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgramEquipmentService {

  @Autowired
  ProgramEquipmentRepository programEquipmentRepository;

  public List<ProgramEquipment> getByProgramId(Long programId) {
    return programEquipmentRepository.getByProgramId(programId);
  }

  public void Save(ProgramEquipment programEquipment) {
    if (programEquipment.getId() == null) {
      programEquipmentRepository.insert(programEquipment);
    } else {
      programEquipmentRepository.update(programEquipment);
    }
  }

  public void remove(Long programEquipmentId) {
    programEquipmentRepository.remove(programEquipmentId);
  }


}
