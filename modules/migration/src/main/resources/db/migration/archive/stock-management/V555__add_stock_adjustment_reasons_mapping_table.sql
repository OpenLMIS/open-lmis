DO $$
  BEGIN
    BEGIN
      CREATE TABLE stock_adjustment_reasons_programs
      (
        id serial NOT NULL,
        programcode text NOT NULL,
        reasonname text NOT NULL,
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT stock_adjustment_reasons_programs_pkey PRIMARY KEY (id),
        CONSTRAINT program_fkey FOREIGN KEY (programcode)
            REFERENCES programs (code) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT stock_adjustment_reason_fkey FOREIGN KEY (reasonname)
            REFERENCES losses_adjustments_types (name) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT stock_adjustment_reasons_programs_program_reason_key UNIQUE (programcode, reasonname)
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE stock_adjustment_reasons_programs
        OWNER TO postgres;
    END;
  END;
$$