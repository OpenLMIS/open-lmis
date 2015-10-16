--
-- Name: TABLE gtin_lookups; Schema: public; Owner: postgres
--
DROP TABLE IF EXISTS gtin_lookups;
DROP SEQUENCE IF EXISTS  public.gtin_lookups_id_seq;
CREATE TABLE gtin_lookups (
    id integer NOT NULL,
    gtin character varying(255) NOT NULL,
    productid integer NOT NULL,
    manufacturename character varying,
    dosespervial integer NOT NULL,
    vialsperbox integer NOT NULL,
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer,
    boxesperbox integer
);


ALTER TABLE public.gtin_lookups OWNER TO postgres;

COMMENT ON TABLE gtin_lookups IS 'Information About different Vaccine Packaging Information';

CREATE SEQUENCE gtin_lookups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.gtin_lookups_id_seq OWNER TO postgres;

ALTER SEQUENCE gtin_lookups_id_seq OWNED BY gtin_lookups.id;

ALTER TABLE ONLY gtin_lookups ALTER COLUMN id SET DEFAULT nextval('gtin_lookups_id_seq'::regclass);

ALTER TABLE ONLY gtin_lookups
    ADD CONSTRAINT gtin_lookups_pkey PRIMARY KEY (id);

--
-- Name: TABLE var_items; Schema: public; Owner: postgres
--

DROP TABLE IF EXISTS var_items;
DROP SEQUENCE IF EXISTS public.var_items_id_seq;
CREATE TABLE var_items (
    id integer NOT NULL,
    vardetailsid integer NOT NULL,
    shipmentnumber character varying(255) NOT NULL,
    productid integer NOT NULL,
    manufacturedate timestamp without time zone DEFAULT now(),
    expiredate timestamp without time zone DEFAULT now(),
    lotnumber character varying(255),
    numberofdoses integer NOT NULL,
    derliverystatus character varying(255),
    numberreceived integer,
    physicaldamage character varying(255),
    damagedamount integer,
    vvmstatus character varying(255),
    problems text,
    createdby integer,
    createddate timestamp without time zone DEFAULT now(),
    modifiedby integer,
    modifieddate timestamp without time zone DEFAULT now(),
    gtinlookupid integer NOT NULL
);


ALTER TABLE public.var_items OWNER TO postgres;


COMMENT ON TABLE var_items IS 'Vaccine Arrival Report Items';

CREATE SEQUENCE var_items_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.var_items_id_seq OWNER TO postgres;

ALTER SEQUENCE var_items_id_seq OWNED BY var_items.id;

ALTER TABLE ONLY var_items ALTER COLUMN id SET DEFAULT nextval('var_items_id_seq'::regclass);

ALTER TABLE ONLY var_items
    ADD CONSTRAINT var_items_pkey PRIMARY KEY (id);


--
-- Name: var_details; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

DROP TABLE IF EXISTS  public.var_details;

DROP SEQUENCE IF EXISTS  public.var_details_id_seq;
CREATE TABLE var_details (
    id integer NOT NULL,
    awbnumber character varying(255),
    flightnumber character varying(255),
    estimatetimeofarrival timestamp without time zone DEFAULT now(),
    actualtimeofarrival timestamp without time zone DEFAULT now(),
    numberofitemsinspected integer,
    coolanttype character varying(255),
    tempraturemonitor character varying(255),
    purchaseordernumber character varying(255),
    clearingagent character varying(255),
    labels character varying(255),
    comments character varying(255),
    invoice character varying(255),
    packinglist character varying(255),
    releasecerificate character varying(255),
    airwaybill character varying(255),
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer,
    deliverystatus character varying(255),
    destnationairport character varying(255)
);


ALTER TABLE public.var_details OWNER TO postgres;

COMMENT ON TABLE var_details IS 'Vaccine Arrival Report Details';


CREATE SEQUENCE var_details_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.var_details_id_seq OWNER TO postgres;

ALTER SEQUENCE var_details_id_seq OWNED BY var_details.id;

ALTER TABLE ONLY var_details ALTER COLUMN id SET DEFAULT nextval('var_details_id_seq'::regclass);

ALTER TABLE ONLY var_details
    ADD CONSTRAINT var_details_pkey PRIMARY KEY (id);


--
-- Name: var_item_alarms; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

DROP TABLE IF EXISTS  public.var_item_alarms;
DROP SEQUENCE IF EXISTS  public.var_item_alarms_id_seq;

CREATE TABLE var_item_alarms (
    id integer NOT NULL,
    vardetailsid integer,
    productid integer,
    boxnumber integer,
    lotnumber character varying,
    alarmtemprature character varying,
    coldchainmonitor character varying,
    timeofinspection timestamp without time zone,
    gtinlookupid integer,
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer
);


ALTER TABLE public.var_item_alarms OWNER TO postgres;

COMMENT ON TABLE var_item_alarms IS 'Store Alarm Information for Items with Problems';

CREATE SEQUENCE var_item_alarms_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.var_item_alarms_id_seq OWNER TO postgres;

ALTER SEQUENCE var_item_alarms_id_seq OWNED BY var_item_alarms.id;

ALTER TABLE ONLY var_item_alarms ALTER COLUMN id SET DEFAULT nextval('var_item_alarms_id_seq'::regclass);

ALTER TABLE ONLY var_item_alarms
    ADD CONSTRAINT var_item_alarms_pkey PRIMARY KEY (id);


--
-- Name: var_item_partials; Type: TABLE; Schema: public; Owner: postgres; Tablespace:
--

DROP TABLE  IF EXISTS public.var_item_partials;
DROP SEQUENCE  IF EXISTS public.var_item_partials_id_seq;

CREATE TABLE var_item_partials (
    id integer NOT NULL,
    vardetailsid integer,
    productid integer,
    boxnumber integer,
    lotnumber character varying(255),
    expectednumber integer,
    availablenumber integer,
    gtinlookupid integer,
    createddate timestamp without time zone DEFAULT now(),
    modifieddate timestamp without time zone DEFAULT now(),
    createdby integer
);


ALTER TABLE public.var_item_partials OWNER TO postgres;

COMMENT ON TABLE var_item_partials IS 'Store The information about Partial boxes';

CREATE SEQUENCE var_item_partials_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER TABLE public.var_item_partials_id_seq OWNER TO postgres;

ALTER SEQUENCE var_item_partials_id_seq OWNED BY var_item_partials.id;

ALTER TABLE ONLY var_item_partials ALTER COLUMN id SET DEFAULT nextval('var_item_partials_id_seq'::regclass);

ALTER TABLE ONLY var_item_partials
    ADD CONSTRAINT var_item_partials_pkey PRIMARY KEY (id);
