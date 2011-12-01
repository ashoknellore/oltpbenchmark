DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'IPBLOCKS'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE ipblocks'; END IF;END;;

CREATE TABLE ipblocks (
  ipb_id number(10,0) NOT NULL,
  ipb_address varchar2(255) NOT NULL,
  ipb_usr number(10,0) DEFAULT '0',
  ipb_by number(10,0)  DEFAULT '0',
  ipb_by_text varchar2(255)  DEFAULT '',
  ipb_reason varchar2(255) NOT NULL,
  ipb_timestamp varchar2(14)  DEFAULT '              ',
  ipb_auto number(3,0) DEFAULT '0',
  ipb_anon_only number(3,0) DEFAULT '0',
  ipb_create_account number(3,0) DEFAULT '1',
  ipb_enable_autoblock number(3,0) DEFAULT '1',
  ipb_expiry varchar2(14) DEFAULT '',
  ipb_range_start varchar2(8) NOT NULL,
  ipb_range_end varchar2(8) NOT NULL,
  ipb_deleted number(3,0) DEFAULT '0',
  ipb_block_email number(3,0) DEFAULT '0',
  ipb_allow_usrtalk number(3,0) DEFAULT '0',
  PRIMARY KEY (ipb_id),
  UNIQUE (ipb_address,ipb_usr,ipb_auto,ipb_anon_only)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'IPBLOCKS_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE IPBLOCKS_SEQ'; END IF;END;;

-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE ipblocks_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER ipblocks_seq_tr
 BEFORE INSERT ON ipblocks FOR EACH ROW
 WHEN (NEW.ipb_id IS NULL OR NEW.ipb_id = 0)
BEGIN SELECT ipblocks_seq.NEXTVAL INTO :NEW.ipb_id FROM dual; END;;


CREATE INDEX IDX_IPB_usr ON ipblocks (ipb_usr);
CREATE INDEX IDX_IPB_RANGE ON ipblocks (ipb_range_start,ipb_range_end);
CREATE INDEX IDX_IPB_TIMESTAMP ON ipblocks (ipb_timestamp);
CREATE INDEX IDX_IPB_EXPIRY ON ipblocks (ipb_expiry);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'LOGGING'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE logging'; END IF; END;;

CREATE TABLE logging (
  log_id number(10,0) NOT NULL,
  log_type varchar2(32) NOT NULL,
  log_action varchar2(32) NOT NULL,
  log_timestamp varchar2(14) DEFAULT '19700101000000',
  log_usr number(10,0) DEFAULT '0',
  log_namespace number(10,0) DEFAULT '0',
  log_title varchar2(255) DEFAULT '',
  log_comment varchar2(255) DEFAULT '',
  log_params varchar2(255) NOT NULL,
  log_deleted number(3,0) DEFAULT '0',
  log_usr_text varchar2(255) DEFAULT '',
  log_page number(10,0) DEFAULT NULL,
  PRIMARY KEY (log_id)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN  SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'LOGGING_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE LOGGING_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE logging_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER logging_seq_tr
 BEFORE INSERT ON logging FOR EACH ROW
 WHEN (NEW.log_id IS NULL OR NEW.log_id = 0)
BEGIN
 SELECT logging_seq.NEXTVAL INTO :NEW.log_id FROM dual; END;;

CREATE INDEX IDX_LOG_TYPE_TIME ON logging (log_type,log_timestamp);
CREATE INDEX IDX_LOG_usr_TIME ON logging (log_usr,log_timestamp);
CREATE INDEX IDX_LOG_PAGE_TIME ON logging (log_namespace,log_title,log_timestamp);
CREATE INDEX IDX_LOG_TIMES ON logging (log_timestamp);
CREATE INDEX IDX_LOG_usr_TYPE_TIME ON logging (log_usr,log_type,log_timestamp);
CREATE INDEX IDX_LOG_PAGE_ID_TIME ON logging (log_page,log_timestamp);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'PAGE'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE page'; END IF; END;;

CREATE TABLE page (
  page_id number(10,0) NOT NULL,
  page_namespace number(10,0) NOT NULL,
  page_title varchar2(255) NOT NULL,
  page_restrictions raw(255) NOT NULL,
  page_counter number(20,0) DEFAULT '0',
  page_is_redirect number(3,0) DEFAULT '0',
  page_is_new number(3,0) DEFAULT '0',
  page_random number(3,10) NOT NULL,
  page_touched varchar2(14) DEFAULT '              ',
  page_latest number(10,0) NOT NULL,
  page_len number(10,0) NOT NULL,
  PRIMARY KEY (page_id),
  UNIQUE (page_namespace,page_title)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'PAGE_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE PAGE_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE page_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER page_seq_tr
 BEFORE INSERT ON page FOR EACH ROW
 WHEN (NEW.page_id IS NULL OR NEW.page_id = 0)
BEGIN
 SELECT page_seq.NEXTVAL INTO :NEW.page_id FROM dual; END;;

CREATE INDEX IDX_PAGE_RANDOM ON page (page_random);
CREATE INDEX IDX_PAGE_LEN ON page (page_len);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'PAGE_BACKUP'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE page_backup'; END IF; END;;

CREATE TABLE page_backup (
  page_id number(10,0) NOT NULL,
  page_namespace number(10,0) NOT NULL,
  page_title varchar2(255) NOT NULL,
  page_restrictions varchar2(20) NOT NULL,
  page_counter number(20,0) DEFAULT '0',
  page_is_redirect number(3,0) DEFAULT '0',
  page_is_new number(3,0) DEFAULT '0',
  page_random number NOT NULL,
  page_touched varchar2(14) DEFAULT '              ',
  page_latest number(10,0) NOT NULL,
  page_len number(10,0) NOT NULL,
  PRIMARY KEY (page_id),
  UNIQUE (page_namespace,page_title)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'PAGE_BACKUP_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE PAGE_BACKUP_SEQ'; END IF;END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE page_backup_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER page_backup_seq_tr
 BEFORE INSERT ON page_backup FOR EACH ROW
 WHEN (NEW.page_id IS NULL OR NEW.page_id = 0)
BEGIN
 SELECT page_backup_seq.NEXTVAL INTO :NEW.page_id FROM dual;END;;

CREATE INDEX IDX_PAGE_BACKUP_RANDOM ON page_backup (page_random);
CREATE INDEX IDX_PAGE_BACKUP_LEN ON page_backup (page_len);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'PAGE_RESTRICTIONS'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE page_restrictions'; END IF; END;;

CREATE TABLE page_restrictions (
  pr_page number(10,0) NOT NULL,
  pr_type varchar2(60) NOT NULL,
  pr_level varchar2(60) NOT NULL,
  pr_cascade number(3,0) NOT NULL,
  pr_usr number(10,0) DEFAULT NULL,
  pr_expiry varchar2(14) DEFAULT NULL,
  pr_id number(10,0) NOT NULL,
  PRIMARY KEY (pr_id),
  UNIQUE (pr_page,pr_type)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'PAGE_RESTRICTIONS_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE PAGE_RESTRICTIONS_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE page_restrictions_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER page_restrictions_seq_tr
 BEFORE INSERT ON page_restrictions FOR EACH ROW
 WHEN (NEW.pr_page IS NULL OR NEW.pr_page = 0)
BEGIN
 SELECT page_restrictions_seq.NEXTVAL INTO :NEW.pr_page FROM dual;END;;

CREATE INDEX IDX_PR_TYPELEVEL ON page_restrictions (pr_type,pr_level);
CREATE INDEX IDX_PR_LEVEL ON page_restrictions (pr_level);
CREATE INDEX IDX_PR_CASCADE ON page_restrictions (pr_cascade);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'RECENTCHANGES'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE recentchanges'; END IF; END;;

CREATE TABLE recentchanges (
  rc_id number(10,0) NOT NULL,
  rc_timestamp varchar2(14) DEFAULT '',
  rc_cur_time varchar2(14) DEFAULT '',
  rc_usr number(10,0) DEFAULT '0',
  rc_usr_text varchar2(255) NOT NULL,
  rc_namespace number(10,0) DEFAULT '0',
  rc_title varchar2(255) DEFAULT '',
  rc_comment varchar2(255) DEFAULT '',
  rc_minor number(3,0) DEFAULT '0',
  rc_bot number(3,0) DEFAULT '0',
  rc_new number(3,0) DEFAULT '0',
  rc_cur_id number(10,0) DEFAULT '0',
  rc_this_oldid number(10,0) DEFAULT '0',
  rc_last_oldid number(10,0) DEFAULT '0',
  rc_type number(3,0) DEFAULT '0',
  rc_moved_to_ns number(3,0) DEFAULT '0',
  rc_moved_to_title varchar2(255) DEFAULT '',
  rc_patrolled number(3,0) DEFAULT '0',
  rc_ip varchar2(40) DEFAULT '',
  rc_old_len number(10,0) DEFAULT NULL,
  rc_new_len number(10,0) DEFAULT NULL,
  rc_deleted number(3,0) DEFAULT '0',
  rc_logid number(10,0) DEFAULT '0',
  rc_log_type varchar2(255) DEFAULT NULL,
  rc_log_action varchar2(255) DEFAULT NULL,
  rc_params varchar2(255),
  PRIMARY KEY (rc_id)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'RECENTCHANGES_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE RECENTCHANGES_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE recentchanges_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER recentchanges_seq_tr
 BEFORE INSERT ON recentchanges FOR EACH ROW
 WHEN (NEW.rc_id IS NULL OR NEW.rc_id = 0)
BEGIN
 SELECT recentchanges_seq.NEXTVAL INTO :NEW.rc_id FROM dual;END;;

CREATE INDEX IDX_RC_TIMESTAMP ON recentchanges (rc_timestamp);
CREATE INDEX IDX_RC_NAMESPACE_TITLE ON recentchanges (rc_namespace,rc_title);
CREATE INDEX IDX_RC_CUR_ID ON recentchanges (rc_cur_id);
CREATE INDEX IDX_NEW_NAME_TIMESTAMP ON recentchanges (rc_new,rc_namespace,rc_timestamp);
CREATE INDEX IDX_RC_IP ON recentchanges (rc_ip);
CREATE INDEX IDX_RC_NS_usrTEXT ON recentchanges (rc_namespace,rc_usr_text);
CREATE INDEX IDX_RC_usr_TEXT ON recentchanges (rc_usr_text,rc_timestamp);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'REVISION'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE revision'; END IF; END;;

CREATE TABLE revision (
  rev_id number(10,0) NOT NULL,
  rev_page number(10,0) NOT NULL,
  rev_text_id number(10,0) NOT NULL,
  rev_comment varchar2(200) NOT NULL,
  rev_usr number(10,0) DEFAULT '0',
  rev_usr_text varchar2(255) DEFAULT '',
  rev_timestamp varchar2(14) DEFAULT '              ',
  rev_minor_edit number(3,0) DEFAULT '0',
  rev_deleted number(3,0) DEFAULT '0',
  rev_len number(10,0) DEFAULT NULL,
  rev_parent_id number(10,0) DEFAULT NULL,
  PRIMARY KEY (rev_id),
  UNIQUE (rev_page,rev_id)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'REVISION_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE REVISION_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE revision_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER revision_seq_tr
 BEFORE INSERT ON revision FOR EACH ROW
 WHEN (NEW.rev_id IS NULL OR NEW.rev_id = 0)
BEGIN
 SELECT revision_seq.NEXTVAL INTO :NEW.rev_id FROM dual;END;;

CREATE INDEX IDX_REV_TIMESTAMP ON revision (rev_timestamp);
CREATE INDEX IDX_PAGE_TIMESTAMP ON revision (rev_page,rev_timestamp);
CREATE INDEX IDX_usr_TIMESTAMP ON revision (rev_usr,rev_timestamp);
CREATE INDEX IDX_usrTEXT_TIMESTAMP ON revision (rev_usr_text,rev_timestamp);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'TEXT'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE text'; END IF;END;;

CREATE TABLE text (
  old_id number(10,0) NOT NULL,
  old_text varchar2(255) NOT NULL,
  old_flags varchar2(30) NOT NULL,
  old_page number(10,0) DEFAULT NULL,
  PRIMARY KEY (old_id)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'TEXT_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE TEXT_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE text_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER text_seq_tr
 BEFORE INSERT ON text FOR EACH ROW
 WHEN (NEW.old_id IS NULL OR NEW.old_id = 0)
BEGIN
 SELECT text_seq.NEXTVAL INTO :NEW.old_id FROM dual;END;;


DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'USR'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE usr'; END IF; END;;

CREATE TABLE usr (
  usr_id number(10,0) NOT NULL,
  usr_name varchar2(255) DEFAULT '',
  usr_real_name varchar2(255) DEFAULT '',
  usr_password varchar2(25) NOT NULL,
  usr_newpassword varchar2(25) NOT NULL,
  usr_newpass_time varchar2(14) DEFAULT NULL,
  usr_email varchar2(32) NOT NULL,
  usr_options varchar2(255) NOT NULL,
  usr_touched varchar2(14) DEFAULT '              ',
  usr_token varchar2(32) DEFAULT '                                ',
  usr_email_authenticated varchar2(32) DEFAULT NULL,
  usr_email_token varchar2(32) DEFAULT NULL,
  usr_email_token_expires varchar2(14) DEFAULT NULL,
  usr_registration varchar2(14) DEFAULT NULL,
  usr_editcount number(10,0) DEFAULT NULL,
  PRIMARY KEY (usr_id),
  UNIQUE (usr_name)
);

-- Drop sequence if exists
DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_sequences WHERE sequence_name = 'USR_SEQ'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP SEQUENCE usr_SEQ'; END IF; END;;


-- Implement auto-increment using sequence and trigger
CREATE SEQUENCE usr_seq INCREMENT BY 1 START WITH 1;

CREATE OR REPLACE TRIGGER usr_seq_tr
 BEFORE INSERT ON usr FOR EACH ROW
 WHEN (NEW.usr_id IS NULL OR NEW.usr_id = 0)
BEGIN
 SELECT usr_seq.NEXTVAL INTO :NEW.usr_id FROM dual;END;;

CREATE INDEX IDX_usr_EMAIL_TOKEN ON usr (usr_email_token);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'USR_GROUPS'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE usr_groups'; END IF; END;;

CREATE TABLE usr_groups (
  ug_usr number(10,0) DEFAULT '0',
  ug_group varchar2(16) DEFAULT '',
  UNIQUE (ug_usr,ug_group)
);
CREATE INDEX IDX_UG_GROUP ON usr_groups (ug_group);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'VALUE_BACKUP'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE value_backup'; END IF; END;;

CREATE TABLE value_backup (
  table_name varchar2(255) DEFAULT NULL,
  maxid number(10,0) DEFAULT NULL
);

DECLARE cnt NUMBER; BEGIN SELECT count(*) INTO cnt FROM all_tables WHERE table_name = 'WATCHLIST'; IF cnt > 0 THEN EXECUTE IMMEDIATE 'DROP TABLE watchlist'; END IF; END;;

CREATE TABLE watchlist (
  wl_usr number(10,0) NOT NULL,
  wl_namespace number(10,0) DEFAULT '0',
  wl_title varchar2(255) DEFAULT '',
  wl_notificationtimestamp varchar2(14) DEFAULT NULL,
  UNIQUE (wl_usr,wl_namespace,wl_title)
);
CREATE INDEX IDX_WL_NAMESPACE_TITLE ON watchlist (wl_namespace, wl_title);