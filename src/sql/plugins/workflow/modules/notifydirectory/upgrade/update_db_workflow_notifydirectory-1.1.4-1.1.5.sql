--
-- WORKFLOWNOTIFYDIRECTORY-16 : Add the possibility to joined file entries and  url upload entries in notification attachments
--
 CREATE TABLE task_notify_directory_ef(
  id_task INT DEFAULT NULL,
  position_directory_entry_file INT DEFAULT NULL,
  PRIMARY KEY  (id_task)
  ); 
  
  ALTER TABLE task_notify_directory_ef ADD CONSTRAINT fk_id_task_template FOREIGN KEY (id_task)
     REFERENCES task_notify_directory_cf (id_task)  ON DELETE RESTRICT ON UPDATE RESTRICT ;
