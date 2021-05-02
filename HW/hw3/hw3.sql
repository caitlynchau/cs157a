-- HW#3
-- Section 2
-- Student ID: 012662273
-- Chau, Caitlyn

-- hw3.sql with test cases
connect to cs157a;
delete from hw3.student;
delete from hw3.class;
delete from hw3.class_prereq;
delete from hw3.schedule;

--Inserting data into the student table
INSERT INTO hw3.student VALUES
       ('90000','John','Doe','M'),
       ('90001','Jane','Doe','F'),
       ('90002','James','Bond','M'),
       ('90003','Chris','Newman','U'),
       ('90004','Ken','Tsang','M');

--Inserting data into the class table
INSERT INTO hw3.class VALUES
       ('01000','CS100W','Technical Writing Workshop'),
       ('10000','CS46A','Intro to Programming'),
       ('10001','CS46B','Intro to Data Structures'),
       ('10002','CS47','Intro to Computer Systems'),
       ('10003','CS49J','Programming in Java'),
       ('20000','CS146','Data Structures and Algorithms'),
       ('20001','CS157A','Intro to Database Mgmt Systems'),
       ('20002','CS149','Operating Systems'),
       ('20003','CS160','Intro to Programming'),
       ('20004','CS157B','Database Management Systems II'),
       ('20005','CS157C','NoSQL Database Systems'),
       ('20006','CS151','Object-Oriented Design');

--Inserting data into the classreq table
INSERT INTO hw3.class_prereq VALUES
       ('10001','10000'),
       ('10002','10001'),
       ('20000','10001'),
       ('20001','20000'),
       ('20002','20000'),
       ('20003','01000'),
       ('20003','20000'),
       ('20003','20006'),
       ('20004','20001'),
       ('20005','20001'),
       ('20006','10001');

-- Check that tables were created
select * from hw3.student;
select * from hw3.class;
select * from hw3.class_prereq;
select * from hw3.schedule;

--Test #1 (HW3.STUDENT)
INSERT INTO hw3.student VALUES ('00001','Test','Student 1','M');
INSERT INTO hw3.student VALUES ('00002','Test','Student 2','F');
INSERT INTO hw3.student VALUES ('00003','Test','Student 3','U');
-- error tests
INSERT INTO hw3.student VALUES ('00001','Duplicate','Student ID test','F');
INSERT INTO hw3.student VALUES (NULL, 'Test','Student 2','M');
INSERT INTO hw3.student VALUES ('00004', NULL,'Student 3','M');
INSERT INTO hw3.student VALUES ('00004', 'Test','Student 4','f');
INSERT INTO hw3.student VALUES ('00005', 'Test','Student 5','Z');
INSERT INTO hw3.student VALUES ('00006', 'Test','Student 6',NULL);

--Test #2 (HW3.CLASS)
INSERT INTO hw3.class VALUES ('00010','CS42','Discrete Mathematics');
INSERT INTO hw3.class VALUES ('00020','CS185C','Special Topics');
-- error tests
INSERT INTO hw3.class VALUES ('00010','Duplicate','Class ID test');
INSERT INTO hw3.class VALUES (NULL,'CS42','Bad ID Test');
INSERT INTO hw3.class VALUES ('00011',NULL,'NULL Test');
INSERT INTO hw3.class VALUES ('00012','CS25',NULL);

--Test #3 (HW3.CLASS_PREREQ) Error tests
INSERT INTO hw3.class_prereq VALUES ('00010','99999');
INSERT INTO hw3.class_prereq VALUES ('00010',NULL);
INSERT INTO hw3.class_prereq VALUES ('99999','00010');

--Test #4 (HW3.SCHEDULE) non-existing class id and student id (Foregin Key constraint)
INSERT INTO hw3.schedule VALUES ('99999','10000',1,2019,'A');
INSERT INTO hw3.schedule VALUES ('00001','99999',2,2019,'B');
--Test invalid values which should be block by CHECK contraints, invalid semester & grades
INSERT INTO hw3.schedule VALUES ('00001','00020',4,2019,'C');
INSERT INTO hw3.schedule VALUES ('00002','00020',3,2019,'E');
INSERT INTO hw3.schedule VALUES ('00003','00020',3,2019,'a');

