INSERT INTO equipment_types
(code, name, displayOrder, createdBy)
VALUES
('et-1', 'Refrigerator', 1, 1),
('et-2', 'Anesthesia Units', 2,  1);


INSERT INTO equipments
(name, equipmentTypeId, createdBy)
VALUES
('Refrigerator', 1, 1),
('Anesthesia Units', 2, 1);

INSERT INTO donors
(code, shortName, longName, createdBy)
VALUES
('123', 'donor-1', 'the donor named 1', 1);

INSERT INTO equipment_service_vendors
(name, website,contactperson, primaryPhone, email, description, specialization, geographicCoverage, registrationdate )
VALUES
('The Vendor', 'www.vendor.io', 'John Doe', '1-203-933-2334', 'john@vendor.io', 'Fixing all kinds of stuff', 'Photocopy Machines', 'Tanzania', '2008-01-01');


INSERT INTO equipment_service_contracts
(vendorid, identifier, startdate, enddate, description, terms, coverage, contractdate)
VALUES
(1, '12323','2015-02-27', '2019-02-26', 'Servicing all the equipments', 'Pay Per Maintenance', 'Full Tanzania', '2014-09-17');