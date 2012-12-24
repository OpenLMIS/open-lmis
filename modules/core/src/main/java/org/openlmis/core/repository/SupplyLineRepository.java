package org.openlmis.core.repository;

import lombok.NoArgsConstructor;
import org.openlmis.core.domain.SupervisoryNode;
import org.openlmis.core.domain.SupplyLine;
import org.openlmis.core.exception.DataException;
import org.openlmis.core.repository.mapper.FacilityMapper;
import org.openlmis.core.repository.mapper.ProgramMapper;
import org.openlmis.core.repository.mapper.SupervisoryNodeMapper;
import org.openlmis.core.repository.mapper.SupplyLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

@Repository
@NoArgsConstructor
public class SupplyLineRepository {

    private SupplyLineMapper supplyLineMapper;

    private ProgramMapper programMapper;

    private FacilityMapper facilityMapper;

    private SupervisoryNodeMapper supervisoryNodeMapper;

    @Autowired
    public SupplyLineRepository(SupplyLineMapper supplyLineMapper, SupervisoryNodeMapper supervisoryNodeMapper, ProgramMapper programMapper, FacilityMapper facilityMapper) {
        this.supplyLineMapper = supplyLineMapper;
        this.programMapper = programMapper;
        this.supervisoryNodeMapper = supervisoryNodeMapper;
        this.facilityMapper = facilityMapper;
    }

    public void insert(SupplyLine supplyLine) {
        try {
            supplyLine.getProgram().setId(programMapper.getIdByCode(supplyLine.getProgram().getCode()));
            supplyLine.getSupplyingFacility().setId(facilityMapper.getIdForCode(supplyLine.getSupplyingFacility().getCode()));
            supplyLine.getSupervisoryNode().setId(supervisoryNodeMapper.getIdForCode(supplyLine.getSupervisoryNode().getCode()));

            if (supplyLine.getProgram().getId() == null) {
                throw new DataException("Program Code does not exist");
            }

            if (supplyLine.getSupplyingFacility().getId() == null) {
                throw new DataException("Facility Code does not exist");
            }

            if (supplyLine.getSupervisoryNode().getId() == null) {
                throw new DataException("Supervising Node does not exist");
            } else {
                SupervisoryNode supervisoryNode = supervisoryNodeMapper.getSupervisoryNode(supplyLine.getSupervisoryNode().getId());

                if (supervisoryNode != null && supervisoryNode.getParent() != null) {
                    throw new DataException("Supervising Node is not the Top node");
                }
            }

            supplyLineMapper.insert(supplyLine);
        } catch (DuplicateKeyException ex) {
            throw new DataException("Duplicate entry for Supply Line found.");
        }
    }


}
