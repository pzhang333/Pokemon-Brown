--
-- TOC entry 191 (class 1259 OID 17762)
-- Name: inventories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS inventories (
    user_id integer NOT NULL,
    item_id text NOT NULL,
    amount integer NOT NULL
);


--
-- TOC entry 190 (class 1259 OID 17760)
-- Name: inventories_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS inventories_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2169 (class 0 OID 0)
-- Dependencies: 190
-- Name: inventories_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE inventories_user_id_seq OWNED BY inventories.user_id;


--
-- TOC entry 189 (class 1259 OID 17745)
-- Name: pokemon; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS pokemon (
    id integer NOT NULL,
    user_id integer NOT NULL,
    nickname text,
    gender integer NOT NULL,
    experience integer NOT NULL,
    stored boolean NOT NULL,
    cur_health integer NOT NULL,
    max_health integer NOT NULL,
    move_1 text,
    move_2 text,
    move_3 text,
    move_4 text,
    pp_1 integer,
    pp_2 integer,
    pp_3 integer,
    pp_4 integer
);


--
-- TOC entry 187 (class 1259 OID 17741)
-- Name: pokemon_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS pokemon_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2170 (class 0 OID 0)
-- Dependencies: 187
-- Name: pokemon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE pokemon_id_seq OWNED BY pokemon.id;


--
-- TOC entry 188 (class 1259 OID 17743)
-- Name: pokemon_user_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS pokemon_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2171 (class 0 OID 0)
-- Dependencies: 188
-- Name: pokemon_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE pokemon_user_id_seq OWNED BY pokemon.user_id;


--
-- TOC entry 193 (class 1259 OID 17773)
-- Name: status_effects; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS status_effects (
    pokemon_id integer NOT NULL,
    effect text NOT NULL
);


--
-- TOC entry 192 (class 1259 OID 17771)
-- Name: status_effects_pokemon_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS status_effects_pokemon_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2172 (class 0 OID 0)
-- Dependencies: 192
-- Name: status_effects_pokemon_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE status_effects_pokemon_id_seq OWNED BY status_effects.pokemon_id;


--
-- TOC entry 186 (class 1259 OID 17727)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS users (
    id integer NOT NULL,
    username text NOT NULL,
    email text NOT NULL,
    chunk text,
    "row" integer,
    col integer,
    currency integer DEFAULT 0 NOT NULL,
    hashed_pw text NOT NULL,
    salt text NOT NULL,
    session_token text
);


--
-- TOC entry 185 (class 1259 OID 17725)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 2173 (class 0 OID 0)
-- Dependencies: 185
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- TOC entry 2029 (class 2604 OID 17765)
-- Name: inventories user_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY inventories ALTER COLUMN user_id SET DEFAULT nextval('inventories_user_id_seq'::regclass);


--
-- TOC entry 2027 (class 2604 OID 17748)
-- Name: pokemon id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY pokemon ALTER COLUMN id SET DEFAULT nextval('pokemon_id_seq'::regclass);


--
-- TOC entry 2028 (class 2604 OID 17749)
-- Name: pokemon user_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY pokemon ALTER COLUMN user_id SET DEFAULT nextval('pokemon_user_id_seq'::regclass);


--
-- TOC entry 2030 (class 2604 OID 17776)
-- Name: status_effects pokemon_id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY status_effects ALTER COLUMN pokemon_id SET DEFAULT nextval('status_effects_pokemon_id_seq'::regclass);


--
-- TOC entry 2025 (class 2604 OID 17730)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- TOC entry 2032 (class 2606 OID 17740)
-- Name: users email_unique; Type: CONSTRAINT; Schema: public; Owner: -
--


ALTER TABLE ONLY users
    ADD CONSTRAINT email_unique UNIQUE (email);


--
-- TOC entry 2041 (class 2606 OID 17770)
-- Name: inventories inventories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY inventories
    ADD CONSTRAINT inventories_pkey PRIMARY KEY (user_id, item_id);


--
-- TOC entry 2038 (class 2606 OID 17754)
-- Name: pokemon pokemon_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pokemon
    ADD CONSTRAINT pokemon_pkey PRIMARY KEY (id);


--
-- TOC entry 2034 (class 2606 OID 17738)
-- Name: users username_unique; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT username_unique UNIQUE (username);


--
-- TOC entry 2036 (class 2606 OID 17736)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 2039 (class 1259 OID 17790)
-- Name: fki_user_id_foreign_key; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS fki_user_id_foreign_key ON inventories USING btree (user_id);


--
-- TOC entry 2044 (class 2606 OID 17780)
-- Name: status_effects pokemon_id_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY status_effects
    ADD CONSTRAINT pokemon_id_foreign_key FOREIGN KEY (pokemon_id) REFERENCES pokemon(id);


--
-- TOC entry 2042 (class 2606 OID 17755)
-- Name: pokemon user_id_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY pokemon
    ADD CONSTRAINT user_id_foreign_key FOREIGN KEY (user_id) REFERENCES users(id);


--
-- TOC entry 2043 (class 2606 OID 17785)
-- Name: inventories user_id_foreign_key; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY inventories
    ADD CONSTRAINT user_id_foreign_key FOREIGN KEY (user_id) REFERENCES users(id);


-- Completed on 2018-04-01 13:37:29

--
-- PostgreSQL database dump complete
--

