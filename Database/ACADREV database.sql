--
-- PostgreSQL database dump
--

\restrict ui5xdV4JuibdiRSz0FATTNCJYVIdlb26oYPeNN3eXqFCcY6TxvKGBfcvtddXb4O

-- Dumped from database version 16.10
-- Dumped by pg_dump version 16.10

-- Started on 2025-10-24 22:17:34

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 23668)
-- Name: acadrev_schema; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA acadrev_schema;


ALTER SCHEMA acadrev_schema OWNER TO postgres;

--
-- TOC entry 850 (class 1247 OID 23730)
-- Name: user_role; Type: TYPE; Schema: acadrev_schema; Owner: postgres
--

CREATE TYPE acadrev_schema.user_role AS ENUM (
    'COMPANY_OWNER',
    'AUDITOR',
    'ADMIN'
);


ALTER TYPE acadrev_schema.user_role OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 225 (class 1259 OID 23852)
-- Name: audit_review; Type: TABLE; Schema: acadrev_schema; Owner: postgres
--

CREATE TABLE acadrev_schema.audit_review (
    id integer NOT NULL,
    document_id integer NOT NULL,
    auditor_id integer NOT NULL,
    rating integer,
    comments text,
    reviewed_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT audit_review_rating_check CHECK (((rating >= 1) AND (rating <= 100)))
);


ALTER TABLE acadrev_schema.audit_review OWNER TO postgres;

--
-- TOC entry 224 (class 1259 OID 23851)
-- Name: audit_review_id_seq; Type: SEQUENCE; Schema: acadrev_schema; Owner: postgres
--

CREATE SEQUENCE acadrev_schema.audit_review_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE acadrev_schema.audit_review_id_seq OWNER TO postgres;

--
-- TOC entry 4900 (class 0 OID 0)
-- Dependencies: 224
-- Name: audit_review_id_seq; Type: SEQUENCE OWNED BY; Schema: acadrev_schema; Owner: postgres
--

ALTER SEQUENCE acadrev_schema.audit_review_id_seq OWNED BY acadrev_schema.audit_review.id;


--
-- TOC entry 221 (class 1259 OID 23763)
-- Name: company_profile; Type: TABLE; Schema: acadrev_schema; Owner: postgres
--

CREATE TABLE acadrev_schema.company_profile (
    id integer NOT NULL,
    user_id integer NOT NULL,
    name character varying(255) NOT NULL,
    address character varying(255),
    industry character varying(100),
    phone character varying(50)
);


ALTER TABLE acadrev_schema.company_profile OWNER TO postgres;

--
-- TOC entry 220 (class 1259 OID 23762)
-- Name: companyprofile_id_seq; Type: SEQUENCE; Schema: acadrev_schema; Owner: postgres
--

CREATE SEQUENCE acadrev_schema.companyprofile_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE acadrev_schema.companyprofile_id_seq OWNER TO postgres;

--
-- TOC entry 4901 (class 0 OID 0)
-- Dependencies: 220
-- Name: companyprofile_id_seq; Type: SEQUENCE OWNED BY; Schema: acadrev_schema; Owner: postgres
--

ALTER SEQUENCE acadrev_schema.companyprofile_id_seq OWNED BY acadrev_schema.company_profile.id;


--
-- TOC entry 223 (class 1259 OID 23837)
-- Name: document; Type: TABLE; Schema: acadrev_schema; Owner: postgres
--

