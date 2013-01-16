INSERT INTO requisition_groups ( code ,name,description,supervisoryNodeId )values
('RG2','Requistion Group 2','Supports EM(Q1M)',(select id from  supervisory_nodes where code ='N1')),
('RG1','Requistion Group 1','Supports EM(Q2M)',(select id from  supervisory_nodes where code ='N2'));

INSERT INTO requisition_group_members ( requisitionGroupId ,facilityId )values
((select id from  requisition_groups where code ='RG1'),(select id from  facilities where code ='F10')),
((select id from  requisition_groups where code ='RG2'),(select id from  facilities where code ='F11'));


insert into requisition_group_program_schedules ( requisitionGroupId , programId , scheduleId , directDelivery ) values
((select id from requisition_groups where code='RG1'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),
((select id from requisition_groups where code='RG1'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),
((select id from requisition_groups where code='RG1'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='ESS_MEDS'),(select id from processing_schedules where code='Q1stM'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='MALARIA'),(select id from processing_schedules where code='Q1stM'),TRUE),
((select id from requisition_groups where code='RG2'),(select id from programs where code='HIV'),(select id from processing_schedules where code='M'),TRUE);


