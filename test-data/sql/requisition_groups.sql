--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values
('RG2','Requistion Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='N1')),
('RG1','Requistion Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='N2')),
('RG3','Requistion Group 3','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='N2')),
('RG4','Requistion Group 4','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='N3'));

INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values
((select id from  requisition_groups where code ='RG4'),(select id from  facilities where code ='F10')),
((select id from  requisition_groups where code ='RG4'),(select id from  facilities where code ='V10')),
((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='F11')),
((select id from  requisition_groups where code ='RG3'),(select id from  facilities where code ='F13')),
((select id from  requisition_groups where code ='RG4'),(select id from  facilities where code ='F100'));


insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values
((select id from requisition_groups where code='RG1'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG1'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG1'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG1'),(select id from programs where code='TB'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='TB'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG3'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG3'),(select id from programs where code='TB'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG4'),(select id from programs where code='TB'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG4'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG4'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG4'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE);


insert into supply_lines
(description, supervisoryNodeId, programId, supplyingFacilityId,exportOrders) values
('supplying node       for MALARIA', (select id from supervisory_nodes where code = 'N1'), (select id from programs where code='MALARIA'),(select id from facilities where code = 'F10'),'t'),
('supplying node            for TB', (select id from supervisory_nodes where code = 'N1'), (select id from programs where code='TB'),(select id from facilities where code = 'F10'),'t'),
('supplying node      for ESS_MEDS', (select id from supervisory_nodes where code = 'N1'), (select id from programs where code='ESS_MEDS'),(select id from facilities where code = 'F10'),'t'),
('supplying node       for MALARIA', (select id from supervisory_nodes where code = 'N3'), (select id from programs where code='MALARIA'),(select id from facilities where code = 'F11'),'f'),
('supplying node            for TB', (select id from supervisory_nodes where code = 'N3'), (select id from programs where code='TB'),(select id from facilities where code = 'F11'),'f'),
('supplying node      for ESS_MEDS', (select id from supervisory_nodes where code = 'N3'), (select id from programs where code='ESS_MEDS'),(select id from facilities where code = 'F11'),'f'),
('supplying node           for HIV', (select id from supervisory_nodes where code = 'N3'), (select id from programs where code='HIV'),(select id from facilities where code = 'F11'),'f');

