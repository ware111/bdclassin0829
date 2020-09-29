--bb课程-classin课程信息（一对一）
create table bb_course_classin_course(
 bb_course_id VARCHAR2(100) PRIMARY KEY,
 classin_course_id VARCHAR2(32) not null
);

--classin课程-classin课节(一对多)
create table classin_course_class(
 CLASSIN_COURSE_ID VARCHAR2(32),
 CLASSIN_CLASS_ID VARCHAR2(32),
 DTCREATED NUMBER(38),
 TEACHER_PHONE VARCHAR2(20),
 ASSISTANT_PHONE VARCHAR2(20),
 EXPIRE_STATUS VARCHAR2(1),
 LIVE_URL VARCHAR2(300),
 LIVE_INFO VARCHAR2(500),
 DELETE_STATUS VARCHAR2(1),
 CLASS_TIME_LENGTH NUMBER(38),
 CLOSE_CLASS_DELAY NUMBER(38),
 CLASS_NAME VARCHAR2(80),
 CLASS_TYPE VARCHAR2(30),
 START_TIME VARCHAR2(80),
 CLASS_TOTAL_TIME VARCHAR2(80),
 START_DATE VARCHAR2(50),
 TEACHER_NAME VARCHAR2(80),
 ASSISTANT_NAME VARCHAR2(80),
 END_TIME_STAMP VARCHAR2(80),
 LIVE NUMBER(38),
 RECORD NUMBER(38),
 REPLAY NUMBER(38),
 BB_COURSE_ID VARCHAR2(80),
 START_TIME_STAMP NUMBER(38),
 BB_USER_NAME VARCHAR2(80),
 USER_NAME VARCHAR2(80),
CONSTRAINT PK_classin_course_class PRIMARY KEY (classin_course_id,classin_class_id)
);

--课程表内，创建失败的课节
create table class_schedule_data(ID INT,content varchar2(200),result varchar2(100),reason varchar2(200),course_id varchar2(100),today_time_stamp varchar2(100))
--创建针对class_schedule_data表的触发器，实现id自增
--(1)创建序列
create sequence class_schedule_data_id
minvalue 1             --自增字段最小值
nomaxvalue           --最大值 没有就算nomaxvalue
increment by 1      --每次增值1
start with 1           --起始值
nocache;             --不缓存
--(2)为class_schedule_data表创建触发器，实现自增。
create or replace trigger class_schedule_data_tg_insertId
before insert on class_schedule_data for each row
begin
select table1_id.nextval into:new.id from dual;
end;

alter table classin_course_class add class_time_length integer default 14400;
alter table classin_course_class add close_class_delay integer default 1200;

--课节课程表
create table classin_course_timetable(
course_id VARCHAR2(100),
teacher_id VARCHAR2(20),
classDate  date;
class_start_time date,
class_end_time date
);


--研讨室课节
create table classin_course_class_meeting(
classin_course_id VARCHAR2(32),
classin_class_id VARCHAR2(32),
dtcreated VARCHAR2(20),
teacher_phone VARCHAR2(20),
assistant_phone VARCHAR2(20),
expire_status varchar2(1) default '0',
live_url varchar2(100),
delete_status varchar2(1) default 'N'
CONSTRAINT PK_classin_class_meeting PRIMARY KEY (classin_course_id,classin_class_id)
);


--用户-手机号对应信息(一对一)
create table user_phone(
user_id VARCHAR2(50) PRIMARY KEY,
phone VARCHAR2(20) unique
);

alter table user_phone add classin_uid VARCHAR2(50);

--视频信息
create table classin_class_video(
classin_course_id varchar2(20),
classin_class_id varchar2(20),
action_time varchar2(30),
sid_id VARCHAR2(20),
v_timestamp varchar2(30),
vst varchar2(30),
vet varchar2(30),
cmd varchar2(100),
v_url varchar2(500),
v_duration int,
file_id varchar2(50),
v_size int,
delete_status varchar2(1)
);

----    pku@bb#metc=2013
--添加URL
--修改课节信息
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_addteacher_url','https://api.eeo.cn/partner/api/course.api.php?action=addTeacher');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_addstudent_url','https://api.eeo.cn/partner/api/course.api.php?action=addSchoolStudent');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deleteclassstudent_url','https://api.eeo.cn/partner/api/course.api.php?action=delClassStudentMultiple');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_removeCourseTeacher_url','https://api.eeo.cn/partner/api/course.api.php?action=removeCourseTeacher');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_register_url','https://api.eeo.cn/partner/api/course.api.php?action=register');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_entrance_url','https://api.eeo.cn/partner/api/course.api.php?action=getLoginLinked');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_editCourseClass_url','https://api.eeo.cn/partner/api/course.api.php?action=editCourseClass');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_delCourseStudent_url','https://api.eeo.cn/partner/api/course.api.php?action=delCourseStudent');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_addcoursestudent_url','https://api.eeo.cn/partner/api/course.api.php?action=addCourseStudent');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_addclassstudent_url','https://api.eeo.cn/partner/api/course.api.php?action=addClassStudentMultiple');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_addcourseclass_url','https://api.eeo.cn/partner/api/course.api.php?action=addCourseClass');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_addcourse_url','https://api.eeo.cn/partner/api/course.api.php?action=addCourse');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deletecourseclass_url','https://api.eeo.cn/partner/api/course.api.php?action=delCourseClass');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deletecourseclassvideo_url','https://api.eeo.cn/partner/api/course.api.php?action=deleteClassVideo');


insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_import_grade_url','TODO');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_class_activity_info_url','TODO');

commit;