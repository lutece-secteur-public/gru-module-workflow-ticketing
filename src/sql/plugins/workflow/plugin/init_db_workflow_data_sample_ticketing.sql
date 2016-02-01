INSERT INTO workflow_workflow (id_workflow, name, description, creation_date, is_enabled, workgroup_key) 
	VALUES	(301,'Workflow GRU','Workflow GRU','2016-01-13 08:36:34',1,'all');

INSERT INTO workflow_state (id_state, name, description, id_workflow, is_initial_state, is_required_workgroup_assigned, id_icon, display_order) 
	VALUES	(301,'Nouveau','Nouveau',301,1,0,NULL,1),
			(302,'A qualifier','A qualifier',301,0,0,NULL,2),
			(303,'A traiter','A traiter',301,0,0,NULL,3);

INSERT INTO workflow_action (id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action) 
	VALUES 	(301,'Initialisation','Initialisation de la sollicitation',301,301,302,1,1,0,1,0),
			(302,'Qualifier','Qualifier',301,302,303,1,0,0,2,0),
			(303,'Requalifier','Requalifier',301,303,303,1,0,0,3,0),
			(304,'Escalader','Escalader',301,303,303,1,0,0,4,0),
            (305,'Assigner une sollicitation à une autre entité','Assigner une sollicitation à une autre entité',301,303,303,1,0,0,5,0),
            (306,'Assigner une sollicitation à un autre agent','Assigner une sollicitation à un autre agent',301,303,303,1,0,0,6,0),
            (307,'M\'assigner une sollicitation','M\'assigner une sollicitation',301,303,303,1,0,0,7,0);
			
INSERT INTO workflow_task (id_task, task_type_key, id_action, display_order) 
	VALUES 	(301,'taskTicketingGenerateTicketReference',301,1),
			(302,'taskTicketingAssignUnitLinkedToCategory',301,2),
            (321,'taskTicketingQualifyTicket',302,1),
            (322,'taskTicketingAssignTicketToUnit',302,2),
            (341,'taskTicketingQualifyTicket',303,1),
            (351,'taskTicketingAssignTicketToUnit',305,1),
            (361,'taskTicketingAssignTicketToUser',306,1),
            (371,'taskTicketingAssignTicketToMe',307,1),
			(381,'taskTicketingAssignUpTicket',304,1);

			