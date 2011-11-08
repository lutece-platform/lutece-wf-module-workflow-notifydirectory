--
-- WORKFLOWNOTIFYDIRECTORY-15 : Add the possibility to include the link to view the directory record in BO
--
ALTER TABLE task_notify_directory_cf ADD COLUMN is_view_record SMALLINT(6) NOT NULL DEFAULT 0;
ALTER TABLE task_notify_directory_cf ADD COLUMN label_link_view_record VARCHAR(255) DEFAULT NULL;
