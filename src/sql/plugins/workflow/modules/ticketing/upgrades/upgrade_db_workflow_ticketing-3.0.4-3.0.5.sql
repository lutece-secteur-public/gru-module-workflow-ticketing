-- Ticketing  Configuration table for AssignTicketToUnitTASK
DROP TABLE if exists workflow_task_ticketing_assign_unit_config;
CREATE TABLE workflow_task_ticketing_assign_unit_config (
  id_task int(11) NOT NULL,
  level_1 tinyint(1) DEFAULT NULL,
  level_2 tinyint(1) DEFAULT NULL,
  level_3 tinyint(1) DEFAULT NULL,
  PRIMARY KEY (id_task)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;