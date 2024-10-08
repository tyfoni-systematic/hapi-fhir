create sequence SEQ_BLKEXCOL_PID start 1 increment 50;
create sequence SEQ_BLKEXCOLFILE_PID start 1 increment 50;
create sequence SEQ_BLKEXJOB_PID start 1 increment 50;
create sequence SEQ_CNCPT_MAP_GRP_ELM_TGT_PID start 1 increment 50;
create sequence SEQ_CODESYSTEM_PID start 1 increment 50;
create sequence SEQ_CODESYSTEMVER_PID start 1 increment 50;
create sequence SEQ_CONCEPT_DESIG_PID start 1 increment 50;
create sequence SEQ_CONCEPT_MAP_GROUP_PID start 1 increment 50;
create sequence SEQ_CONCEPT_MAP_GRP_ELM_PID start 1 increment 50;
create sequence SEQ_CONCEPT_MAP_PID start 1 increment 50;
create sequence SEQ_CONCEPT_PC_PID start 1 increment 50;
create sequence SEQ_CONCEPT_PID start 1 increment 50;
create sequence SEQ_CONCEPT_PROP_PID start 1 increment 50;
create sequence SEQ_EMPI_LINK_ID start 1 increment 50;
create sequence SEQ_FORCEDID_ID start 1 increment 50;
create sequence SEQ_HISTORYTAG_ID start 1 increment 50;
create sequence SEQ_IDXCMPSTRUNIQ_ID start 1 increment 50;
create sequence SEQ_NPM_PACK start 1 increment 50;
create sequence SEQ_NPM_PACKVER start 1 increment 50;
create sequence SEQ_NPM_PACKVERRES start 1 increment 50;
create sequence SEQ_RES_REINDEX_JOB start 1 increment 50;
create sequence SEQ_RESLINK_ID start 1 increment 50;
create sequence SEQ_RESOURCE_HISTORY_ID start 1 increment 50;
create sequence SEQ_RESOURCE_ID start 1 increment 50;
create sequence SEQ_RESPARMPRESENT_ID start 1 increment 50;
create sequence SEQ_RESTAG_ID start 1 increment 50;
create sequence SEQ_SEARCH start 1 increment 50;
create sequence SEQ_SEARCH_INC start 1 increment 50;
create sequence SEQ_SEARCH_RES start 1 increment 50;
create sequence SEQ_SPIDX_COORDS start 1 increment 50;
create sequence SEQ_SPIDX_DATE start 1 increment 50;
create sequence SEQ_SPIDX_NUMBER start 1 increment 50;
create sequence SEQ_SPIDX_QUANTITY start 1 increment 50;
create sequence SEQ_SPIDX_STRING start 1 increment 50;
create sequence SEQ_SPIDX_TOKEN start 1 increment 50;
create sequence SEQ_SPIDX_URI start 1 increment 50;
create sequence SEQ_SUBSCRIPTION_ID start 1 increment 50;
create sequence SEQ_TAGDEF_ID start 1 increment 50;
create sequence SEQ_VALUESET_C_DSGNTN_PID start 1 increment 50;
create sequence SEQ_VALUESET_CONCEPT_PID start 1 increment 50;
create sequence SEQ_VALUESET_PID start 1 increment 50;

    CREATE TABLE "FLY_HFJ_MIGRATION" (
        "installed_rank" integer NOT NULL,
        "version" character varying(50),
        "description" character varying(200) NOT NULL,
        "type" character varying(20) NOT NULL,
        "script" character varying(1000) NOT NULL,
        "checksum" integer,
        "installed_by" character varying(100) NOT NULL,
        "installed_on" timestamp without time zone DEFAULT now() NOT NULL,
        "execution_time" integer NOT NULL,
        "success" boolean NOT NULL
    );

    create table HFJ_BINARY_STORAGE_BLOB (
       BLOB_ID varchar(200) not null,
        BLOB_DATA oid not null,
        CONTENT_TYPE varchar(100) not null,
        BLOB_HASH varchar(128),
        PUBLISHED_DATE timestamp not null,
        RESOURCE_ID varchar(100) not null,
        BLOB_SIZE int4,
        primary key (BLOB_ID)
    );

    create table HFJ_BLK_EXPORT_COLFILE (
       PID int8 not null,
        RES_ID varchar(100) not null,
        COLLECTION_PID int8 not null,
        primary key (PID)
    );

    create table HFJ_BLK_EXPORT_COLLECTION (
       PID int8 not null,
        TYPE_FILTER varchar(1000),
        RES_TYPE varchar(40) not null,
        OPTLOCK int4 not null,
        JOB_PID int8 not null,
        primary key (PID)
    );

    create table HFJ_BLK_EXPORT_JOB (
       PID int8 not null,
        CREATED_TIME timestamp not null,
        EXP_TIME timestamp not null,
        JOB_ID varchar(36) not null,
        REQUEST varchar(500) not null,
        EXP_SINCE timestamp,
        JOB_STATUS varchar(10) not null,
        STATUS_MESSAGE varchar(500),
        STATUS_TIME timestamp not null,
        OPTLOCK int4 not null,
        primary key (PID)
    );

    create table HFJ_FORCED_ID (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        FORCED_ID varchar(100) not null,
        RESOURCE_PID int8 not null,
        RESOURCE_TYPE varchar(100) default '',
        primary key (PID)
    );

    create table HFJ_HISTORY_TAG (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        TAG_ID int8,
        RES_VER_PID int8 not null,
        RES_ID int8 not null,
        RES_TYPE varchar(40) not null,
        primary key (PID)
    );

    create table HFJ_IDX_CMP_STRING_UNIQ (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        IDX_STRING varchar(200) not null,
        RES_ID int8,
        primary key (PID)
    );

    create table HFJ_PARTITION (
       PART_ID int4 not null,
        PART_DESC varchar(200),
        PART_NAME varchar(200) not null,
        primary key (PART_ID)
    );

    create table HFJ_RES_LINK (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SRC_PATH varchar(200) not null,
        SRC_RESOURCE_ID int8 not null,
        SOURCE_RESOURCE_TYPE varchar(40) not null,
        TARGET_RESOURCE_ID int8,
        TARGET_RESOURCE_TYPE varchar(40) not null,
        TARGET_RESOURCE_URL varchar(200),
        SP_UPDATED timestamp,
        primary key (PID)
    );

    create table HFJ_RES_PARAM_PRESENT (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        HASH_PRESENCE int8,
        SP_PRESENT boolean not null,
        RES_ID int8 not null,
        primary key (PID)
    );

    create table HFJ_RES_REINDEX_JOB (
       PID int8 not null,
        JOB_DELETED boolean not null,
        REINDEX_COUNT int4,
        RES_TYPE varchar(100),
        SUSPENDED_UNTIL timestamp,
        UPDATE_THRESHOLD_HIGH timestamp not null,
        UPDATE_THRESHOLD_LOW timestamp,
        primary key (PID)
    );

    create table HFJ_RES_TAG (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        TAG_ID int8,
        RES_ID int8,
        RES_TYPE varchar(40) not null,
        primary key (PID)
    );

    create table HFJ_RES_VER (
       PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        RES_DELETED_AT timestamp,
        RES_VERSION varchar(7),
        HAS_TAGS boolean not null,
        RES_PUBLISHED timestamp not null,
        RES_UPDATED timestamp not null,
        RES_ENCODING varchar(5) not null,
        RES_TEXT oid,
        RES_ID int8 not null,
        RES_TYPE varchar(40) not null,
        RES_VER int8 not null,
        primary key (PID)
    );

    create table HFJ_RES_VER_PROV (
       RES_VER_PID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        REQUEST_ID varchar(16),
        SOURCE_URI varchar(100),
        RES_PID int8 not null,
        primary key (RES_VER_PID)
    );

    create table HFJ_RESOURCE (
       RES_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        RES_DELETED_AT timestamp,
        RES_VERSION varchar(7),
        HAS_TAGS boolean not null,
        RES_PUBLISHED timestamp not null,
        RES_UPDATED timestamp not null,
        SP_HAS_LINKS boolean,
        HASH_SHA256 varchar(64),
        SP_INDEX_STATUS int8,
        RES_LANGUAGE varchar(20),
        SP_CMPSTR_UNIQ_PRESENT boolean,
        SP_COORDS_PRESENT boolean,
        SP_DATE_PRESENT boolean,
        SP_NUMBER_PRESENT boolean,
        SP_QUANTITY_PRESENT boolean,
        SP_STRING_PRESENT boolean,
        SP_TOKEN_PRESENT boolean,
        SP_URI_PRESENT boolean,
        RES_TYPE varchar(40) not null,
        RES_VER int8,
        primary key (RES_ID)
    );

    create table HFJ_SEARCH (
       PID int8 not null,
        CREATED timestamp not null,
        SEARCH_DELETED boolean,
        EXPIRY_OR_NULL timestamp,
        FAILURE_CODE int4,
        FAILURE_MESSAGE varchar(500),
        LAST_UPDATED_HIGH timestamp,
        LAST_UPDATED_LOW timestamp,
        NUM_BLOCKED int4,
        NUM_FOUND int4 not null,
        PREFERRED_PAGE_SIZE int4,
        RESOURCE_ID int8,
        RESOURCE_TYPE varchar(200),
        SEARCH_PARAM_MAP oid,
        SEARCH_QUERY_STRING text,
        SEARCH_QUERY_STRING_HASH int4,
        SEARCH_TYPE int4 not null,
        SEARCH_STATUS varchar(10) not null,
        TOTAL_COUNT int4,
        SEARCH_UUID varchar(36) not null,
        OPTLOCK_VERSION int4,
        primary key (PID)
    );

    create table HFJ_SEARCH_INCLUDE (
       PID int8 not null,
        SEARCH_INCLUDE varchar(200) not null,
        INC_RECURSE boolean not null,
        REVINCLUDE boolean not null,
        SEARCH_PID int8 not null,
        primary key (PID)
    );

    create table HFJ_SEARCH_RESULT (
       PID int8 not null,
        SEARCH_ORDER int4 not null,
        RESOURCE_PID int8 not null,
        SEARCH_PID int8 not null,
        primary key (PID)
    );

    create table HFJ_SPIDX_COORDS (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_IDENTITY int8,
        SP_LATITUDE float8,
        SP_LONGITUDE float8,
        primary key (SP_ID)
    );

    create table HFJ_SPIDX_DATE (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_IDENTITY int8,
        SP_VALUE_HIGH timestamp,
        SP_VALUE_HIGH_DATE_ORDINAL int4,
        SP_VALUE_LOW timestamp,
        SP_VALUE_LOW_DATE_ORDINAL int4,
        primary key (SP_ID)
    );

    create table HFJ_SPIDX_NUMBER (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_IDENTITY int8,
        SP_VALUE numeric(19, 2),
        primary key (SP_ID)
    );

    create table HFJ_SPIDX_QUANTITY (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_IDENTITY int8,
        HASH_IDENTITY_AND_UNITS int8,
        HASH_IDENTITY_SYS_UNITS int8,
        SP_SYSTEM varchar(200),
        SP_UNITS varchar(200),
        SP_VALUE numeric(19, 2),
        primary key (SP_ID)
    );

    create table HFJ_SPIDX_STRING (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_EXACT int8,
        HASH_IDENTITY int8,
        HASH_NORM_PREFIX int8,
        SP_VALUE_EXACT varchar(200),
        SP_VALUE_NORMALIZED varchar(200),
        primary key (SP_ID)
    );

    create table HFJ_SPIDX_TOKEN (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_IDENTITY int8,
        HASH_SYS int8,
        HASH_SYS_AND_VALUE int8,
        HASH_VALUE int8,
        SP_SYSTEM varchar(200),
        SP_VALUE varchar(200),
        primary key (SP_ID)
    );

    create table HFJ_SPIDX_URI (
       SP_ID int8 not null,
        PARTITION_DATE date,
        PARTITION_ID int4,
        SP_MISSING boolean not null,
        SP_NAME varchar(100) not null,
        RES_ID int8 not null,
        RES_TYPE varchar(100) not null,
        SP_UPDATED timestamp,
        HASH_IDENTITY int8,
        HASH_URI int8,
        SP_URI varchar(254),
        primary key (SP_ID)
    );

    create table HFJ_SUBSCRIPTION_STATS (
       PID int8 not null,
        CREATED_TIME timestamp not null,
        RES_ID int8,
        primary key (PID)
    );

    create table HFJ_TAG_DEF (
       TAG_ID int8 not null,
        TAG_CODE varchar(200),
        TAG_DISPLAY varchar(200),
        TAG_SYSTEM varchar(200),
        TAG_TYPE int4 not null,
        primary key (TAG_ID)
    );

    create table MPI_LINK (
       PID int8 not null,
        CREATED timestamp not null,
        EID_MATCH boolean,
        TARGET_TYPE varchar(40),
        LINK_SOURCE int4 not null,
        MATCH_RESULT int4 not null,
        NEW_PERSON boolean,
        PERSON_PID int8 not null,
        SCORE float8,
        TARGET_PID int8 not null,
        UPDATED timestamp not null,
        VECTOR int8,
        VERSION varchar(16) not null,
        primary key (PID)
    );

    create table NPM_PACKAGE (
       PID int8 not null,
        CUR_VERSION_ID varchar(200),
        PACKAGE_DESC varchar(200),
        PACKAGE_ID varchar(200) not null,
        UPDATED_TIME timestamp not null,
        primary key (PID)
    );

    create table NPM_PACKAGE_VER (
       PID int8 not null,
        CURRENT_VERSION boolean not null,
        PKG_DESC varchar(200),
        DESC_UPPER varchar(200),
        FHIR_VERSION varchar(10) not null,
        FHIR_VERSION_ID varchar(10) not null,
        PACKAGE_ID varchar(200) not null,
        PACKAGE_SIZE_BYTES int8 not null,
        SAVED_TIME timestamp not null,
        UPDATED_TIME timestamp not null,
        VERSION_ID varchar(200) not null,
        PACKAGE_PID int8 not null,
        BINARY_RES_ID int8 not null,
        primary key (PID)
    );

    create table NPM_PACKAGE_VER_RES (
       PID int8 not null,
        CANONICAL_URL varchar(200),
        CANONICAL_VERSION varchar(200),
        FILE_DIR varchar(200),
        FHIR_VERSION varchar(10) not null,
        FHIR_VERSION_ID varchar(10) not null,
        FILE_NAME varchar(200),
        RES_SIZE_BYTES int8 not null,
        RES_TYPE varchar(40) not null,
        UPDATED_TIME timestamp not null,
        PACKVER_PID int8 not null,
        BINARY_RES_ID int8 not null,
        primary key (PID)
    );

    create table TRM_CODESYSTEM (
       PID int8 not null,
        CODE_SYSTEM_URI varchar(200) not null,
        CURRENT_VERSION_PID int8,
        CS_NAME varchar(200),
        RES_ID int8,
        primary key (PID)
    );

    create table TRM_CODESYSTEM_VER (
       PID int8 not null,
        CS_DISPLAY varchar(200),
        CODESYSTEM_PID int8,
        CS_VERSION_ID varchar(200),
        RES_ID int8 not null,
        primary key (PID)
    );

    create table TRM_CONCEPT (
       PID int8 not null,
        CODEVAL varchar(500) not null,
        CODESYSTEM_PID int8,
        DISPLAY varchar(400),
        INDEX_STATUS int8,
        PARENT_PIDS text,
        CODE_SEQUENCE int4,
        CONCEPT_UPDATED timestamp,
        primary key (PID)
    );

    create table TRM_CONCEPT_DESIG (
       PID int8 not null,
        LANG varchar(500),
        USE_CODE varchar(500),
        USE_DISPLAY varchar(500),
        USE_SYSTEM varchar(500),
        VAL varchar(2000) not null,
        CS_VER_PID int8,
        CONCEPT_PID int8,
        primary key (PID)
    );

    create table TRM_CONCEPT_MAP (
       PID int8 not null,
        RES_ID int8,
        SOURCE_URL varchar(200),
        TARGET_URL varchar(200),
        URL varchar(200) not null,
        primary key (PID)
    );

    create table TRM_CONCEPT_MAP_GROUP (
       PID int8 not null,
        CONCEPT_MAP_URL varchar(200),
        SOURCE_URL varchar(200) not null,
        SOURCE_VS varchar(200),
        SOURCE_VERSION varchar(200),
        TARGET_URL varchar(200) not null,
        TARGET_VS varchar(200),
        TARGET_VERSION varchar(200),
        CONCEPT_MAP_PID int8 not null,
        primary key (PID)
    );

    create table TRM_CONCEPT_MAP_GRP_ELEMENT (
       PID int8 not null,
        SOURCE_CODE varchar(500) not null,
        CONCEPT_MAP_URL varchar(200),
        SOURCE_DISPLAY varchar(400),
        SYSTEM_URL varchar(200),
        SYSTEM_VERSION varchar(200),
        VALUESET_URL varchar(200),
        CONCEPT_MAP_GROUP_PID int8 not null,
        primary key (PID)
    );

    create table TRM_CONCEPT_MAP_GRP_ELM_TGT (
       PID int8 not null,
        TARGET_CODE varchar(500) not null,
        CONCEPT_MAP_URL varchar(200),
        TARGET_DISPLAY varchar(400),
        TARGET_EQUIVALENCE varchar(50),
        SYSTEM_URL varchar(200),
        SYSTEM_VERSION varchar(200),
        VALUESET_URL varchar(200),
        CONCEPT_MAP_GRP_ELM_PID int8 not null,
        primary key (PID)
    );

    create table TRM_CONCEPT_PC_LINK (
       PID int8 not null,
        CHILD_PID int8,
        CODESYSTEM_PID int8 not null,
        PARENT_PID int8,
        REL_TYPE int4,
        primary key (PID)
    );

    create table TRM_CONCEPT_PROPERTY (
       PID int8 not null,
        PROP_CODESYSTEM varchar(500),
        PROP_DISPLAY varchar(500),
        PROP_KEY varchar(500) not null,
        PROP_TYPE int4 not null,
        PROP_VAL varchar(500),
        PROP_VAL_LOB oid,
        CS_VER_PID int8,
        CONCEPT_PID int8,
        primary key (PID)
    );

    create table TRM_VALUESET (
       PID int8 not null,
        EXPANSION_STATUS varchar(50) not null,
        VSNAME varchar(200),
        RES_ID int8,
        TOTAL_CONCEPT_DESIGNATIONS int8 default 0 not null,
        TOTAL_CONCEPTS int8 default 0 not null,
        URL varchar(200) not null,
        primary key (PID)
    );

    create table TRM_VALUESET_C_DESIGNATION (
       PID int8 not null,
        VALUESET_CONCEPT_PID int8 not null,
        LANG varchar(500),
        USE_CODE varchar(500),
        USE_DISPLAY varchar(500),
        USE_SYSTEM varchar(500),
        VAL varchar(2000) not null,
        VALUESET_PID int8 not null,
        primary key (PID)
    );

    create table TRM_VALUESET_CONCEPT (
       PID int8 not null,
        CODEVAL varchar(500) not null,
        DISPLAY varchar(400),
        VALUESET_ORDER int4 not null,
        SYSTEM_URL varchar(200) not null,
        VALUESET_PID int8 not null,
        primary key (PID)
    );
