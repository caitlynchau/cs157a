-- sample test.sql file
connect to sample;
--select count(*) from department;
--select count(*) from employee;
--select empno from employee;
--select empno from employee order by empno desc;
--select empno,lastname,firstnme from employee order by lastname,firstnme;

-- question 1
select empno,lastname,firstnme
from employee
where hiredate between '2000-01-01' and '2005-12-31'
order by lastname,firstnme

-- question 2
select empno,lastname,firstnme,job,deptno,deptname
from employee
inner join department
on employee.workdept = department.deptno
where deptname='OPERATIONS' or deptname='PLANNING'
order by deptname;

-- question 3
select empno,lastname,firstnme, salary + bonus + comm as totalcomp
from employee
order by totalcomp desc;

-- question 4
select empno,lastname,firstnme, salary + bonus + comm as totalcomp
from employee
order by totalcomp
limit 10;


-- question 5
select job, count(*) as total
from employee
group by job
having count(*) > 0
order by total desc;

-- question 6

select deptno, deptname, count(empno) as total
from department
inner join employee on department.deptno = employee.workdept
group by deptno, deptname
having count(empno) > 0
order by total;

terminate;
-- end of file
