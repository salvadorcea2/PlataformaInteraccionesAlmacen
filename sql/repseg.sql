CREATE EXTENSION hstore;

CREATE TABLE campo (
    id integer NOT NULL,
    plantilla_campo_id integer NOT NULL,
    receptor_id integer NOT NULL,
    habilitado boolean DEFAULT true
);


ALTER TABLE campo OWNER TO repo_adm;

--
-- TOC entry 217 (class 1259 OID 186957)
-- Name: campo_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE campo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE campo_id_seq OWNER TO repo_adm;

--
-- TOC entry 2868 (class 0 OID 0)
-- Dependencies: 217
-- Name: campo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE campo_id_seq OWNED BY campo.id;


--
-- TOC entry 189 (class 1259 OID 186706)
-- Name: canal; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE canal (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE canal OWNER TO repo_adm;

--
-- TOC entry 188 (class 1259 OID 186704)
-- Name: canal_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE canal_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE canal_id_seq OWNER TO repo_adm;

--
-- TOC entry 2869 (class 0 OID 0)
-- Dependencies: 188
-- Name: canal_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE canal_id_seq OWNED BY canal.id;


--
-- TOC entry 193 (class 1259 OID 186734)
-- Name: canal_transmision; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE canal_transmision (
    id integer NOT NULL,
    propiedades character varying(80)[],
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE canal_transmision OWNER TO repo_adm;

--
-- TOC entry 192 (class 1259 OID 186732)
-- Name: canal_transmision_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE canal_transmision_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE canal_transmision_id_seq OWNER TO repo_adm;

--
-- TOC entry 2870 (class 0 OID 0)
-- Dependencies: 192
-- Name: canal_transmision_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE canal_transmision_id_seq OWNED BY canal_transmision.id;


--
-- TOC entry 210 (class 1259 OID 186888)
-- Name: extractor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE extractor (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE extractor OWNER TO repo_adm;

--
-- TOC entry 209 (class 1259 OID 186886)
-- Name: extractor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE extractor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE extractor_id_seq OWNER TO repo_adm;

--
-- TOC entry 2871 (class 0 OID 0)
-- Dependencies: 209
-- Name: extractor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE extractor_id_seq OWNED BY extractor.id;


--
-- TOC entry 195 (class 1259 OID 186748)
-- Name: formato; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE formato (
    id integer NOT NULL,
    propiedades character varying(80)[],
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE formato OWNER TO repo_adm;

--
-- TOC entry 194 (class 1259 OID 186746)
-- Name: formato_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE formato_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE formato_id_seq OWNER TO repo_adm;

--
-- TOC entry 2872 (class 0 OID 0)
-- Dependencies: 194
-- Name: formato_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE formato_id_seq OWNED BY formato.id;


--
-- TOC entry 201 (class 1259 OID 186789)
-- Name: institucion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE institucion (
    id integer NOT NULL,
    subsecretaria_id integer,
    nombre character varying(120) NOT NULL,
    habilitado boolean DEFAULT true
);


ALTER TABLE institucion OWNER TO repo_adm;

--
-- TOC entry 200 (class 1259 OID 186787)
-- Name: institucion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE institucion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE institucion_id_seq OWNER TO repo_adm;

--
-- TOC entry 2873 (class 0 OID 0)
-- Dependencies: 200
-- Name: institucion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE institucion_id_seq OWNED BY institucion.id;


--
-- TOC entry 230 (class 1259 OID 187072)
-- Name: interaccion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE interaccion (
    id integer NOT NULL,
    recepcion_id integer NOT NULL,
    periodicidad_id integer NOT NULL,
    fecha_interaccion timestamp with time zone,
    fecha_creacion timestamp with time zone DEFAULT now(),
    tipo_interaccion_id integer NOT NULL,
    canal_id integer NOT NULL,
    tramite_id integer NOT NULL,
    localidad_id integer NOT NULL,
    hmac256 bytea
);


ALTER TABLE interaccion OWNER TO repo_adm;

--
-- TOC entry 229 (class 1259 OID 187070)
-- Name: interaccion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE interaccion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE interaccion_id_seq OWNER TO repo_adm;

--
-- TOC entry 2874 (class 0 OID 0)
-- Dependencies: 229
-- Name: interaccion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE interaccion_id_seq OWNED BY interaccion.id;


--
-- TOC entry 228 (class 1259 OID 187050)
-- Name: intermedio; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE intermedio (
    id integer NOT NULL,
    recepcion_id integer NOT NULL,
    tramite_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    data jsonb,
    hmac256 bytea
);


ALTER TABLE intermedio OWNER TO repo_adm;

--
-- TOC entry 227 (class 1259 OID 187048)
-- Name: intermedio_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE intermedio_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE intermedio_id_seq OWNER TO repo_adm;

--
-- TOC entry 2875 (class 0 OID 0)
-- Dependencies: 227
-- Name: intermedio_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE intermedio_id_seq OWNED BY intermedio.id;


--
-- TOC entry 185 (class 1259 OID 186687)
-- Name: localidad; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE localidad (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    codigo_ine_comuna integer,
    codigo_ine_provinica integer,
    codigo_ine_region integer,
    codigo_pais character varying(3) NOT NULL,
    tipo character varying(2) NOT NULL,
    habilitado boolean DEFAULT true
);


ALTER TABLE localidad OWNER TO repo_adm;

--
-- TOC entry 184 (class 1259 OID 186685)
-- Name: localidad_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE localidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE localidad_id_seq OWNER TO repo_adm;

--
-- TOC entry 2876 (class 0 OID 0)
-- Dependencies: 184
-- Name: localidad_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE localidad_id_seq OWNED BY localidad.id;


--
-- TOC entry 197 (class 1259 OID 186762)
-- Name: ministerio; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE ministerio (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    habilitado boolean DEFAULT true
);


ALTER TABLE ministerio OWNER TO repo_adm;

--
-- TOC entry 196 (class 1259 OID 186760)
-- Name: ministerio_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE ministerio_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE ministerio_id_seq OWNER TO repo_adm;

--
-- TOC entry 2877 (class 0 OID 0)
-- Dependencies: 196
-- Name: ministerio_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE ministerio_id_seq OWNED BY ministerio.id;


--
-- TOC entry 235 (class 1259 OID 187145)
-- Name: notificacion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE notificacion (
    id integer NOT NULL,
    usuario_id integer NOT NULL,
    receptor_id integer NOT NULL,
    fecha_creacion timestamp with time zone,
    texto text,
    despachada boolean DEFAULT false
);


ALTER TABLE notificacion OWNER TO repo_adm;

--
-- TOC entry 234 (class 1259 OID 187143)
-- Name: notificacion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE notificacion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE notificacion_id_seq OWNER TO repo_adm;

--
-- TOC entry 2878 (class 0 OID 0)
-- Dependencies: 234
-- Name: notificacion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE notificacion_id_seq OWNED BY notificacion.id;


--
-- TOC entry 191 (class 1259 OID 186720)
-- Name: periodicidad; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE periodicidad (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE periodicidad OWNER TO repo_adm;

--
-- TOC entry 190 (class 1259 OID 186718)
-- Name: periodicidad_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE periodicidad_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE periodicidad_id_seq OWNER TO repo_adm;

--
-- TOC entry 2879 (class 0 OID 0)
-- Dependencies: 190
-- Name: periodicidad_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE periodicidad_id_seq OWNED BY periodicidad.id;


--
-- TOC entry 187 (class 1259 OID 186698)
-- Name: persona; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE persona (
    id integer NOT NULL,
    rut integer NOT NULL,
    dv character varying(1) NOT NULL,
    nombre character varying(120) NOT NULL,
    primer_apellido character varying(120) NOT NULL,
    segundo_apellido character varying(120),
    fecha_nacimiento date,
    vulnerable boolean
);


ALTER TABLE persona OWNER TO repo_adm;

--
-- TOC entry 186 (class 1259 OID 186696)
-- Name: persona_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE persona_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE persona_id_seq OWNER TO repo_adm;

--
-- TOC entry 2880 (class 0 OID 0)
-- Dependencies: 186
-- Name: persona_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE persona_id_seq OWNED BY persona.id;


--
-- TOC entry 216 (class 1259 OID 186930)
-- Name: plantilla_campo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE plantilla_campo (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    nombre_campo character varying(120),
    descripcion text,
    extractor_id integer,
    transformador_id integer,
    validador_id integer,
    propiedades hstore,
    habilitado boolean DEFAULT true
);


ALTER TABLE plantilla_campo OWNER TO repo_adm;

--
-- TOC entry 215 (class 1259 OID 186928)
-- Name: plantilla_campo_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE plantilla_campo_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE plantilla_campo_id_seq OWNER TO repo_adm;

--
-- TOC entry 2881 (class 0 OID 0)
-- Dependencies: 215
-- Name: plantilla_campo_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE plantilla_campo_id_seq OWNED BY plantilla_campo.id;


--
-- TOC entry 205 (class 1259 OID 186824)
-- Name: plantilla_recepcion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE plantilla_recepcion (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true,
    plantilla text
);


ALTER TABLE plantilla_recepcion OWNER TO repo_adm;

--
-- TOC entry 204 (class 1259 OID 186822)
-- Name: plantilla_recepcion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE plantilla_recepcion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE plantilla_recepcion_id_seq OWNER TO repo_adm;

--
-- TOC entry 2882 (class 0 OID 0)
-- Dependencies: 204
-- Name: plantilla_recepcion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE plantilla_recepcion_id_seq OWNED BY plantilla_recepcion.id;


--
-- TOC entry 220 (class 1259 OID 186978)
-- Name: recepcion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE recepcion (
    id integer NOT NULL,
    receptor_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    estado character varying(40) NOT NULL,
    archivo character varying(2000) NOT NULL,
    hmac256 bytea,
    propiedades hstore
);


ALTER TABLE recepcion OWNER TO repo_adm;

--
-- TOC entry 224 (class 1259 OID 187009)
-- Name: recepcion_bitacora; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE recepcion_bitacora (
    id integer NOT NULL,
    recepcion_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    nivel character varying(80),
    log text,
    etapa character varying(80)
);


ALTER TABLE recepcion_bitacora OWNER TO repo_adm;

--
-- TOC entry 223 (class 1259 OID 187007)
-- Name: recepcion_bitacora_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE recepcion_bitacora_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE recepcion_bitacora_id_seq OWNER TO repo_adm;

--
-- TOC entry 2883 (class 0 OID 0)
-- Dependencies: 223
-- Name: recepcion_bitacora_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE recepcion_bitacora_id_seq OWNED BY recepcion_bitacora.id;


--
-- TOC entry 222 (class 1259 OID 186995)
-- Name: recepcion_estados; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE recepcion_estados (
    id integer NOT NULL,
    recepcion_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    estado character varying(40)
);


ALTER TABLE recepcion_estados OWNER TO repo_adm;

--
-- TOC entry 221 (class 1259 OID 186993)
-- Name: recepcion_estados_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE recepcion_estados_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE recepcion_estados_id_seq OWNER TO repo_adm;

--
-- TOC entry 2884 (class 0 OID 0)
-- Dependencies: 221
-- Name: recepcion_estados_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE recepcion_estados_id_seq OWNED BY recepcion_estados.id;


--
-- TOC entry 219 (class 1259 OID 186976)
-- Name: recepcion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE recepcion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE recepcion_id_seq OWNER TO repo_adm;

--
-- TOC entry 2885 (class 0 OID 0)
-- Dependencies: 219
-- Name: recepcion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE recepcion_id_seq OWNED BY recepcion.id;


--
-- TOC entry 207 (class 1259 OID 186838)
-- Name: receptor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE receptor (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    canal_transmision_id integer NOT NULL,
    formato_id integer NOT NULL,
    plantilla_recepcion_id integer NOT NULL,
    periodicidad_id integer NOT NULL,
    notificacion_diaria boolean DEFAULT false,
    propiedades hstore,
    habilitado boolean DEFAULT true
);


ALTER TABLE receptor OWNER TO repo_adm;

--
-- TOC entry 206 (class 1259 OID 186836)
-- Name: receptor_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE receptor_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE receptor_id_seq OWNER TO repo_adm;

--
-- TOC entry 2886 (class 0 OID 0)
-- Dependencies: 206
-- Name: receptor_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE receptor_id_seq OWNED BY receptor.id;


--
-- TOC entry 199 (class 1259 OID 186773)
-- Name: subsecretaria; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE subsecretaria (
    id integer NOT NULL,
    ministerio_id integer,
    nombre character varying(120) NOT NULL,
    habilitado boolean DEFAULT true
);


ALTER TABLE subsecretaria OWNER TO repo_adm;

--
-- TOC entry 198 (class 1259 OID 186771)
-- Name: subsecretaria_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE subsecretaria_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE subsecretaria_id_seq OWNER TO repo_adm;

--
-- TOC entry 2887 (class 0 OID 0)
-- Dependencies: 198
-- Name: subsecretaria_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE subsecretaria_id_seq OWNED BY subsecretaria.id;


--
-- TOC entry 183 (class 1259 OID 186673)
-- Name: tipo_interaccion; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tipo_interaccion (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE tipo_interaccion OWNER TO repo_adm;

--
-- TOC entry 182 (class 1259 OID 186671)
-- Name: tipo_interaccion_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tipo_interaccion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tipo_interaccion_id_seq OWNER TO repo_adm;

--
-- TOC entry 2888 (class 0 OID 0)
-- Dependencies: 182
-- Name: tipo_interaccion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_interaccion_id_seq OWNED BY tipo_interaccion.id;


--
-- TOC entry 203 (class 1259 OID 186805)
-- Name: tipo_tramite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tipo_tramite (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    institucion_id integer NOT NULL,
    codigo_pmg character varying(80),
    habilitado boolean DEFAULT true
);


ALTER TABLE tipo_tramite OWNER TO repo_adm;

--
-- TOC entry 202 (class 1259 OID 186803)
-- Name: tipo_tramite_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tipo_tramite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tipo_tramite_id_seq OWNER TO repo_adm;

--
-- TOC entry 2889 (class 0 OID 0)
-- Dependencies: 202
-- Name: tipo_tramite_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tipo_tramite_id_seq OWNED BY tipo_tramite.id;


--
-- TOC entry 208 (class 1259 OID 186871)
-- Name: tipo_tramite_receptor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tipo_tramite_receptor (
    tipo_tramite_id integer NOT NULL,
    receptor_id integer NOT NULL
);


ALTER TABLE tipo_tramite_receptor OWNER TO repo_adm;

--
-- TOC entry 226 (class 1259 OID 187026)
-- Name: tramite; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tramite (
    id integer NOT NULL,
    recepcion_id integer NOT NULL,
    tipo_tramite_id integer NOT NULL,
    persona_id integer NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now(),
    fecha_tramite timestamp without time zone
);


ALTER TABLE tramite OWNER TO repo_adm;

--
-- TOC entry 225 (class 1259 OID 187024)
-- Name: tramite_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE tramite_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE tramite_id_seq OWNER TO repo_adm;

--
-- TOC entry 2890 (class 0 OID 0)
-- Dependencies: 225
-- Name: tramite_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE tramite_id_seq OWNED BY tramite.id;


--
-- TOC entry 212 (class 1259 OID 186902)
-- Name: transformador; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE transformador (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE transformador OWNER TO repo_adm;

--
-- TOC entry 211 (class 1259 OID 186900)
-- Name: transformador_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE transformador_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE transformador_id_seq OWNER TO repo_adm;

--
-- TOC entry 2891 (class 0 OID 0)
-- Dependencies: 211
-- Name: transformador_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE transformador_id_seq OWNED BY transformador.id;


--
-- TOC entry 232 (class 1259 OID 187119)
-- Name: usuario; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE usuario (
    id integer NOT NULL,
    usuario character varying(120) NOT NULL,
    email character varying(120) NOT NULL,
    habilitado boolean DEFAULT true,
    telefono character varying(12)
);


ALTER TABLE usuario OWNER TO repo_adm;

--
-- TOC entry 231 (class 1259 OID 187117)
-- Name: usuario_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE usuario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE usuario_id_seq OWNER TO repo_adm;

--
-- TOC entry 2892 (class 0 OID 0)
-- Dependencies: 231
-- Name: usuario_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE usuario_id_seq OWNED BY usuario.id;


--
-- TOC entry 233 (class 1259 OID 187128)
-- Name: usuario_receptor; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE usuario_receptor (
    usuario_id integer NOT NULL,
    receptor_id integer NOT NULL
);


ALTER TABLE usuario_receptor OWNER TO repo_adm;

--
-- TOC entry 214 (class 1259 OID 186916)
-- Name: validador; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE validador (
    id integer NOT NULL,
    nombre character varying(120) NOT NULL,
    descripcion text,
    habilitado boolean DEFAULT true
);


ALTER TABLE validador OWNER TO repo_adm;

--
-- TOC entry 213 (class 1259 OID 186914)
-- Name: validador_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE validador_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE validador_id_seq OWNER TO repo_adm;

--
-- TOC entry 2893 (class 0 OID 0)
-- Dependencies: 213
-- Name: validador_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE validador_id_seq OWNED BY validador.id;


--
-- TOC entry 2553 (class 2604 OID 186962)
-- Name: campo id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY campo ALTER COLUMN id SET DEFAULT nextval('campo_id_seq'::regclass);


--
-- TOC entry 2524 (class 2604 OID 186709)
-- Name: canal id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY canal ALTER COLUMN id SET DEFAULT nextval('canal_id_seq'::regclass);


--
-- TOC entry 2528 (class 2604 OID 186737)
-- Name: canal_transmision id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY canal_transmision ALTER COLUMN id SET DEFAULT nextval('canal_transmision_id_seq'::regclass);


--
-- TOC entry 2545 (class 2604 OID 186891)
-- Name: extractor id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY extractor ALTER COLUMN id SET DEFAULT nextval('extractor_id_seq'::regclass);


--
-- TOC entry 2530 (class 2604 OID 186751)
-- Name: formato id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formato ALTER COLUMN id SET DEFAULT nextval('formato_id_seq'::regclass);


--
-- TOC entry 2536 (class 2604 OID 186792)
-- Name: institucion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY institucion ALTER COLUMN id SET DEFAULT nextval('institucion_id_seq'::regclass);


--
-- TOC entry 2565 (class 2604 OID 187075)
-- Name: interaccion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion ALTER COLUMN id SET DEFAULT nextval('interaccion_id_seq'::regclass);


--
-- TOC entry 2563 (class 2604 OID 187053)
-- Name: intermedio id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY intermedio ALTER COLUMN id SET DEFAULT nextval('intermedio_id_seq'::regclass);


--
-- TOC entry 2521 (class 2604 OID 186690)
-- Name: localidad id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY localidad ALTER COLUMN id SET DEFAULT nextval('localidad_id_seq'::regclass);


--
-- TOC entry 2532 (class 2604 OID 186765)
-- Name: ministerio id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ministerio ALTER COLUMN id SET DEFAULT nextval('ministerio_id_seq'::regclass);


--
-- TOC entry 2569 (class 2604 OID 187148)
-- Name: notificacion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY notificacion ALTER COLUMN id SET DEFAULT nextval('notificacion_id_seq'::regclass);


--
-- TOC entry 2526 (class 2604 OID 186723)
-- Name: periodicidad id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY periodicidad ALTER COLUMN id SET DEFAULT nextval('periodicidad_id_seq'::regclass);


--
-- TOC entry 2523 (class 2604 OID 186701)
-- Name: persona id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY persona ALTER COLUMN id SET DEFAULT nextval('persona_id_seq'::regclass);


--
-- TOC entry 2551 (class 2604 OID 186933)
-- Name: plantilla_campo id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_campo ALTER COLUMN id SET DEFAULT nextval('plantilla_campo_id_seq'::regclass);


--
-- TOC entry 2540 (class 2604 OID 186827)
-- Name: plantilla_recepcion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_recepcion ALTER COLUMN id SET DEFAULT nextval('plantilla_recepcion_id_seq'::regclass);


--
-- TOC entry 2555 (class 2604 OID 186981)
-- Name: recepcion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion ALTER COLUMN id SET DEFAULT nextval('recepcion_id_seq'::regclass);


--
-- TOC entry 2559 (class 2604 OID 187012)
-- Name: recepcion_bitacora id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion_bitacora ALTER COLUMN id SET DEFAULT nextval('recepcion_bitacora_id_seq'::regclass);


--
-- TOC entry 2557 (class 2604 OID 186998)
-- Name: recepcion_estados id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion_estados ALTER COLUMN id SET DEFAULT nextval('recepcion_estados_id_seq'::regclass);


--
-- TOC entry 2542 (class 2604 OID 186841)
-- Name: receptor id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor ALTER COLUMN id SET DEFAULT nextval('receptor_id_seq'::regclass);


--
-- TOC entry 2534 (class 2604 OID 186776)
-- Name: subsecretaria id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subsecretaria ALTER COLUMN id SET DEFAULT nextval('subsecretaria_id_seq'::regclass);


--
-- TOC entry 2519 (class 2604 OID 186676)
-- Name: tipo_interaccion id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_interaccion ALTER COLUMN id SET DEFAULT nextval('tipo_interaccion_id_seq'::regclass);


--
-- TOC entry 2538 (class 2604 OID 186808)
-- Name: tipo_tramite id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite ALTER COLUMN id SET DEFAULT nextval('tipo_tramite_id_seq'::regclass);


--
-- TOC entry 2562 (class 2604 OID 187029)
-- Name: tramite id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tramite ALTER COLUMN id SET DEFAULT nextval('tramite_id_seq'::regclass);


--
-- TOC entry 2547 (class 2604 OID 186905)
-- Name: transformador id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transformador ALTER COLUMN id SET DEFAULT nextval('transformador_id_seq'::regclass);


--
-- TOC entry 2567 (class 2604 OID 187122)
-- Name: usuario id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuario ALTER COLUMN id SET DEFAULT nextval('usuario_id_seq'::regclass);


--
-- TOC entry 2549 (class 2604 OID 186919)
-- Name: validador id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY validador ALTER COLUMN id SET DEFAULT nextval('validador_id_seq'::regclass);


--
-- TOC entry 2841 (class 0 OID 186959)
-- Dependencies: 218
-- Data for Name: campo; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2894 (class 0 OID 0)
-- Dependencies: 217
-- Name: campo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('campo_id_seq', 1, false);


--
-- TOC entry 2812 (class 0 OID 186706)
-- Dependencies: 189
-- Data for Name: canal; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO canal VALUES (1, 'WEB', 'WEB', true);


--
-- TOC entry 2895 (class 0 OID 0)
-- Dependencies: 188
-- Name: canal_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('canal_id_seq', 1, true);


--
-- TOC entry 2816 (class 0 OID 186734)
-- Dependencies: 193
-- Data for Name: canal_transmision; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO canal_transmision VALUES (1, NULL, 'SFTP', 'SFTP', true);


--
-- TOC entry 2896 (class 0 OID 0)
-- Dependencies: 192
-- Name: canal_transmision_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('canal_transmision_id_seq', 1, true);


--
-- TOC entry 2833 (class 0 OID 186888)
-- Dependencies: 210
-- Data for Name: extractor; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2897 (class 0 OID 0)
-- Dependencies: 209
-- Name: extractor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('extractor_id_seq', 1, false);


--
-- TOC entry 2818 (class 0 OID 186748)
-- Dependencies: 195
-- Data for Name: formato; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO formato VALUES (1, NULL, 'LOG', 'LOG', true);


--
-- TOC entry 2898 (class 0 OID 0)
-- Dependencies: 194
-- Name: formato_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('formato_id_seq', 1, true);


--
-- TOC entry 2824 (class 0 OID 186789)
-- Dependencies: 201
-- Data for Name: institucion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO institucion VALUES (1, 1, 'INSTITUCION', true);


--
-- TOC entry 2899 (class 0 OID 0)
-- Dependencies: 200
-- Name: institucion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('institucion_id_seq', 1, true);




--
-- TOC entry 2900 (class 0 OID 0)
-- Dependencies: 229
-- Name: interaccion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('interaccion_id_seq', 63898, true);


--
-- TOC entry 2851 (class 0 OID 187050)
-- Dependencies: 228
-- Data for Name: intermedio; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2901 (class 0 OID 0)
-- Dependencies: 227
-- Name: intermedio_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('intermedio_id_seq', 1, false);


--
-- TOC entry 2808 (class 0 OID 186687)
-- Dependencies: 185
-- Data for Name: localidad; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO localidad VALUES (1, 'SIN LOCALIDAD', NULL, NULL, NULL, 'CL', 'SL', true);


--
-- TOC entry 2902 (class 0 OID 0)
-- Dependencies: 184
-- Name: localidad_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('localidad_id_seq', 3, true);


--
-- TOC entry 2820 (class 0 OID 186762)
-- Dependencies: 197
-- Data for Name: ministerio; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO ministerio VALUES (1, 'MINISTERIO', true);


--
-- TOC entry 2903 (class 0 OID 0)
-- Dependencies: 196
-- Name: ministerio_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('ministerio_id_seq', 1, true);


--
-- TOC entry 2858 (class 0 OID 187145)
-- Dependencies: 235
-- Data for Name: notificacion; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2904 (class 0 OID 0)
-- Dependencies: 234
-- Name: notificacion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('notificacion_id_seq', 1, false);


--
-- TOC entry 2814 (class 0 OID 186720)
-- Dependencies: 191
-- Data for Name: periodicidad; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO periodicidad VALUES (1, 'TIMESTAMP', 'TIMESTAMP', true);
INSERT INTO periodicidad VALUES (2, 'DIARIA', 'DIARIA', true);
INSERT INTO periodicidad VALUES (3, 'SEMANAL', 'SEMANAL', true);
INSERT INTO periodicidad VALUES (4, 'MENSUAL', 'MENSUAL', true);
INSERT INTO periodicidad VALUES (5, 'ANUAL', 'ANUAL', true);


--
-- TOC entry 2905 (class 0 OID 0)
-- Dependencies: 190
-- Name: periodicidad_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('periodicidad_id_seq', 5, true);


--
-- TOC entry 2810 (class 0 OID 186698)
-- Dependencies: 187
-- Data for Name: persona; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO persona VALUES (1, 1, '9', 'NO INFORMADO', 'NO INFORMADO', NULL, NULL, NULL);


--
-- TOC entry 2906 (class 0 OID 0)
-- Dependencies: 186
-- Name: persona_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('persona_id_seq', 3, true);


--
-- TOC entry 2839 (class 0 OID 186930)
-- Dependencies: 216
-- Data for Name: plantilla_campo; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2907 (class 0 OID 0)
-- Dependencies: 215
-- Name: plantilla_campo_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('plantilla_campo_id_seq', 1, false);


--
-- TOC entry 2828 (class 0 OID 186824)
-- Dependencies: 205
-- Data for Name: plantilla_recepcion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO plantilla_recepcion VALUES (1, 'PLANTILLA_RECEPCION', 'PLANTILLA_RECEPCION', true, 'PLANTILLA_RECEPCION');


--
-- TOC entry 2908 (class 0 OID 0)
-- Dependencies: 204
-- Name: plantilla_recepcion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('plantilla_recepcion_id_seq', 1, true);



--
-- TOC entry 2847 (class 0 OID 187009)
-- Dependencies: 224
-- Data for Name: recepcion_bitacora; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2909 (class 0 OID 0)
-- Dependencies: 223
-- Name: recepcion_bitacora_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('recepcion_bitacora_id_seq', 1, false);


--
-- TOC entry 2845 (class 0 OID 186995)
-- Dependencies: 222
-- Data for Name: recepcion_estados; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2910 (class 0 OID 0)
-- Dependencies: 221
-- Name: recepcion_estados_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('recepcion_estados_id_seq', 1, false);


--
-- TOC entry 2911 (class 0 OID 0)
-- Dependencies: 219
-- Name: recepcion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('recepcion_id_seq', 54, true);


--
-- TOC entry 2830 (class 0 OID 186838)
-- Dependencies: 207
-- Data for Name: receptor; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO receptor VALUES (1, 'RECEPTOR', NULL, 1, 1, 1, 2, false, NULL, true);


--
-- TOC entry 2912 (class 0 OID 0)
-- Dependencies: 206
-- Name: receptor_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('receptor_id_seq', 1, true);


--
-- TOC entry 2822 (class 0 OID 186773)
-- Dependencies: 199
-- Data for Name: subsecretaria; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO subsecretaria VALUES (1, 1, 'SUBSECRETARIA', true);


--
-- TOC entry 2913 (class 0 OID 0)
-- Dependencies: 198
-- Name: subsecretaria_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('subsecretaria_id_seq', 1, true);


--
-- TOC entry 2806 (class 0 OID 186673)
-- Dependencies: 183
-- Data for Name: tipo_interaccion; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tipo_interaccion VALUES (1, 'INICIO', 'INICIO', true);
INSERT INTO tipo_interaccion VALUES (2, 'FIN', 'FIN', true);


--
-- TOC entry 2914 (class 0 OID 0)
-- Dependencies: 182
-- Name: tipo_interaccion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_interaccion_id_seq', 2, true);


--
-- TOC entry 2826 (class 0 OID 186805)
-- Dependencies: 203
-- Data for Name: tipo_tramite; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO tipo_tramite VALUES (1, 'CARTAS', 'CARTAS', 1, '1', true);
INSERT INTO tipo_tramite VALUES (2, 'CERTIFICADOS', 'CERTIFICADOS', 1, '1', true);
INSERT INTO tipo_tramite VALUES (3, 'CONSTANCIA', 'CONSTANCIA', 1, '1', true);
INSERT INTO tipo_tramite VALUES (4, 'PARITARIO', 'PARITARIO', 1, '1', true);
INSERT INTO tipo_tramite VALUES (5, 'PARITARIOFAENA', 'PARITARIOFAENA', 1, '1', true);
INSERT INTO tipo_tramite VALUES (6, 'CENTRALIZACION', 'CENTRALIZACION', 1, '1', true);
INSERT INTO tipo_tramite VALUES (7, 'MULTAS', 'MULTAS', 1, '1', true);
INSERT INTO tipo_tramite VALUES (8, 'INTERMEDIARIOAGRICOLA', 'INTERMEDIARIOAGRICOLA', 1, '1', true);
INSERT INTO tipo_tramite VALUES (9, 'CONTRATOMENOR', 'CONTRATOMENOR', 1, '1', true);
INSERT INTO tipo_tramite VALUES (10, 'TRABCASAPARTICULAR', 'TRABCASAPARTICULAR', 1, '1', true);


--
-- TOC entry 2915 (class 0 OID 0)
-- Dependencies: 202
-- Name: tipo_tramite_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tipo_tramite_id_seq', 10, true);


--
-- TOC entry 2831 (class 0 OID 186871)
-- Dependencies: 208
-- Data for Name: tipo_tramite_receptor; Type: TABLE DATA; Schema: public; Owner: postgres
--


--
-- TOC entry 2916 (class 0 OID 0)
-- Dependencies: 225
-- Name: tramite_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('tramite_id_seq', 63900, true);


--
-- TOC entry 2835 (class 0 OID 186902)
-- Dependencies: 212
-- Data for Name: transformador; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2917 (class 0 OID 0)
-- Dependencies: 211
-- Name: transformador_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('transformador_id_seq', 1, false);


--
-- TOC entry 2855 (class 0 OID 187119)
-- Dependencies: 232
-- Data for Name: usuario; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2918 (class 0 OID 0)
-- Dependencies: 231
-- Name: usuario_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('usuario_id_seq', 1, false);


--
-- TOC entry 2856 (class 0 OID 187128)
-- Dependencies: 233
-- Data for Name: usuario_receptor; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2837 (class 0 OID 186916)
-- Dependencies: 214
-- Data for Name: validador; Type: TABLE DATA; Schema: public; Owner: postgres
--



--
-- TOC entry 2919 (class 0 OID 0)
-- Dependencies: 213
-- Name: validador_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('validador_id_seq', 1, false);


--
-- TOC entry 2640 (class 2606 OID 186965)
-- Name: campo pk_campo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY campo
    ADD CONSTRAINT pk_campo PRIMARY KEY (id);


--
-- TOC entry 2582 (class 2606 OID 186715)
-- Name: canal pk_canal; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY canal
    ADD CONSTRAINT pk_canal PRIMARY KEY (id);


--
-- TOC entry 2590 (class 2606 OID 186743)
-- Name: canal_transmision pk_canal_transmision; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY canal_transmision
    ADD CONSTRAINT pk_canal_transmision PRIMARY KEY (id);


--
-- TOC entry 2624 (class 2606 OID 186897)
-- Name: extractor pk_extractor; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY extractor
    ADD CONSTRAINT pk_extractor PRIMARY KEY (id);


--
-- TOC entry 2594 (class 2606 OID 186757)
-- Name: formato pk_formato; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formato
    ADD CONSTRAINT pk_formato PRIMARY KEY (id);


--
-- TOC entry 2606 (class 2606 OID 186795)
-- Name: institucion pk_institucion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY institucion
    ADD CONSTRAINT pk_institucion PRIMARY KEY (id);


--
-- TOC entry 2652 (class 2606 OID 187081)
-- Name: interaccion pk_interaccion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT pk_interaccion PRIMARY KEY (id);


--
-- TOC entry 2650 (class 2606 OID 187059)
-- Name: intermedio pk_intermedio; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY intermedio
    ADD CONSTRAINT pk_intermedio PRIMARY KEY (id);


--
-- TOC entry 2576 (class 2606 OID 186693)
-- Name: localidad pk_localidad; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY localidad
    ADD CONSTRAINT pk_localidad PRIMARY KEY (id);


--
-- TOC entry 2598 (class 2606 OID 186768)
-- Name: ministerio pk_ministerio; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ministerio
    ADD CONSTRAINT pk_ministerio PRIMARY KEY (id);


--
-- TOC entry 2660 (class 2606 OID 187154)
-- Name: notificacion pk_notificacion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY notificacion
    ADD CONSTRAINT pk_notificacion PRIMARY KEY (id);


--
-- TOC entry 2586 (class 2606 OID 186729)
-- Name: periodicidad pk_periodicidad; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY periodicidad
    ADD CONSTRAINT pk_periodicidad PRIMARY KEY (id);


--
-- TOC entry 2580 (class 2606 OID 186703)
-- Name: persona pk_persona; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY persona
    ADD CONSTRAINT pk_persona PRIMARY KEY (id);


--
-- TOC entry 2636 (class 2606 OID 186939)
-- Name: plantilla_campo pk_plantilla_campo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_campo
    ADD CONSTRAINT pk_plantilla_campo PRIMARY KEY (id);


--
-- TOC entry 2614 (class 2606 OID 186833)
-- Name: plantilla_recepcion pk_plantilla_recepcion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_recepcion
    ADD CONSTRAINT pk_plantilla_recepcion PRIMARY KEY (id);


--
-- TOC entry 2642 (class 2606 OID 186987)
-- Name: recepcion pk_recepcion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion
    ADD CONSTRAINT pk_recepcion PRIMARY KEY (id);


--
-- TOC entry 2646 (class 2606 OID 187018)
-- Name: recepcion_bitacora pk_recepcion_bitacora; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion_bitacora
    ADD CONSTRAINT pk_recepcion_bitacora PRIMARY KEY (id);


--
-- TOC entry 2644 (class 2606 OID 187001)
-- Name: recepcion_estados pk_recepcion_estados; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion_estados
    ADD CONSTRAINT pk_recepcion_estados PRIMARY KEY (id);


--
-- TOC entry 2618 (class 2606 OID 186848)
-- Name: receptor pk_receptor; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor
    ADD CONSTRAINT pk_receptor PRIMARY KEY (id);


--
-- TOC entry 2602 (class 2606 OID 186779)
-- Name: subsecretaria pk_subsecretaria; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subsecretaria
    ADD CONSTRAINT pk_subsecretaria PRIMARY KEY (id);


--
-- TOC entry 2572 (class 2606 OID 186682)
-- Name: tipo_interaccion pk_tipo_interaccion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_interaccion
    ADD CONSTRAINT pk_tipo_interaccion PRIMARY KEY (id);


--
-- TOC entry 2610 (class 2606 OID 186814)
-- Name: tipo_tramite pk_tipo_tramite; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite
    ADD CONSTRAINT pk_tipo_tramite PRIMARY KEY (id);


--
-- TOC entry 2648 (class 2606 OID 187032)
-- Name: tramite pk_tramite; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tramite
    ADD CONSTRAINT pk_tramite PRIMARY KEY (id);


--
-- TOC entry 2622 (class 2606 OID 186875)
-- Name: tipo_tramite_receptor pk_tramite_receptor; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite_receptor
    ADD CONSTRAINT pk_tramite_receptor PRIMARY KEY (receptor_id, tipo_tramite_id);


--
-- TOC entry 2628 (class 2606 OID 186911)
-- Name: transformador pk_transformador; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transformador
    ADD CONSTRAINT pk_transformador PRIMARY KEY (id);


--
-- TOC entry 2654 (class 2606 OID 187125)
-- Name: usuario pk_usuario; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuario
    ADD CONSTRAINT pk_usuario PRIMARY KEY (id);


--
-- TOC entry 2658 (class 2606 OID 187132)
-- Name: usuario_receptor pk_usuarios_receptor; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuario_receptor
    ADD CONSTRAINT pk_usuarios_receptor PRIMARY KEY (receptor_id, usuario_id);


--
-- TOC entry 2632 (class 2606 OID 186925)
-- Name: validador pk_validador; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY validador
    ADD CONSTRAINT pk_validador PRIMARY KEY (id);


--
-- TOC entry 2584 (class 2606 OID 186717)
-- Name: canal uq_canal_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY canal
    ADD CONSTRAINT uq_canal_nombre UNIQUE (nombre);


--
-- TOC entry 2592 (class 2606 OID 186745)
-- Name: canal_transmision uq_canal_transmision_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY canal_transmision
    ADD CONSTRAINT uq_canal_transmision_nombre UNIQUE (nombre);


--
-- TOC entry 2626 (class 2606 OID 186899)
-- Name: extractor uq_extractor; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY extractor
    ADD CONSTRAINT uq_extractor UNIQUE (nombre);


--
-- TOC entry 2596 (class 2606 OID 186759)
-- Name: formato uq_formato_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY formato
    ADD CONSTRAINT uq_formato_nombre UNIQUE (nombre);


--
-- TOC entry 2608 (class 2606 OID 186797)
-- Name: institucion uq_institucion_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY institucion
    ADD CONSTRAINT uq_institucion_nombre UNIQUE (nombre);


--
-- TOC entry 2578 (class 2606 OID 186695)
-- Name: localidad uq_localidad_interaccion_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY localidad
    ADD CONSTRAINT uq_localidad_interaccion_nombre UNIQUE (nombre);


--
-- TOC entry 2600 (class 2606 OID 186770)
-- Name: ministerio uq_ministerio_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY ministerio
    ADD CONSTRAINT uq_ministerio_nombre UNIQUE (nombre);


--
-- TOC entry 2588 (class 2606 OID 186731)
-- Name: periodicidad uq_periodicidad_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY periodicidad
    ADD CONSTRAINT uq_periodicidad_nombre UNIQUE (nombre);


--
-- TOC entry 2638 (class 2606 OID 186941)
-- Name: plantilla_campo uq_plantilla_campo; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_campo
    ADD CONSTRAINT uq_plantilla_campo UNIQUE (nombre);


--
-- TOC entry 2616 (class 2606 OID 186835)
-- Name: plantilla_recepcion uq_plantilla_recepcion; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_recepcion
    ADD CONSTRAINT uq_plantilla_recepcion UNIQUE (nombre);


--
-- TOC entry 2620 (class 2606 OID 186850)
-- Name: receptor uq_receptor_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor
    ADD CONSTRAINT uq_receptor_nombre UNIQUE (nombre);


--
-- TOC entry 2604 (class 2606 OID 186781)
-- Name: subsecretaria uq_subsecretaria_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subsecretaria
    ADD CONSTRAINT uq_subsecretaria_nombre UNIQUE (nombre);


--
-- TOC entry 2574 (class 2606 OID 186684)
-- Name: tipo_interaccion uq_tipo_interaccion_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_interaccion
    ADD CONSTRAINT uq_tipo_interaccion_nombre UNIQUE (nombre);


--
-- TOC entry 2612 (class 2606 OID 186816)
-- Name: tipo_tramite uq_tramite_nombre; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite
    ADD CONSTRAINT uq_tramite_nombre UNIQUE (nombre);


--
-- TOC entry 2630 (class 2606 OID 186913)
-- Name: transformador uq_transformador; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transformador
    ADD CONSTRAINT uq_transformador UNIQUE (nombre);


--
-- TOC entry 2656 (class 2606 OID 187127)
-- Name: usuario uq_usuario_usuario; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuario
    ADD CONSTRAINT uq_usuario_usuario UNIQUE (usuario);


--
-- TOC entry 2634 (class 2606 OID 186927)
-- Name: validador uq_validador; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY validador
    ADD CONSTRAINT uq_validador UNIQUE (nombre);


--
-- TOC entry 2673 (class 2606 OID 186966)
-- Name: campo fk_campo_plantilla; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY campo
    ADD CONSTRAINT fk_campo_plantilla FOREIGN KEY (plantilla_campo_id) REFERENCES plantilla_campo(id);


--
-- TOC entry 2674 (class 2606 OID 186971)
-- Name: campo fk_campo_receptor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY campo
    ADD CONSTRAINT fk_campo_receptor FOREIGN KEY (receptor_id) REFERENCES receptor(id);


--
-- TOC entry 2675 (class 2606 OID 186988)
-- Name: recepcion fk_campo_receptor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion
    ADD CONSTRAINT fk_campo_receptor FOREIGN KEY (receptor_id) REFERENCES receptor(id);


--
-- TOC entry 2662 (class 2606 OID 186798)
-- Name: institucion fk_institucion_subsecretaria; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY institucion
    ADD CONSTRAINT fk_institucion_subsecretaria FOREIGN KEY (subsecretaria_id) REFERENCES subsecretaria(id);


--
-- TOC entry 2684 (class 2606 OID 187087)
-- Name: interaccion fk_interaccion_canal; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT fk_interaccion_canal FOREIGN KEY (canal_id) REFERENCES canal(id);


--
-- TOC entry 2688 (class 2606 OID 187112)
-- Name: interaccion fk_interaccion_localidad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT fk_interaccion_localidad FOREIGN KEY (localidad_id) REFERENCES localidad(id);


--
-- TOC entry 2687 (class 2606 OID 187102)
-- Name: interaccion fk_interaccion_periodicidad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT fk_interaccion_periodicidad FOREIGN KEY (periodicidad_id) REFERENCES periodicidad(id);


--
-- TOC entry 2686 (class 2606 OID 187097)
-- Name: interaccion fk_interaccion_recepcion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT fk_interaccion_recepcion FOREIGN KEY (recepcion_id) REFERENCES recepcion(id);


--
-- TOC entry 2683 (class 2606 OID 187082)
-- Name: interaccion fk_interaccion_tipo_interaccion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT fk_interaccion_tipo_interaccion FOREIGN KEY (tipo_interaccion_id) REFERENCES tipo_interaccion(id);


--
-- TOC entry 2685 (class 2606 OID 187092)
-- Name: interaccion fk_interaccion_tramite; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY interaccion
    ADD CONSTRAINT fk_interaccion_tramite FOREIGN KEY (tramite_id) REFERENCES tramite(id);


--
-- TOC entry 2681 (class 2606 OID 187060)
-- Name: intermedio fk_intermedio_recepcion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY intermedio
    ADD CONSTRAINT fk_intermedio_recepcion FOREIGN KEY (recepcion_id) REFERENCES recepcion(id);


--
-- TOC entry 2682 (class 2606 OID 187065)
-- Name: intermedio fk_intermedio_tramite; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY intermedio
    ADD CONSTRAINT fk_intermedio_tramite FOREIGN KEY (tramite_id) REFERENCES tramite(id);


--
-- TOC entry 2670 (class 2606 OID 186942)
-- Name: plantilla_campo fk_plantilla_campo_extractor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_campo
    ADD CONSTRAINT fk_plantilla_campo_extractor FOREIGN KEY (extractor_id) REFERENCES extractor(id);


--
-- TOC entry 2671 (class 2606 OID 186947)
-- Name: plantilla_campo fk_plantilla_campo_transformador; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_campo
    ADD CONSTRAINT fk_plantilla_campo_transformador FOREIGN KEY (transformador_id) REFERENCES transformador(id);


--
-- TOC entry 2672 (class 2606 OID 186952)
-- Name: plantilla_campo fk_plantilla_campo_validador; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY plantilla_campo
    ADD CONSTRAINT fk_plantilla_campo_validador FOREIGN KEY (validador_id) REFERENCES validador(id);


--
-- TOC entry 2677 (class 2606 OID 187019)
-- Name: recepcion_bitacora fk_recepcion_bitacora_recepcion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion_bitacora
    ADD CONSTRAINT fk_recepcion_bitacora_recepcion FOREIGN KEY (recepcion_id) REFERENCES recepcion(id);


--
-- TOC entry 2676 (class 2606 OID 187002)
-- Name: recepcion_estados fk_recepcion_estados_recepcion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY recepcion_estados
    ADD CONSTRAINT fk_recepcion_estados_recepcion FOREIGN KEY (recepcion_id) REFERENCES recepcion(id);


--
-- TOC entry 2665 (class 2606 OID 186856)
-- Name: receptor fk_receptor_canal_transmision; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor
    ADD CONSTRAINT fk_receptor_canal_transmision FOREIGN KEY (canal_transmision_id) REFERENCES canal_transmision(id);


--
-- TOC entry 2666 (class 2606 OID 186861)
-- Name: receptor fk_receptor_formato; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor
    ADD CONSTRAINT fk_receptor_formato FOREIGN KEY (formato_id) REFERENCES formato(id);


--
-- TOC entry 2667 (class 2606 OID 186866)
-- Name: receptor fk_receptor_periodicidad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor
    ADD CONSTRAINT fk_receptor_periodicidad FOREIGN KEY (periodicidad_id) REFERENCES periodicidad(id);


--
-- TOC entry 2664 (class 2606 OID 186851)
-- Name: receptor fk_receptor_plantilla_recepcion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY receptor
    ADD CONSTRAINT fk_receptor_plantilla_recepcion FOREIGN KEY (plantilla_recepcion_id) REFERENCES plantilla_recepcion(id);


--
-- TOC entry 2661 (class 2606 OID 186782)
-- Name: subsecretaria fk_subsecretaria_ministerio; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subsecretaria
    ADD CONSTRAINT fk_subsecretaria_ministerio FOREIGN KEY (ministerio_id) REFERENCES ministerio(id);


--
-- TOC entry 2663 (class 2606 OID 186817)
-- Name: tipo_tramite fk_tramite_institucion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite
    ADD CONSTRAINT fk_tramite_institucion FOREIGN KEY (institucion_id) REFERENCES institucion(id);


--
-- TOC entry 2679 (class 2606 OID 187043)
-- Name: tramite fk_tramite_persona; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tramite
    ADD CONSTRAINT fk_tramite_persona FOREIGN KEY (persona_id) REFERENCES persona(id);


--
-- TOC entry 2678 (class 2606 OID 187033)
-- Name: tramite fk_tramite_recepcion; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tramite
    ADD CONSTRAINT fk_tramite_recepcion FOREIGN KEY (recepcion_id) REFERENCES recepcion(id);


--
-- TOC entry 2669 (class 2606 OID 186881)
-- Name: tipo_tramite_receptor fk_tramite_receptor_receptor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite_receptor
    ADD CONSTRAINT fk_tramite_receptor_receptor FOREIGN KEY (receptor_id) REFERENCES receptor(id);


--
-- TOC entry 2668 (class 2606 OID 186876)
-- Name: tipo_tramite_receptor fk_tramite_receptor_tipo_tramite; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tipo_tramite_receptor
    ADD CONSTRAINT fk_tramite_receptor_tipo_tramite FOREIGN KEY (tipo_tramite_id) REFERENCES tipo_tramite(id);


--
-- TOC entry 2680 (class 2606 OID 192044)
-- Name: tramite fk_tramite_tipo_tramite; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tramite
    ADD CONSTRAINT fk_tramite_tipo_tramite FOREIGN KEY (tipo_tramite_id) REFERENCES tipo_tramite(id);


--
-- TOC entry 2690 (class 2606 OID 187138)
-- Name: usuario_receptor fk_usuario_receptor_receptor; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuario_receptor
    ADD CONSTRAINT fk_usuario_receptor_receptor FOREIGN KEY (receptor_id) REFERENCES receptor(id);


--
-- TOC entry 2689 (class 2606 OID 187133)
-- Name: usuario_receptor fk_usuario_receptor_usuario; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY usuario_receptor
    ADD CONSTRAINT fk_usuario_receptor_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id);


create table mantenedor (
    tabla varchar(120), 
    singular varchar(120), 
    plural varchar(120),
    constraint pk_mantenedor primary key (tabla)
);

insert into mantenedor (tabla, singular, plural) values ('canal','Canal de interacción','Canales de interacción');
insert into mantenedor (tabla, singular, plural) values ('canal_transmision','Canal de transmisión','Canales de transmision');
insert into mantenedor (tabla, singular, plural) values ('extractor','Extractor','Extractores');
insert into mantenedor (tabla, singular, plural) values ('formato','Formato','Formatos');
insert into mantenedor (tabla, singular, plural) values ('periodicidad','Periodicidad','Periodicidades');
insert into mantenedor (tabla, singular, plural) values ('tipo_interaccion','Tipo de interacción','Tipos de interacción');
insert into mantenedor (tabla, singular, plural) values ('transformador','Transformador','Transformadores');
insert into mantenedor (tabla, singular, plural) values ('validador','Validador','Validadores');