CREATE TABLE acadrev_schema.document (
    id integer NOT NULL,
    company_id integer NOT NULL,
    file_name character varying(255) NOT NULL,
    file_data bytea NOT NULL,
    file_type character varying(100),
    document_type character varying(100),
    uploaded_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE acadrev_schema.document OWNER TO postgres;

--
-- TOC entry 222 (class 1259 OID 23836)
-- Name: document_id_seq; Type: SEQUENCE; Schema: acadrev_schema; Owner: postgres
--

CREATE SEQUENCE acadrev_schema.document_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE acadrev_schema.document_id_seq OWNER TO postgres;

--
-- TOC entry 4902 (class 0 OID 0)
-- Dependencies: 222
-- Name: document_id_seq; Type: SEQUENCE OWNED BY; Schema: acadrev_schema; Owner: postgres
--

ALTER SEQUENCE acadrev_schema.document_id_seq OWNED BY acadrev_schema.document.id;


--
-- TOC entry 217 (class 1259 OID 23738)
-- Name: role; Type: TABLE; Schema: acadrev_schema; Owner: postgres
--

CREATE TABLE acadrev_schema.role (
    id integer NOT NULL,
    role_type acadrev_schema.user_role NOT NULL
);


ALTER TABLE acadrev_schema.role OWNER TO postgres;

--
-- TOC entry 216 (class 1259 OID 23737)
-- Name: role_id_seq; Type: SEQUENCE; Schema: acadrev_schema; Owner: postgres
--

CREATE SEQUENCE acadrev_schema.role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE acadrev_schema.role_id_seq OWNER TO postgres;

--
-- TOC entry 4903 (class 0 OID 0)
-- Dependencies: 216
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: acadrev_schema; Owner: postgres
--

ALTER SEQUENCE acadrev_schema.role_id_seq OWNED BY acadrev_schema.role.id;


--
-- TOC entry 219 (class 1259 OID 23747)
-- Name: users; Type: TABLE; Schema: acadrev_schema; Owner: postgres
--

CREATE TABLE acadrev_schema.users (
    id integer NOT NULL,
    username character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    role_id integer NOT NULL,
    certification character varying(255),
    experience_level character varying(255),
    name character varying(255) DEFAULT 'Unknown'::character varying NOT NULL
);


ALTER TABLE acadrev_schema.users OWNER TO postgres;

--
-- TOC entry 218 (class 1259 OID 23746)
-- Name: users_id_seq; Type: SEQUENCE; Schema: acadrev_schema; Owner: postgres
--

CREATE SEQUENCE acadrev_schema.users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE acadrev_schema.users_id_seq OWNER TO postgres;

--
-- TOC entry 4904 (class 0 OID 0)
-- Dependencies: 218
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: acadrev_schema; Owner: postgres
--

ALTER SEQUENCE acadrev_schema.users_id_seq OWNED BY acadrev_schema.users.id;


--
-- TOC entry 4718 (class 2604 OID 23855)
-- Name: audit_review id; Type: DEFAULT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.audit_review ALTER COLUMN id SET DEFAULT nextval('acadrev_schema.audit_review_id_seq'::regclass);


--
-- TOC entry 4715 (class 2604 OID 23766)
-- Name: company_profile id; Type: DEFAULT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.company_profile ALTER COLUMN id SET DEFAULT nextval('acadrev_schema.companyprofile_id_seq'::regclass);


--
-- TOC entry 4716 (class 2604 OID 23840)
-- Name: document id; Type: DEFAULT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.document ALTER COLUMN id SET DEFAULT nextval('acadrev_schema.document_id_seq'::regclass);


--
-- TOC entry 4712 (class 2604 OID 23741)
-- Name: role id; Type: DEFAULT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.role ALTER COLUMN id SET DEFAULT nextval('acadrev_schema.role_id_seq'::regclass);


--
-- TOC entry 4713 (class 2604 OID 23750)
-- Name: users id; Type: DEFAULT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.users ALTER COLUMN id SET DEFAULT nextval('acadrev_schema.users_id_seq'::regclass);


--
-- TOC entry 4894 (class 0 OID 23852)
-- Dependencies: 225
-- Data for Name: audit_review; Type: TABLE DATA; Schema: acadrev_schema; Owner: postgres
--

COPY acadrev_schema.audit_review (id, document_id, auditor_id, rating, comments, reviewed_at) FROM stdin;
\.


--
-- TOC entry 4890 (class 0 OID 23763)
-- Dependencies: 221
-- Data for Name: company_profile; Type: TABLE DATA; Schema: acadrev_schema; Owner: postgres
--

COPY acadrev_schema.company_profile (id, user_id, name, address, industry, phone) FROM stdin;
2	3	GSG	Ramallah	technology	059787784
\.


--
-- TOC entry 4892 (class 0 OID 23837)
-- Dependencies: 223
-- Data for Name: document; Type: TABLE DATA; Schema: acadrev_schema; Owner: postgres
--

COPY acadrev_schema.document (id, company_id, file_name, file_data, file_type, document_type, uploaded_at) FROM stdin;
\.


--
-- TOC entry 4886 (class 0 OID 23738)
-- Dependencies: 217
-- Data for Name: role; Type: TABLE DATA; Schema: acadrev_schema; Owner: postgres
--

COPY acadrev_schema.role (id, role_type) FROM stdin;
1	ADMIN
3	AUDITOR
2	COMPANY_OWNER
\.


--
-- TOC entry 4888 (class 0 OID 23747)
-- Dependencies: 219
-- Data for Name: users; Type: TABLE DATA; Schema: acadrev_schema; Owner: postgres
--

COPY acadrev_schema.users (id, username, password, role_id, certification, experience_level, name) FROM stdin;
1	abukhader@gmail.com	$2a$10$SdnB8mt5v.owDlxMGonkcu/BnqlglmXA33Mf0Gd75/TnrnltFVWsW	1	\N	\N	Ahmad Abu Khader
2	company_tech_solutions	$2a$10$Tuxle8WGu/0cEElIHaKafuRWgSuDpBdR./ZWuEKwyBXvMzRzZ37SS	2	\N	\N	Tech Solutions Ltd
3	company_innovatech	$2a$10$PIv8xdLoMLASUi3Iqz1LdeiWQ5lyjkJfuTvKuID4E2uSHPZU4/hC.	2	\N	\N	Innovatech Inc
4	company_nextgen	$2a$10$Gc.eHwbc4t2/kInXHTjW/OXYCvj8It8Ao8l70Cmiv7HNxMneEIcd2	2	\N	\N	NextGen Corp
5	company_alphasoft	$2a$10$wenN.I.1OuCV1yFf53rlvODbaf3AxYPW.h9jSpMGec9.nPda3b.Bq	2	\N	\N	AlphaSoft LLC
6	company_globaltech	$2a$10$k2nmx0Y30J03vYhcI8Bzu.K.4in2Y3zbvOWG0/leGKvoP2CcsCPPy	2	\N	\N	GlobalTech Solutions
7	auditor_john_doe	$2a$10$mghOvzeoINpFlZsa2Tbdp.g092YznSGBdgwigz5fgzhecSzVeEWR2	3	\N	\N	John Doe
8	auditor_jane_smith	$2a$10$xRu7ZkgTnrbS7BNhuIGl0.B4cAkgSiV99.zcWmahr2Eq/cxN7bWgm	3	\N	\N	Jane Smith
9	auditor_robert_brown	$2a$10$944sLC.xeeviAfp3wYxZCuJTWJY5F1r5hlJBYrGa45lkVNzQFvvXC	3	\N	\N	Robert Brown
10	auditor_emily_davis	$2a$10$jBMtvBF6qeXfUj4iRmGPq.ZJgZhD7Lx5Quy9SLQddL4ZkTF33hEt6	3	\N	\N	Emily Davis
11	auditor_michael_wilson	$2a$10$Mkr7OqpH50qthwOnu3fakOrMRejDhgfJk29G/6WyMaFEB6IQICVC2	3	\N	\N	Michael Wilson
12	admin_alex_johnson	$2a$10$b0/yxh/pU7fGWvSKKZHmBe5U7eqL07dNjfrQT5.jgjXdVDOM0.70.	1	\N	\N	Alex Johnson
13	admin_lisa_martin	$2a$10$kWjEodPMaz63UkjeUJpH3.G6bOuzlYHjb3TlLtPOc.gWlJRB/.Nwm	1	\N	\N	Lisa Martin
14	admin_david_clark	$2a$10$RpE.EJO7wqSDhxgZhCYWnuMgbDYN1Ln9V11BbKO2zKr4LSBtCS21.	1	\N	\N	David Clark
15	admin_sophia_walker	$2a$10$4gqVUm2xytal2EfaVVk0pu46IfU3O8Yqn/cgWf8OtUvEJIQjk8lAq	1	\N	\N	Sophia Walker
16	admin_james_lewis	$2a$10$p9LkBKDb78lHt8X7b2CIceIYe2GXSXAWrKwZmLPKigCw3XBrHUVf6	1	\N	\N	James Lewis
17	admin_ahmad	$2a$10$CwEVz71tVO75LG2YQ3/QLOzUDEW0ynS9jYqcMZwmSSOR7T8RJfIV2	1	\N	\N	ahmad
18	admin_ahmadd	$2a$10$qPEr.X1.C3MyUe2uHjro5O8cYuyfsah..TmwJjnFeDPxkNEyyqJ9u	1	\N	\N	ahmad
19	ahmad_thabata@gmail.com	$2a$10$t8SU/VPHr31oQEWZYyCUcuN2jeS15L/tZJCJ/cJ1ZyNITTI6Vgsyq	2	\N	\N	Ahmad
20	Sammer@gmail.com	$2a$10$OSRXHXFdhs5hj0ZVkhMSI.ouzo18lVO1tJn/kCiAizKfEupTofvk.	3	\N	\N	Sammer
\.


--
-- TOC entry 4905 (class 0 OID 0)
-- Dependencies: 224
-- Name: audit_review_id_seq; Type: SEQUENCE SET; Schema: acadrev_schema; Owner: postgres
--

SELECT pg_catalog.setval('acadrev_schema.audit_review_id_seq', 1, true);


--
-- TOC entry 4906 (class 0 OID 0)
-- Dependencies: 220
-- Name: companyprofile_id_seq; Type: SEQUENCE SET; Schema: acadrev_schema; Owner: postgres
--

SELECT pg_catalog.setval('acadrev_schema.companyprofile_id_seq', 2, true);


--
-- TOC entry 4907 (class 0 OID 0)
-- Dependencies: 222
-- Name: document_id_seq; Type: SEQUENCE SET; Schema: acadrev_schema; Owner: postgres
--

SELECT pg_catalog.setval('acadrev_schema.document_id_seq', 2, true);


--
-- TOC entry 4908 (class 0 OID 0)
-- Dependencies: 216
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: acadrev_schema; Owner: postgres
--

SELECT pg_catalog.setval('acadrev_schema.role_id_seq', 3, true);


--
-- TOC entry 4909 (class 0 OID 0)
-- Dependencies: 218
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: acadrev_schema; Owner: postgres
--

SELECT pg_catalog.setval('acadrev_schema.users_id_seq', 20, true);


--
-- TOC entry 4736 (class 2606 OID 23861)
-- Name: audit_review audit_review_pkey; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.audit_review
    ADD CONSTRAINT audit_review_pkey PRIMARY KEY (id);


--
-- TOC entry 4730 (class 2606 OID 23770)
-- Name: company_profile companyprofile_pkey; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.company_profile
    ADD CONSTRAINT companyprofile_pkey PRIMARY KEY (id);


--
-- TOC entry 4732 (class 2606 OID 23772)
-- Name: company_profile companyprofile_userid_key; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.company_profile
    ADD CONSTRAINT companyprofile_userid_key UNIQUE (user_id);


--
-- TOC entry 4734 (class 2606 OID 23845)
-- Name: document document_pkey; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.document
    ADD CONSTRAINT document_pkey PRIMARY KEY (id);


--
-- TOC entry 4722 (class 2606 OID 23743)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 4724 (class 2606 OID 23745)
-- Name: role role_roletype_key; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.role
    ADD CONSTRAINT role_roletype_key UNIQUE (role_type);


--
-- TOC entry 4726 (class 2606 OID 23754)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4728 (class 2606 OID 23756)
-- Name: users users_username_key; Type: CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.users
    ADD CONSTRAINT users_username_key UNIQUE (username);


--
-- TOC entry 4740 (class 2606 OID 23867)
-- Name: audit_review audit_review_auditor_id_fkey; Type: FK CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.audit_review
    ADD CONSTRAINT audit_review_auditor_id_fkey FOREIGN KEY (auditor_id) REFERENCES acadrev_schema.users(id) ON DELETE CASCADE;


--
-- TOC entry 4741 (class 2606 OID 23862)
-- Name: audit_review audit_review_document_id_fkey; Type: FK CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.audit_review
    ADD CONSTRAINT audit_review_document_id_fkey FOREIGN KEY (document_id) REFERENCES acadrev_schema.document(id) ON DELETE CASCADE;


--
-- TOC entry 4738 (class 2606 OID 23773)
-- Name: company_profile companyprofile_userid_fkey; Type: FK CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.company_profile
    ADD CONSTRAINT companyprofile_userid_fkey FOREIGN KEY (user_id) REFERENCES acadrev_schema.users(id) ON DELETE CASCADE;


--
-- TOC entry 4739 (class 2606 OID 23846)
-- Name: document document_company_id_fkey; Type: FK CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.document
    ADD CONSTRAINT document_company_id_fkey FOREIGN KEY (company_id) REFERENCES acadrev_schema.company_profile(id) ON DELETE CASCADE;


--
-- TOC entry 4737 (class 2606 OID 23757)
-- Name: users users_roleid_fkey; Type: FK CONSTRAINT; Schema: acadrev_schema; Owner: postgres
--

ALTER TABLE ONLY acadrev_schema.users
    ADD CONSTRAINT users_roleid_fkey FOREIGN KEY (role_id) REFERENCES acadrev_schema.role(id) ON DELETE RESTRICT;


-- Completed on 2025-10-24 22:17:34

--
-- PostgreSQL database dump complete
--

\unrestrict ui5xdV4JuibdiRSz0FATTNCJYVIdlb26oYPeNN3eXqFCcY6TxvKGBfcvtddXb4O