--Test cascade drop of HW3.CLASS (Question 2 from spec), the 2 (SELECT * ...) stmts after DELETE should have 0 record
INSERT INTO hw3.schedule VALUES ('00001','00020',1,2019,'A');
INSERT INTO hw3.class_prereq VALUES ('00020','00010');
SELECT * FROM hw3.class_prereq WHERE hw3.class_prereq.class_id = '00020';
SELECT * FROM hw3.schedule WHERE hw3.schedule.class_id = '00020';
DELETE FROM hw3.class WHERE hw3.class.class_id = '00020';
SELECT * FROM hw3.class_prereq WHERE hw3.class_prereq.class_id = '00020';
SELECT * FROM hw3.schedule WHERE hw3.schedule.class_id = '00020';

-- Test #5 Test Trigger, first empty the table just in case
DELETE from hw3.schedule;
-- Add CS100W, no prereq, should work!
INSERT INTO hw3.schedule VALUES ('90000','01000',1,2018,'C');
-- Add CS46B but no CS46A yet, should fail
INSERT INTO hw3.schedule VALUES ('90000','10001',2,2018,NULL);
-- Add CS46A without grade, should work!
INSERT INTO hw3.schedule VALUES ('90000','10000',1,2018,NULL);
-- Add CS46B but no CS46A grade, should fail
INSERT INTO hw3.schedule VALUES ('90000','10001',1,2018,NULL);
-- Update CS46A grade to 'F'
UPDATE hw3.schedule SET hw3.schedule.grade = 'F' where hw3.schedule.student_id = '90000' AND hw3.schedule.class_id = '10000';
-- Add CS46B but CS47A grade is 'F', should fail
INSERT INTO hw3.schedule VALUES ('90000','10001',1,2018,NULL);
-- Update CS46A grade to 'B'
UPDATE hw3.schedule SET hw3.schedule.grade = 'B' where hw3.schedule.student_id = '90000' AND hw3.schedule.class_id = '10000';
-- Add CS46B with CS46A grade = 'B', should work!
INSERT INTO hw3.schedule VALUES ('90000','10001',2,2018,'A');
-- Add CS151 with prereq CS46B = 'B', should work!
INSERT INTO hw3.schedule VALUES ('90000','20006',3,2018,'B');
-- Add CS160, has CS151,CS100W, but no CS146, should FAIL
INSERT INTO hw3.schedule VALUES ('90000','20003',1,2019,'B');
-- Add CS157A but missing prereq, should fail
INSERT INTO hw3.schedule VALUES ('90000','20001',1,2019,'B');
-- Add CS146 with prereq CS46B = 'B', should work!
INSERT INTO hw3.schedule VALUES ('90000','20000',3,2018,'A');
-- Add CS157A with prereq CS146 = 'A', should work!
INSERT INTO hw3.schedule VALUES ('90000','20001',1,2019,'B');
-- Add CS160, has CS151,CS100W,and CS146, should work!!!
INSERT INTO hw3.schedule VALUES ('90000','20003',1,2019,'B');
-- Test with student '90001'
-- Add CS100W, no prereq, should work!
INSERT INTO hw3.schedule VALUES ('90001','01000',1,2018,'A');
-- Add CS46B but no CS46A yet, should fail
INSERT INTO hw3.schedule VALUES ('90001','10001',2,2018,'B');
-- Add CS157A but missing prereq, should fail
INSERT INTO hw3.schedule VALUES ('90001','20001',1,2019,'B');
-- Add CS46A, no prereq, should work!
INSERT INTO hw3.schedule VALUES ('90001','10000',1,2018,'A');
-- Add CS46B with CS46A grade = 'A', should work!
INSERT INTO hw3.schedule VALUES ('90001','10001',2,2018,'A');
-- Add CS160, has CS100W, but no CS146,CS151, should FAIL
INSERT INTO hw3.schedule VALUES ('90001','20003',1,2019,'B');
-- Add CS151 with prereq CS46B = 'B', should work!
INSERT INTO hw3.schedule VALUES ('90001','20006',3,2018,'B');
-- Add CS146 with prereq CS46B = 'F', should work!
INSERT INTO hw3.schedule VALUES ('90001','20000',3,2018,'F');
-- Add CS160, has CS151,CS100W,but CS146='F', should FAIL
INSERT INTO hw3.schedule VALUES ('90001','20003',1,2019,'B');
-- Update CS146 grade to 'B'
UPDATE hw3.schedule SET hw3.schedule.grade = 'B' where hw3.schedule.student_id = '90001' AND hw3.schedule.class_id = '20000';
-- Add CS160, has CS151,CS100W,and CS146, should work!!!
INSERT INTO hw3.schedule VALUES ('90001','20003',1,2019,'B');
-- The End

-- Check table contents after all the tests
select * from hw3.student;
select * from hw3.class;
select * from hw3.class_prereq;
select * from hw3.schedule;

terminate;