create index IDX_BLKEX_EXPTIME on HFJ_BLK_EXPORT_JOB (EXP_TIME);

    alter table if exists HFJ_BLK_EXPORT_JOB 
       add constraint IDX_BLKEX_JOB_ID unique (JOB_ID);

    alter table if exists HFJ_FORCED_ID 
       add constraint IDX_FORCEDID_RESID unique (RESOURCE_PID);

    alter table if exists HFJ_FORCED_ID 
       add constraint IDX_FORCEDID_TYPE_FID unique (RESOURCE_TYPE, FORCED_ID);

    alter table if exists HFJ_HISTORY_TAG 
       add constraint IDX_RESHISTTAG_TAGID unique (RES_VER_PID, TAG_ID);
create index IDX_IDXCMPSTRUNIQ_RESOURCE on HFJ_IDX_CMP_STRING_UNIQ (RES_ID);

    alter table if exists HFJ_IDX_CMP_STRING_UNIQ 
       add constraint IDX_IDXCMPSTRUNIQ_STRING unique (IDX_STRING);

    alter table if exists HFJ_PARTITION 
       add constraint IDX_PART_NAME unique (PART_NAME);
create index IDX_RL_TPATHRES on HFJ_RES_LINK (SRC_PATH, TARGET_RESOURCE_ID);
create index IDX_RL_SRC on HFJ_RES_LINK (SRC_RESOURCE_ID);
create index IDX_RL_DEST on HFJ_RES_LINK (TARGET_RESOURCE_ID);
create index IDX_RESPARMPRESENT_RESID on HFJ_RES_PARAM_PRESENT (RES_ID);
create index IDX_RESPARMPRESENT_HASHPRES on HFJ_RES_PARAM_PRESENT (HASH_PRESENCE);

    alter table if exists HFJ_RES_TAG 
       add constraint IDX_RESTAG_TAGID unique (RES_ID, TAG_ID);
