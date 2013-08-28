-- Copyright © 2013 VillageReach.  All Rights Reserved.  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
-- If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
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