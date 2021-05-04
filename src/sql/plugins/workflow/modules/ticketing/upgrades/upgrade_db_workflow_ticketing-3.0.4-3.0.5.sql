-- Ticketing  Configuration table for AssignTicketToUnitTASK
CREATE TABLE workflow_task_ticketing_assign_unit_config (
  id_task int(11) NOT NULL,
  id_level int(11) NOT NULL,
  PRIMARY KEY (id_task)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

-- TODO reprise de données (ajout des tâches existantes dans la table)