create index IDX_RESVER_TYPE_DATE on HFJ_RES_VER (RES_TYPE, RES_UPDATED);
create index IDX_RESVER_ID_DATE on HFJ_RES_VER (RES_ID, RES_UPDATED);
create index IDX_RESVER_DATE on HFJ_RES_VER (RES_UPDATED);

    alter table if exists HFJ_RES_VER 
       add constraint IDX_RESVER_ID_VER unique (RES_ID, RES_VER);
create index IDX_RESVERPROV_SOURCEURI on HFJ_RES_VER_PROV (SOURCE_URI);
create index IDX_RESVERPROV_REQUESTID on HFJ_RES_VER_PROV (REQUEST_ID);
create index IDX_RES_DATE on HFJ_RESOURCE (RES_UPDATED);
create index IDX_RES_LANG on HFJ_RESOURCE (RES_TYPE, RES_LANGUAGE);
create index IDX_RES_TYPE on HFJ_RESOURCE (RES_TYPE);
create index IDX_INDEXSTATUS on HFJ_RESOURCE (SP_INDEX_STATUS);
create index IDX_SEARCH_RESTYPE_HASHS on HFJ_SEARCH (RESOURCE_TYPE, SEARCH_QUERY_STRING_HASH, CREATED);
create index IDX_SEARCH_CREATED on HFJ_SEARCH (CREATED);

    alter table if exists HFJ_SEARCH 
       add constraint IDX_SEARCH_UUID unique (SEARCH_UUID);

    alter table if exists HFJ_SEARCH_RESULT 
       add constraint IDX_SEARCHRES_ORDER unique (SEARCH_PID, SEARCH_ORDER);
