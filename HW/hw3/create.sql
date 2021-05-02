-- HW#3
-- Section 2
-- Student ID: 012662273
-- Chau, Caitlyn

connect to cs157a^
CREATE TABLE hw3.student (
  student_id char (5) NOT NULL PRIMARY KEY,
  first varchar (20) NOT NULL,
  last varchar (20) NOT NULL,
  gender char (1) NOT NULL CHECK (gender = 'M' OR gender = 'F' OR gender = 'U')
)^

--Creating class table
CREATE TABLE hw3.class (
  class_id char (5) NOT NULL PRIMARY KEY,
  name varchar (10) NOT NULL,
  desc varchar (30) NOT NULL
)^

--Creating pre-req table
CREATE TABLE hw3.class_prereq (
  class_id char (5) NOT NULL,
  prereq_id char (5) NOT NULL,
  FOREIGN KEY (class_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE,
  FOREIGN KEY (prereq_id) REFERENCES hw3.class(class_id) ON DELETE CASCADE
)^

--Creating schedule table
CREATE TABLE hw3.schedule (
  student_id char (5) NOT NULL,
  class_id char (5) NOT NULL,
  semester int NOT NULL CHECK (semester IN (1, 2, 3)),
  year int NOT NULL CHECK (year >= 2013 AND year <= 2021),
  grade char (1) NULL CHECK (grade = 'A' OR grade = 'B' or grade = 'C' or grade = 'D' or grade = 'F' or grade = 'I'),
  FOREIGN KEY (student_id) REFERENCES hw3.student (student_id),
  FOREIGN KEY (class_id) REFERENCES hw3.class (class_id) ON DELETE CASCADE
)^

CREATE TRIGGER hw3.classcheck
NO CASCADE BEFORE INSERT ON hw3.schedule
REFERENCING NEW AS newrow  
FOR EACH ROW MODE DB2SQL
-- get pre req ids for new row class_id -- get all classes taken by new row student_id
-- 3
WHEN ( 0 < (SELECT COUNT(*) as prereq_count 
  FROM hw3.class_prereq 
  WHERE hw3.class_prereq.class_id = newrow.class_id))

BEGIN ATOMIC
  DECLARE final_count int;
  DECLARE prereq_count int;

  SET prereq_count = (SELECT COUNT(*) 
    FROM hw3.class_prereq 
    WHERE hw3.class_prereq.class_id = newrow.class_id
  );

  SET final_count = (SELECT COUNT(*) 
    FROM hw3.schedule, 
      (SELECT prereq_id FROM hw3.class_prereq WHERE hw3.class_prereq.class_id = newrow.class_id) prereq 
    WHERE hw3.schedule.class_id = prereq.prereq_id 
      AND hw3.schedule.student_id = newrow.student_id 
      AND (hw3.schedule.grade = 'A' OR hw3.schedule.grade = 'B' OR hw3.schedule.grade = 'C' OR hw3.schedule.grade = 'D')
  );
  
  IF ( final_count < prereq_count ) 
    THEN SIGNAL SQLSTATE '88888' ( 'Missing Pre-req' );
  END IF;
END^

terminate;
