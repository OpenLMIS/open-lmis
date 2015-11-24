--
-- This program is part of the OpenLMIS logistics management information system platform software.
-- Copyright © 2013 VillageReach
--
-- This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
--  
-- This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
-- You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
--

INSERT INTO rights (name, rightType, description) VALUES
('UPLOADS', 'ADMIN', 'Permission to upload'),
('UPLOAD_REPORT', 'ADMIN', 'Permission to upload reports'),
('MANAGE_FACILITY', 'ADMIN', 'Permission to manage facilities(crud)'),
('MANAGE_ROLE', 'ADMIN', 'Permission to create and edit roles in the system'),
('MANAGE_SCHEDULE', 'ADMIN', 'Permission to create and edit schedules in the system'),
('CONFIGURE_RNR', 'ADMIN', 'Permission to create and edit r&r template for any program'),
('CREATE_REQUISITION', 'REQUISITION', 'Permission to create, edit, submit and recall requisitions'),
('APPROVE_REQUISITION', 'REQUISITION', 'Permission to approve requisitions'),
('AUTHORIZE_REQUISITION', 'REQUISITION', 'Permission to edit, authorize and recall requisitions'),
('MANAGE_USER', 'ADMIN', 'Permission to create and view users'),
('CONVERT_TO_ORDER', 'FULFILLMENT', 'Permission to convert requisitions to order'),
('VIEW_ORDER', 'FULFILLMENT', 'Permission to view orders'),
('VIEW_REQUISITION', 'REQUISITION', 'Permission to view requisition'),
('VIEW_REPORT', 'ADMIN', 'Permission to view reports'),
('MANAGE_REPORT', 'ADMIN', 'Permission to manage reports'),
('MANAGE_PROGRAM_PRODUCT', 'ADMIN', 'Permission to manage program products'),
('MANAGE_DISTRIBUTION', 'ALLOCATION', 'Permission to manage an distribution'),
('SYSTEM_SETTINGS', 'ADMIN', 'Permission to configure Electronic Data Interchange (EDI)'),
('MANAGE_REGIMEN_TEMPLATE', 'ADMIN', 'Permission to manage a regimen template'),
('FACILITY_FILL_SHIPMENT', 'FULFILLMENT', 'Permission to fill shipment data for facility'),
('MANAGE_POD', 'FULFILLMENT', 'Permission to manage proof of delivery');