create index IDX_SP_COORDS_HASH on HFJ_SPIDX_COORDS (HASH_IDENTITY, SP_LATITUDE, SP_LONGITUDE);
create index IDX_SP_COORDS_UPDATED on HFJ_SPIDX_COORDS (SP_UPDATED);
create index IDX_SP_COORDS_RESID on HFJ_SPIDX_COORDS (RES_ID);
create index IDX_SP_DATE_HASH on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW, SP_VALUE_HIGH);
create index IDX_SP_DATE_HASH_LOW on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW);
create index IDX_SP_DATE_ORD_HASH on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL, SP_VALUE_HIGH_DATE_ORDINAL);
create index IDX_SP_DATE_ORD_HASH_LOW on HFJ_SPIDX_DATE (HASH_IDENTITY, SP_VALUE_LOW_DATE_ORDINAL);
create index IDX_SP_DATE_RESID on HFJ_SPIDX_DATE (RES_ID);
create index IDX_SP_DATE_UPDATED on HFJ_SPIDX_DATE (SP_UPDATED);
create index IDX_SP_NUMBER_HASH_VAL on HFJ_SPIDX_NUMBER (HASH_IDENTITY, SP_VALUE);
create index IDX_SP_NUMBER_UPDATED on HFJ_SPIDX_NUMBER (SP_UPDATED);
create index IDX_SP_NUMBER_RESID on HFJ_SPIDX_NUMBER (RES_ID);
create index IDX_SP_QUANTITY_HASH on HFJ_SPIDX_QUANTITY (HASH_IDENTITY, SP_VALUE);
create index IDX_SP_QUANTITY_HASH_UN on HFJ_SPIDX_QUANTITY (HASH_IDENTITY_AND_UNITS, SP_VALUE);
create index IDX_SP_QUANTITY_HASH_SYSUN on HFJ_SPIDX_QUANTITY (HASH_IDENTITY_SYS_UNITS, SP_VALUE);
create index IDX_SP_QUANTITY_UPDATED on HFJ_SPIDX_QUANTITY (SP_UPDATED);
create index IDX_SP_QUANTITY_RESID on HFJ_SPIDX_QUANTITY (RES_ID);
create index IDX_SP_STRING_HASH_IDENT on HFJ_SPIDX_STRING (HASH_IDENTITY);
create index IDX_SP_STRING_HASH_NRM on HFJ_SPIDX_STRING (HASH_NORM_PREFIX, SP_VALUE_NORMALIZED);
create index IDX_SP_STRING_HASH_EXCT on HFJ_SPIDX_STRING (HASH_EXACT);
create index IDX_SP_STRING_UPDATED on HFJ_SPIDX_STRING (SP_UPDATED);
create index IDX_SP_STRING_RESID on HFJ_SPIDX_STRING (RES_ID);
create index IDX_SP_TOKEN_HASH on HFJ_SPIDX_TOKEN (HASH_IDENTITY);
create index IDX_SP_TOKEN_HASH_S on HFJ_SPIDX_TOKEN (HASH_SYS);
create index IDX_SP_TOKEN_HASH_SV on HFJ_SPIDX_TOKEN (HASH_SYS_AND_VALUE);
create index IDX_SP_TOKEN_HASH_V on HFJ_SPIDX_TOKEN (HASH_VALUE);
create index IDX_SP_TOKEN_UPDATED on HFJ_SPIDX_TOKEN (SP_UPDATED);
create index IDX_SP_TOKEN_RESID on HFJ_SPIDX_TOKEN (RES_ID);
create index IDX_SP_URI on HFJ_SPIDX_URI (RES_TYPE, SP_NAME, SP_URI);
create index IDX_SP_URI_HASH_IDENTITY on HFJ_SPIDX_URI (HASH_IDENTITY, SP_URI);
create index IDX_SP_URI_HASH_URI on HFJ_SPIDX_URI (HASH_URI);
create index IDX_SP_URI_RESTYPE_NAME on HFJ_SPIDX_URI (RES_TYPE, SP_NAME);
create index IDX_SP_URI_UPDATED on HFJ_SPIDX_URI (SP_UPDATED);
create index IDX_SP_URI_COORDS on HFJ_SPIDX_URI (RES_ID);

    alter table if exists HFJ_SUBSCRIPTION_STATS 
       add constraint IDX_SUBSC_RESID unique (RES_ID);

    alter table if exists HFJ_TAG_DEF 
       add constraint IDX_TAGDEF_TYPESYSCODE unique (TAG_TYPE, TAG_SYSTEM, TAG_CODE);

    alter table if exists MPI_LINK 
       add constraint IDX_EMPI_PERSON_TGT unique (PERSON_PID, TARGET_PID);

    alter table if exists NPM_PACKAGE 
       add constraint IDX_PACK_ID unique (PACKAGE_ID);

    alter table if exists NPM_PACKAGE_VER 
       add constraint IDX_PACKVER unique (PACKAGE_ID, VERSION_ID);
