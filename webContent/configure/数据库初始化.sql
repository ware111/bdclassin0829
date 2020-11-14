--bb课程-classin课程信息（一对一）
create table bb_course_classin_course(
 bb_course_id VARCHAR2(100) PRIMARY KEY,
 classin_course_id VARCHAR2(32) not null
);

--classin课程-classin课节(一对多)
create table classin_course_class(
 CLASSIN_COURSE_ID VARCHAR2(32),
 CLASSIN_CLASS_ID VARCHAR2(32),
 DTCREATED VARCHAR2(20) not null,
 TEACHER_PHONE VARCHAR2(20),
 ASSISTANT_PHONE VARCHAR2(20),
 EXPIRE_STATUS VARCHAR2(1),
 LIVE_URL VARCHAR2(300),
 LIVE_INFO VARCHAR2(500),
 DELETE_STATUS VARCHAR2(1),
 CLASS_TIME_LENGTH NUMBER(38),
 CLOSE_CLASS_DELAY NUMBER(38),
 CLASS_NAME VARCHAR2(200),
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
 STUDENTS_TOTAL INT,
 label_name  varchar(200),
 label_id varchar(200),
CONSTRAINT PK_classin_course_class PRIMARY KEY (classin_course_id,classin_class_id)
);

create table class_schedule_data(ID INT ,content varchar2(200),result varchar2(100),
reason varchar2(200),course_id varchar2(100),YEAR_DATE varchar2(100),SUB_ID VARCHAR2(100))
--创建针对class_schedule_data表的触发器，实现id自增
--(1)创建序列
create sequence class_schedule_data_id

minvalue 1             --自增字段最小值
nomaxvalue           --最大值 没有就算nomaxvalue
increment by 1      --每次增值1
start with 1           --起始值
nocache            --不缓存
--(2)为class_schedule_data表创建触发器，实现自增，如果有课表需求，则对接。
create or replace trigger class_schedule_tg
before insert on class_schedule_data for each row
begin
select class_schedule_data_id.nextval into:new.id from dual;
end;


--课节课程表
create table classin_course_timetable(
course_id VARCHAR2(100),
teacher_id VARCHAR2(20),
classDate  date,
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
delete_status varchar2(1) default 'N',
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

---课节考勤表
create table class_data(course_id varchar2(200),class_name varchar2(200),class_id varchar2(100) unique ,
begin_time varchar2(100),end_time varchar2(100),teacher_bbid varchar2(100),
teacher_phone varchar2(100),teacher_name varchar2(200),teacher_attend_length number(8,4),checkin varchar2(50),
later varchar2(50),leave_early varchar2(50),student_total int,student_checkin varchar2(50),student_later int,student_leave_early int,
text_files int,text_file_total_duration number(8,4),audio_video_files int,audio_video_total_duration number(8,4),
muteall_times int,muteall_total_duration number(8,4),remove_student_times int,
remove_students int,award_times int,award_students int,handsup_times int,
hansup_students int,authorize_times int,authorize_students int,authorize_total_duration number(8,4),
desk_share_times int,desk_share_total_duration number(8,4),count_down_times int,dice_times int,responder_times int,
answer_times int,average_accuracy varchar2(50),blackboard_times int,blackboard_total_duration int,timer_times int;


---课节考勤表json版
create table class_data_json(course_id varchar2(200),class_name varchar2(200),class_id varchar2(100) unique ,
begin_time varchar2(100),end_time varchar2(100),teacher_bbid varchar2(100),
teacher_phone varchar2(100),teacher_name varchar2(200),assistant_phone varchar2(100),assistant_name varchar2(200),assistant_attend_length number(8,4),teacher_attend_length number(8,4),checkin varchar2(50),
later varchar2(50),leave_early varchar2(50),student_total int,student_checkin varchar2(50),student_later int,student_leave_early int,
award_times int,award_students int,handsup_times int,hansup_students int,authorize_times int,authorize_students int,
answer_times int,average_accuracy varchar2(50), merge_column varchar2(3000));

---学生考勤详情表
create table student_detail(course_id varchar2(200), class_name varchar2(200),class_id varchar2(100),
begin_time varchar2(100),end_time varchar2(100),student_bbid varchar2(100) unique,student_name varchar2(200),
student_phone varchar2(100),student_uid varchar2(200) unique,identity varchar2(10),
student_attend_duration number(8,4),checkin varchar2(50),later varchar2(50),leave_early varchar2(50),speaking_duration int,
up_stage_times int,up_duration int,down_stage_times int,down_duration int,remove_times int,
award_times int,handsup_times int,authorize_times int,authorize_total_duration number(8,4),
responder_times int,responder_answer_times int,responder_selected_times int,answer_times int,
answer_collect_times int,camera_duration number(8,4));


---学生考勤详情表json版
create table student_detail_json(course_id varchar2(200), class_name varchar2(200),class_id varchar2(100),
begin_time varchar2(100),end_time varchar2(100),student_bbid varchar2(100) ,student_name varchar2(300),
student_phone varchar2(100),student_uid varchar2(200),identity varchar2(10),
student_attend_duration number(9,4),checkin varchar2(50),later varchar2(50),leave_early varchar2(50),award_times int,handsup_times int,authorize_times int,
answer_times int,answer_correct_times int, merge_column varchar2(4000),teacher_attend_duration NUMBER(9,4),unique(class_id,student_uid,student_bbid));



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
values(system_registry_seq.nextval,'classin_addcourseclass_url','https://api.eeo.cn/partner/api/course.api.php?action=addCourseClassMultiple');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_addcourseclassmultiple_url','https://api.eeo.cn/partner/api/course.api.php?action=addCourseClassMultiple');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deletecourseclass_url','https://api.eeo.cn/partner/api/course.api.php?action=delCourseClass');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deletecourseclassvideo_url','https://api.eeo.cn/partner/api/course.api.php?action=deleteClassVideo');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_getcoursestudent_url','https://api.eeo.cn/partner/api/course.api.php?action=getCourseStudent');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deletecoursestudent_url','https://api.eeo.cn/partner/api/course.api.php?action=delCourseStudent');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_label_url','https://api.eeo.cn/partner/api/course.api.php?action=addClassLabels');

insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_import_grade_url','TODO');
insert into system_registry(pk1,registry_key,registry_value) 
values(system_registry_seq.nextval,'classin_class_activity_info_url','TODO');
create table lable_table(type varchar2(80),value varchar2(100),label_id varchar2(100),label_name varchar2(200));

alter table classin_course_class add label_name  varchar(200);

alter table classin_course_class add label_id varchar(200);


commit;