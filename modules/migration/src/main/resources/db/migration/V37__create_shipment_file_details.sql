drop table shipment_file_details;

create table shipment_file_details
(
	id serial not null primary key,
	orderId varchar(20) not null,
	facilityCode varchar(20) not null,
	productCode varchar(20) not null,
	orderedQuantity int not null,
	suppliedQuantity int not null,
	period varchar(20) not null,
	alternativeProductCode varchar null,
	alternativeProductDescription varchar null,
	alternativeOrderedQuantity int null,
	alternativeSuppliedQuantity int null,
	createdBy INTEGER,
  createdDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  modifiedBy INTEGER,
  modifiedDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);