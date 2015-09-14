DO $$
  BEGIN
    BEGIN
      CREATE TABLE vaccine_lots_on_hand_adjustments
      (
        id serial NOT NULL,
        lotonhandid integer,
        adjustmentreason character(50),
        quantity integer,
        createdby integer,
        createddate timestamp with time zone,
        modifiedby integer,
        modifieddate timestamp with time zone,
        effectivedate timestamp with time zone,
        CONSTRAINT adjusment_reasons_pkey PRIMARY KEY (id),
        CONSTRAINT vaccine_adjustment_reasons_fkey FOREIGN KEY (adjustmentreason)
            REFERENCES losses_adjustments_types (name) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE vaccine_lots_on_hand_adjustments
        OWNER TO postgres;

     CREATE TABLE vaccine_lots_on_hand_vvm
     (
       id serial NOT NULL,
       lotonhandid integer,
       vvmstatus smallint,
       effectivedate timestamp without time zone DEFAULT now(),
       CONSTRAINT pkey PRIMARY KEY (id),
       CONSTRAINT vaccine_vvm_lots_on_hand_fkey FOREIGN KEY (lotonhandid)
           REFERENCES lots_on_hand (id) MATCH SIMPLE
           ON UPDATE NO ACTION ON DELETE NO ACTION
     )
     WITH (
       OIDS=FALSE
     );
     ALTER TABLE vaccine_lots_on_hand_vvm
       OWNER TO postgres;
    END;
  END;
$$