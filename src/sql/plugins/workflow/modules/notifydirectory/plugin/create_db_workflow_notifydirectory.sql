DROP TABLE IF EXISTS task_notify_directory_ef;
DROP TABLE IF EXISTS task_notify_directory_cf;
DROP TABLE IF EXISTS task_notify_directory_key;

/*==============================================================*/
/* Table structure for table tf_directory_cf					*/
/*==============================================================*/

CREATE TABLE task_notify_directory_cf(
  id_task INT DEFAULT NULL,
  id_directory INT DEFAULT NULL,
  position_directory_entry_email INT DEFAULT -1,
  position_directory_entry_sms INT DEFAULT -1,
  position_directory_entry_user_guid INT DEFAULT -1,
  sender_name VARCHAR(255) DEFAULT NULL, 
  subject VARCHAR(255) DEFAULT NULL, 
  message long VARCHAR DEFAULT NULL,
  is_notify_by_email SMALLINT NOT NULL DEFAULT 0,
  is_notify_by_sms SMALLINT NOT NULL DEFAULT 0,
  is_notify_by_mailing_list SMALLINT NOT NULL DEFAULT 0,
  is_notify_by_user_guid SMALLINT NOT NULL DEFAULT 0,
  is_email_validation SMALLINT NOT NULL DEFAULT 0,
  id_state_after_validation INT DEFAULT NULL,
  label_link VARCHAR(255) DEFAULT NULL, 
  message_validation long varchar DEFAULT NULL,
  period_validity INT DEFAULT NULL,
  recipients_cc VARCHAR(255) DEFAULT '' NOT NULL,
  recipients_bcc VARCHAR(255) DEFAULT '' NOT NULL,
  id_mailing_list INT DEFAULT NULL,
  is_view_record SMALLINT NOT NULL DEFAULT 0,
  label_link_view_record VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY  (id_task)
  );
  


CREATE TABLE task_notify_directory_key(
	key_email VARCHAR(255) DEFAULT NULL, 
	id_task INT DEFAULT NULL,
	id_resource INT DEFAULT NULL,
    resource_type VARCHAR(255) DEFAULT NULL, 
	date_expiry timestamp DEFAULT NULL NULL,
	PRIMARY KEY  (key_email)
);

 CREATE TABLE task_notify_directory_ef(
  id_task INT DEFAULT NULL,
  position_directory_entry_file INT DEFAULT NULL,
  PRIMARY KEY  (id_task, position_directory_entry_file)
  ); 
  
  ALTER TABLE task_notify_directory_ef ADD CONSTRAINT fk_id_task_template FOREIGN KEY (id_task)
     REFERENCES task_notify_directory_cf (id_task)  ON DELETE RESTRICT ON UPDATE RESTRICT ;