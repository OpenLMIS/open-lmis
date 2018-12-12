CREATE TABLE service_items
(
  id serial NOT NULL,
  serviceid integer NOT NULL,
  requisitionlineitemid integer NOT NULL,
  patientsontreatment integer NOT NULL DEFAULT 0,
  createdby integer,
  createddate timestamp without time zone DEFAULT now(),
  modifiedby integer,
  modifieddate timestamp without time zone DEFAULT now(),
  CONSTRAINT service_items_pkey PRIMARY KEY (id),
  CONSTRAINT service_line_items_service_fkey FOREIGN KEY (serviceid)
      REFERENCES services (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT service_line_items_requisitionlineitem_fkey FOREIGN KEY (requisitionlineitemid)
      REFERENCES requisition_line_items (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)