create index IDX_PACKVERRES_URL on NPM_PACKAGE_VER_RES (CANONICAL_URL);

    alter table if exists TRM_CODESYSTEM 
       add constraint IDX_CS_CODESYSTEM unique (CODE_SYSTEM_URI);
create index IDX_CONCEPT_INDEXSTATUS on TRM_CONCEPT (INDEX_STATUS);
create index IDX_CONCEPT_UPDATED on TRM_CONCEPT (CONCEPT_UPDATED);

    alter table if exists TRM_CONCEPT 
       add constraint IDX_CONCEPT_CS_CODE unique (CODESYSTEM_PID, CODEVAL);

    alter table if exists TRM_CONCEPT_MAP 
       add constraint IDX_CONCEPT_MAP_URL unique (URL);
create index IDX_CNCPT_MAP_GRP_CD on TRM_CONCEPT_MAP_GRP_ELEMENT (SOURCE_CODE);
create index IDX_CNCPT_MP_GRP_ELM_TGT_CD on TRM_CONCEPT_MAP_GRP_ELM_TGT (TARGET_CODE);

    alter table if exists TRM_VALUESET 
       add constraint IDX_VALUESET_URL unique (URL);

    alter table if exists TRM_VALUESET_CONCEPT 
       add constraint IDX_VS_CONCEPT_CS_CD unique (VALUESET_PID, SYSTEM_URL, CODEVAL);

    alter table if exists TRM_VALUESET_CONCEPT 
       add constraint IDX_VS_CONCEPT_ORDER unique (VALUESET_PID, VALUESET_ORDER);

    alter table if exists HFJ_BLK_EXPORT_COLFILE 
       add constraint FK_BLKEXCOLFILE_COLLECT 
       foreign key (COLLECTION_PID) 
       references HFJ_BLK_EXPORT_COLLECTION;

    alter table if exists HFJ_BLK_EXPORT_COLLECTION 
       add constraint FK_BLKEXCOL_JOB 
       foreign key (JOB_PID) 
       references HFJ_BLK_EXPORT_JOB;

    alter table if exists HFJ_FORCED_ID 
       add constraint FK_FORCEDID_RESOURCE 
       foreign key (RESOURCE_PID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_HISTORY_TAG 
       add constraint FKtderym7awj6q8iq5c51xv4ndw 
       foreign key (TAG_ID) 
       references HFJ_TAG_DEF;

    alter table if exists HFJ_HISTORY_TAG 
       add constraint FK_HISTORYTAG_HISTORY 
       foreign key (RES_VER_PID) 
       references HFJ_RES_VER;

    alter table if exists HFJ_IDX_CMP_STRING_UNIQ 
       add constraint FK_IDXCMPSTRUNIQ_RES_ID 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_LINK 
       add constraint FK_RESLINK_SOURCE 
       foreign key (SRC_RESOURCE_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_LINK 
       add constraint FK_RESLINK_TARGET 
       foreign key (TARGET_RESOURCE_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_PARAM_PRESENT 
       add constraint FK_RESPARMPRES_RESID 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_TAG 
       add constraint FKbfcjbaftmiwr3rxkwsy23vneo 
       foreign key (TAG_ID) 
       references HFJ_TAG_DEF;

    alter table if exists HFJ_RES_TAG 
       add constraint FK_RESTAG_RESOURCE 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_VER 
       add constraint FK_RESOURCE_HISTORY_RESOURCE 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_VER_PROV 
       add constraint FK_RESVERPROV_RES_PID 
       foreign key (RES_PID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_RES_VER_PROV 
       add constraint FK_RESVERPROV_RESVER_PID 
       foreign key (RES_VER_PID) 
       references HFJ_RES_VER;

    alter table if exists HFJ_SEARCH_INCLUDE 
       add constraint FK_SEARCHINC_SEARCH 
       foreign key (SEARCH_PID) 
       references HFJ_SEARCH;

    alter table if exists HFJ_SPIDX_COORDS 
       add constraint FKc97mpk37okwu8qvtceg2nh9vn 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SPIDX_DATE 
       add constraint FK17s70oa59rm9n61k9thjqrsqm 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SPIDX_NUMBER 
       add constraint FKcltihnc5tgprj9bhpt7xi5otb 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SPIDX_QUANTITY 
       add constraint FKn603wjjoi1a6asewxbbd78bi5 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SPIDX_STRING 
       add constraint FK_SPIDXSTR_RESOURCE 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SPIDX_TOKEN 
       add constraint FK7ulx3j1gg3v7maqrejgc7ybc4 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SPIDX_URI 
       add constraint FKgxsreutymmfjuwdswv3y887do 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists HFJ_SUBSCRIPTION_STATS 
       add constraint FK_SUBSC_RESOURCE_ID 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists MPI_LINK 
       add constraint FK_EMPI_LINK_PERSON 
       foreign key (PERSON_PID) 
       references HFJ_RESOURCE;

    alter table if exists MPI_LINK 
       add constraint FK_EMPI_LINK_TARGET 
       foreign key (TARGET_PID) 
       references HFJ_RESOURCE;

    alter table if exists NPM_PACKAGE_VER 
       add constraint FK_NPM_PKV_PKG 
       foreign key (PACKAGE_PID) 
       references NPM_PACKAGE;

    alter table if exists NPM_PACKAGE_VER 
       add constraint FK_NPM_PKV_RESID 
       foreign key (BINARY_RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists NPM_PACKAGE_VER_RES 
       add constraint FK_NPM_PACKVERRES_PACKVER 
       foreign key (PACKVER_PID) 
       references NPM_PACKAGE_VER;

    alter table if exists NPM_PACKAGE_VER_RES 
       add constraint FK_NPM_PKVR_RESID 
       foreign key (BINARY_RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists TRM_CODESYSTEM 
       add constraint FK_TRMCODESYSTEM_CURVER 
       foreign key (CURRENT_VERSION_PID) 
       references TRM_CODESYSTEM_VER;

    alter table if exists TRM_CODESYSTEM 
       add constraint FK_TRMCODESYSTEM_RES 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists TRM_CODESYSTEM_VER 
       add constraint FK_CODESYSVER_CS_ID 
       foreign key (CODESYSTEM_PID) 
       references TRM_CODESYSTEM;

    alter table if exists TRM_CODESYSTEM_VER 
       add constraint FK_CODESYSVER_RES_ID 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists TRM_CONCEPT 
       add constraint FK_CONCEPT_PID_CS_PID 
       foreign key (CODESYSTEM_PID) 
       references TRM_CODESYSTEM_VER;

    alter table if exists TRM_CONCEPT_DESIG 
       add constraint FK_CONCEPTDESIG_CSV 
       foreign key (CS_VER_PID) 
       references TRM_CODESYSTEM_VER;

    alter table if exists TRM_CONCEPT_DESIG 
       add constraint FK_CONCEPTDESIG_CONCEPT 
       foreign key (CONCEPT_PID) 
       references TRM_CONCEPT;

    alter table if exists TRM_CONCEPT_MAP 
       add constraint FK_TRMCONCEPTMAP_RES 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists TRM_CONCEPT_MAP_GROUP 
       add constraint FK_TCMGROUP_CONCEPTMAP 
       foreign key (CONCEPT_MAP_PID) 
       references TRM_CONCEPT_MAP;

    alter table if exists TRM_CONCEPT_MAP_GRP_ELEMENT 
       add constraint FK_TCMGELEMENT_GROUP 
       foreign key (CONCEPT_MAP_GROUP_PID) 
       references TRM_CONCEPT_MAP_GROUP;

    alter table if exists TRM_CONCEPT_MAP_GRP_ELM_TGT 
       add constraint FK_TCMGETARGET_ELEMENT 
       foreign key (CONCEPT_MAP_GRP_ELM_PID) 
       references TRM_CONCEPT_MAP_GRP_ELEMENT;

    alter table if exists TRM_CONCEPT_PC_LINK 
       add constraint FK_TERM_CONCEPTPC_CHILD 
       foreign key (CHILD_PID) 
       references TRM_CONCEPT;

    alter table if exists TRM_CONCEPT_PC_LINK 
       add constraint FK_TERM_CONCEPTPC_CS 
       foreign key (CODESYSTEM_PID) 
       references TRM_CODESYSTEM_VER;

    alter table if exists TRM_CONCEPT_PC_LINK 
       add constraint FK_TERM_CONCEPTPC_PARENT 
       foreign key (PARENT_PID) 
       references TRM_CONCEPT;

    alter table if exists TRM_CONCEPT_PROPERTY 
       add constraint FK_CONCEPTPROP_CSV 
       foreign key (CS_VER_PID) 
       references TRM_CODESYSTEM_VER;

    alter table if exists TRM_CONCEPT_PROPERTY 
       add constraint FK_CONCEPTPROP_CONCEPT 
       foreign key (CONCEPT_PID) 
       references TRM_CONCEPT;

    alter table if exists TRM_VALUESET 
       add constraint FK_TRMVALUESET_RES 
       foreign key (RES_ID) 
       references HFJ_RESOURCE;

    alter table if exists TRM_VALUESET_C_DESIGNATION 
       add constraint FK_TRM_VALUESET_CONCEPT_PID 
       foreign key (VALUESET_CONCEPT_PID) 
       references TRM_VALUESET_CONCEPT;

    alter table if exists TRM_VALUESET_C_DESIGNATION 
       add constraint FK_TRM_VSCD_VS_PID 
       foreign key (VALUESET_PID) 
       references TRM_VALUESET;

    alter table if exists TRM_VALUESET_CONCEPT 
       add constraint FK_TRM_VALUESET_PID 
       foreign key (VALUESET_PID) 
       references TRM_VALUESET;
