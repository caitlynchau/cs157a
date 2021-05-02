connect to cs157a;

CREATE TRIGGER hw3.classcheck
NO CASCADE BEFORE INSERT ON hw3.schedule
REFERENCING NEW AS newrow
FOR EACH ROW MODE DB2SQL
WHEN ( 0 < (SELECT COUNT(*)
       FROM hw3.class_prereq
       WHERE hw3.class_prereq.class_id = newrow.class_id) )
BEGIN ATOMIC
       DECLARE final_count int;
       DECLARE prereq_count int;

       DECLARE num_prereq int;
       DECLARE prereq_pass int;

       SET num_prereq = (SELECT COUNT(*)
                            FROM hw3.class_prereq
                            WHERE hw3.class_prereq.class_id = newrow.class_id);

       SELECT prereq_id FROM hw3.

       IF ( num_prereq > 1 )
       THEN      SIGNAL SQLSTATE '88888' ( 'Missing Pre-req' );
       END IF;
END^
