--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

UPDATE rights SET displayOrder = 1, displayNameKey= 'right.configure.rnr' WHERE name = 'CONFIGURE_RNR';
UPDATE rights SET displayOrder = 2, displayNameKey= 'right.manage.facility' WHERE name = 'MANAGE_FACILITY';
UPDATE rights SET displayOrder = 5, displayNameKey= 'right.manage.role' WHERE name = 'MANAGE_ROLE';
UPDATE rights SET displayOrder = 6, displayNameKey= 'right.manage.schedule' WHERE name = 'MANAGE_SCHEDULE';
UPDATE rights SET displayOrder = 7, displayNameKey= 'right.manage.user' WHERE name = 'MANAGE_USER';
UPDATE rights SET displayOrder = 8, displayNameKey= 'right.manage.supervisory.node' WHERE name = 'MANAGE_SUPERVISORY_NODE';
UPDATE rights SET displayOrder = 21, displayNameKey = 'right.upload' WHERE name = 'UPLOADS';
UPDATE rights SET displayOrder = 10, displayNameKey = 'right.manage.report' WHERE name = 'MANAGE_REPORT';
UPDATE rights SET displayOrder = 16, displayNameKey = 'right.view.requisition' WHERE name = 'VIEW_REQUISITION';
UPDATE rights SET displayOrder = 15, displayNameKey = 'right.create.requisition' WHERE name = 'CREATE_REQUISITION';
UPDATE rights SET displayOrder = 13, displayNameKey = 'right.authorize.requisition' WHERE name = 'AUTHORIZE_REQUISITION';
UPDATE rights SET displayOrder = 12, displayNameKey = 'right.approve.requisition' WHERE name = 'APPROVE_REQUISITION';
UPDATE rights SET displayOrder = 14, displayNameKey = 'right.convert.to.order' WHERE name = 'CONVERT_TO_ORDER';
UPDATE rights SET displayOrder = 17, displayNameKey = 'right.view.order' WHERE name = 'VIEW_ORDER';
UPDATE rights SET displayOrder = 3, displayNameKey = 'right.manage.program.product' WHERE name = 'MANAGE_PROGRAM_PRODUCT';
UPDATE rights SET displayOrder = 9, displayNameKey = 'right.manage.distribution' WHERE name = 'MANAGE_DISTRIBUTION';
UPDATE rights SET displayOrder = 18, displayNameKey = 'right.system.settings' WHERE name = 'SYSTEM_SETTINGS';
UPDATE rights SET displayOrder = 4, displayNameKey = 'right.manage.regimen.template' WHERE name = 'MANAGE_REGIMEN_TEMPLATE';
UPDATE rights SET displayOrder = 19, displayNameKey = 'right.fulfillment.fill.shipment' WHERE name = 'FACILITY_FILL_SHIPMENT';
UPDATE rights SET displayOrder = 20, displayNameKey = 'right.fulfillment.manage.pod' WHERE name = 'MANAGE_POD';
UPDATE rights SET displayOrder = 23, displayNameKey = 'right.manage.geo.zone' WHERE name = 'MANAGE_GEOGRAPHIC_ZONE';
UPDATE rights SET displayOrder = 24, displayNameKey = 'right.manage.requisition.group' WHERE name = 'MANAGE_REQUISITION_GROUP';
UPDATE rights SET displayOrder = 25, displayNameKey = 'right.manage.supply.line' WHERE name = 'MANAGE_SUPPLY_LINE';
UPDATE rights SET displayOrder = 26, displayNameKey = 'right.manage.facility.approved.products' WHERE name = 'MANAGE_FACILITY_APPROVED_PRODUCT';
UPDATE rights SET displayOrder = 27, displayNameKey = 'right.manage.products' WHERE name = 'MANAGE_PRODUCT';

UPDATE rights SET rightType = 'REPORTING' WHERE name = 'MANAGE_REPORT';