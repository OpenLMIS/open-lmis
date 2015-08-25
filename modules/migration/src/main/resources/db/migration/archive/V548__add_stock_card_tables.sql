DO $$
  BEGIN
    BEGIN
      CREATE TABLE stock_cards
      (
        id serial NOT NULL,
        facilityid integer NOT NULL,
        productid integer NOT NULL,
        totalquantityonhand integer DEFAULT 0,
        effectivedate timestamp with time zone DEFAULT now(),
        notes text,
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT stock_cards_pkey PRIMARY KEY (id),
        CONSTRAINT facility_fkey FOREIGN KEY (facilityid)
            REFERENCES facilities (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT product_fkey FOREIGN KEY (productid)
            REFERENCES products (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT stock_cards_facility_product_key UNIQUE (facilityid, productid)
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE stock_cards
        OWNER TO postgres;
    END;

    BEGIN
      CREATE TABLE lots
      (
        id serial NOT NULL,
        productid integer NOT NULL,
        lotnumber text,
        manufacturername text,
        manufacturedate timestamp with time zone DEFAULT now(),
        expirationdate timestamp with time zone DEFAULT now(),
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT lots_pkey PRIMARY KEY (id),
        CONSTRAINT product_fkey FOREIGN KEY (productid)
            REFERENCES products (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE lots
        OWNER TO postgres;
    END;

    CREATE TYPE stockmovementtype AS ENUM('Facility Visit', 'Order', 'Inventory Transfer');

    BEGIN
      CREATE TABLE stock_movements
      (
        id serial NOT NULL,
        type stockmovementtype NOT NULL,
        fromfacilityid integer,
        tofacilityid integer,
        initiateddate timestamp with time zone DEFAULT now(),
        shippeddate timestamp with time zone DEFAULT now(),
        expecteddate timestamp with time zone DEFAULT now(),
        receiveddate timestamp with time zone DEFAULT now(),
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT stock_movements_pkey PRIMARY KEY (id),
        CONSTRAINT from_facility_fkey FOREIGN KEY (fromfacilityid)
            REFERENCES facilities (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT to_facility_fkey FOREIGN KEY (tofacilityid)
            REFERENCES facilities (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE stock_movements
        OWNER TO postgres;
    END;

    BEGIN
      CREATE TABLE lots_on_hand
      (
        id serial NOT NULL,
        stockcardid integer NOT NULL,
        lotid integer NOT NULL,
        quantityonhand integer DEFAULT 0,
        effectivedate timestamp with time zone DEFAULT now(),
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT lots_on_hand_pkey PRIMARY KEY (id),
        CONSTRAINT stock_card_fkey FOREIGN KEY (stockcardid)
            REFERENCES stock_cards (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT lot_fkey FOREIGN KEY (lotid)
            REFERENCES lots (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE lots_on_hand
        OWNER TO postgres;
    END;

    CREATE TYPE stockcardlineitemtype AS ENUM('Issue', 'Receipt', 'Adjustment');

    BEGIN
      CREATE TABLE stock_card_line_items
      (
        id serial NOT NULL,
        stockcardid integer NOT NULL,
        lotonhandid integer NOT NULL,
        type stockcardlineitemtype NOT NULL,
        quantity integer NOT NULL DEFAULT 0,
        stockmovementid integer,
        referencenumber text,
        adjustmentreason text,
        notes text,
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT stock_card_line_items_pkey PRIMARY KEY (id),
        CONSTRAINT stock_card_fkey FOREIGN KEY (stockcardid)
            REFERENCES stock_cards (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT lot_on_hand_fkey FOREIGN KEY (lotonhandid)
            REFERENCES lots_on_hand (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT stock_movement_fkey FOREIGN KEY (stockmovementid)
            REFERENCES stock_movements (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE stock_card_line_items
        OWNER TO postgres;
    END;

    BEGIN
      CREATE TABLE stock_movement_line_items
      (
        id serial NOT NULL,
        stockmovementid integer NOT NULL,
        lotid integer NOT NULL,
        quantity integer NOT NULL DEFAULT 0,
        notes text,
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT stock_movement_line_items_pkey PRIMARY KEY (id),
        CONSTRAINT stock_movement_fkey FOREIGN KEY (stockmovementid)
            REFERENCES stock_movements (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT lot_fkey FOREIGN KEY (lotid)
            REFERENCES lots (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE stock_movement_line_items
        OWNER TO postgres;
    END;

    BEGIN
      CREATE TABLE stock_movement_lots
      (
        id serial NOT NULL,
        stockmovementlineitemid integer NOT NULL,
        lotid integer NOT NULL,
        quantity integer DEFAULT 0,
        effectivedate timestamp with time zone DEFAULT now(),
        createdby integer,
        createddate timestamp with time zone DEFAULT now(),
        modifiedby integer,
        modifieddate timestamp with time zone DEFAULT now(),
        CONSTRAINT stock_movement_lots_pkey PRIMARY KEY (id),
        CONSTRAINT stock_movement_line_item_fkey FOREIGN KEY (stockmovementlineitemid)
            REFERENCES stock_movement_line_items (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION,
        CONSTRAINT lot_fkey FOREIGN KEY (lotid)
            REFERENCES lots (id) MATCH SIMPLE
            ON UPDATE NO ACTION ON DELETE NO ACTION
      )
      WITH (
        OIDS=FALSE
      );
      ALTER TABLE stock_movement_lots
        OWNER TO postgres;
    END;
  END;
$$