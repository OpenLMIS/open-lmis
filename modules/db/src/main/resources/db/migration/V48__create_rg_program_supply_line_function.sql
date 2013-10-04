--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

CREATE OR REPLACE FUNCTION getRgProgramSupplyLine()
  RETURNS TABLE(
  sNode TEXT,
  name TEXT,
  requisitionGroup TEXT
  ) AS
  $body$
  DECLARE
    requisitionGroupQuery VARCHAR;
    finalQuery            VARCHAR;
    ultimateParentRecord  RECORD;
    rowRG                 RECORD;
  BEGIN
    EXECUTE 'CREATE TEMP TABLE rg_supervisory_node (
            requisitionGroupId INTEGER, 
            requisitionGroup TEXT,
            supervisoryNodeId INTEGER, 
            sNode TEXT,
            programId INTEGER, 
            name TEXT,
            ultimateParentId INTEGER 
            ) ON COMMIT DROP';
    requisitionGroupQuery := 'SELECT RG.id, RG.code || '' '' || RG.name as requisitionGroup, RG.supervisoryNodeId, RGPS.programId, pg.name 
                              FROM requisition_groups AS RG INNER JOIN requisition_group_program_schedules AS RGPS ON RG.id = RGPS.requisitionGroupId 
                              INNER JOIN programs pg ON pg.id=RGPS.programid WHERE pg.active=true AND pg.push=false';
    FOR rowRG IN EXECUTE requisitionGroupQuery LOOP
    WITH RECURSIVE supervisoryNodesRec(id, sName, parentId, depth, path) AS
    (
      SELECT
        superNode.id,
        superNode.code || ' ' || superNode.name :: TEXT AS sName,
        superNode.parentId,
        1 :: INT                                        AS depth,
        superNode.id :: TEXT                            AS path
      FROM supervisory_nodes superNode
      WHERE id IN (rowRG.supervisoryNodeId)
      UNION
      SELECT
        sn.id,
        sn.code || ' ' || sn.name :: TEXT AS sName,
        sn.parentId,
        snRec.depth + 1                   AS depth,
        (snRec.path)
      FROM supervisory_nodes sn
        JOIN supervisoryNodesRec snRec
          ON sn.id = snRec.parentId
    )
    SELECT
      INTO ultimateParentRecord path  AS id,
                                id    AS ultimateParentId,
                                sName AS sNode
    FROM supervisoryNodesRec
    WHERE depth = (SELECT
                     max(depth)
                   FROM supervisoryNodesRec);
      EXECUTE
      'INSERT INTO rg_supervisory_node VALUES (' || rowRG.id || ',' ||
      quote_literal(rowRG.requisitionGroup) || ',' || rowRG.supervisoryNodeId ||
      ',' || quote_literal(ultimateParentRecord.sNode) || ',' || rowRG.programId
      || ',' || quote_literal(rowRG.name) || ',' ||
      ultimateParentRecord.ultimateParentId || ')';
    END LOOP;
    finalQuery := 'SELECT
                  RGS.snode            AS SupervisoryNode,
                  RGS.name             AS ProgramName,
                  RGS.requisitiongroup AS RequisitionGroup
                  FROM rg_supervisory_node AS RGS
                  WHERE NOT EXISTS
                  (SELECT
                     *
                   FROM supply_lines
                     INNER JOIN facilities f
                       ON f.id = supply_lines.supplyingFacilityId
                   WHERE supply_lines.supervisorynodeid = RGS.ultimateparentid AND
                         RGS.programid = supply_lines.programid AND f.enabled = TRUE)
                  ORDER BY SupervisoryNode, ProgramName, RequisitionGroup';
    RETURN QUERY EXECUTE finalQuery;
  END;
  $body$
LANGUAGE plpgsql;