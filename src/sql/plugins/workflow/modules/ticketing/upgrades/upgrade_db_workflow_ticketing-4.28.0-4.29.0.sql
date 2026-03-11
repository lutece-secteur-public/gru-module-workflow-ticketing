-- Ticketing  Configuration table for TaskManageNbRelance
DROP TABLE if exists  workflow_task_manage_nb_relance_config;
CREATE TABLE  workflow_task_manage_nb_relance_config (
  id_task int(11) NOT NULL,
  is_reinit tinyint(1) DEFAULT 1,
  is_usager tinyint(1) DEFAULT 0,
  PRIMARY KEY (id_task)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;