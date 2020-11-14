----执行添加字段前，请检查以下字段，表中是否有这些字段

alter table classin_course_class add CLASS_TIME_LENGTH integer default 14400;
alter table classin_course_class add  CLOSE_CLASS_DELAY NUMBER(38) integer default 1200;
alter table classin_course_class add CLASS_NAME VARCHAR2(80);
alter table classin_course_class add  CLASS_TYPE VARCHAR2(30);
alter table classin_course_class add START_TIME VARCHAR2(80);
alter table classin_course_class add CLASS_TOTAL_TIME VARCHAR2(80);
alter table classin_course_class add  START_DATE VARCHAR2(50);
alter table classin_course_class add TEACHER_NAME VARCHAR2(80);
alter table classin_course_class add ASSISTANT_NAME VARCHAR2(80);
alter table classin_course_class add END_TIME_STAMP VARCHAR2(80);
alter table classin_course_class add LIVE NUMBER(38);
alter table classin_course_class add RECORD NUMBER(38);
alter table classin_course_class add REPLAY NUMBER(38);
alter table classin_course_class add BB_COURSE_ID VARCHAR2(80),
alter table classin_course_class add START_TIME_STAMP NUMBER(38);
alter table classin_course_class add BB_USER_NAME VARCHAR2(80);
alter table classin_course_class add USER_NAME VARCHAR2(80);
alter table classin_course_class add STUDENTS_TOTAL INT;
alter table classin_course_class modify class_name varchar2(200);

update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=addTeacher' where registry_key='classin_addteacher_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=removeCourseTeacher' where registry_key='classin_removeCourseTeacher_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=register' where registry_key='classin_register_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=getLoginLinked' where registry_key='classin_entrance_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=editCourseClass' where registry_key='classin_editCourseClass_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=delCourseStudent' where registry_key='classin_delCourseStudent_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=addCourseStudent' where registry_key='classin_addcoursestudent_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=addClassStudentMultiple' where registry_key='classin_addclassstudent_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=addCourseClass' where registry_key='classin_addcourseclass_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=addCourse' where registry_key='classin_addcourse_url';
update system_registry set registry_value='TODO' where registry_key='classin_import_grade_url';
update system_registry set registry_value='TODO' where registry_key='classin_class_activity_info_url';

-----classin在线研讨室第一次升级为classin在线课堂时，注意下面两个sql，第一个语句要改为：insert into system_registry(pk1,registry_key,registry_value) values(system_registry_seq.nextval,'classin_deletecourseclassvideo_url','https://api.eeo.cn/partner/api/course.api.php?action=deleteClassVideo');
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=deleteClassVideo' where registry_key='classin_deletecourseclassvideo_url';
update system_registry set registry_value='https://api.eeo.cn/partner/api/course.api.php?action=delCourseClass' where registry_key='classin_deletecourseclass_url';

insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_addstudent_url','https://api.eeo.cn/partner/api/course.api.php?action=addSchoolStudent');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deleteclassstudent_url','https://api.eeo.cn/partner/api/course.api.php?action=delClassStudentMultiple');
insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_addcourseclassmultiple_url','https://api.eeo.cn/partner/api/course.api.php?action=addCourseClassMultiple');


create table class_schedule_data(ID INT,content varchar2(200),result varchar2(100),reason varchar2(200),course_id varchar2(100),today_time_stamp varchar2(100))

create sequence class_schedule_data_id
minvalue 1             
nomaxvalue           
increment by 1      
start with 1          
nocache;             


---class_schedule_data表的触发器(如果之前没有新建成功，需要再次重建)
create or replace trigger class_schedule_tg
before insert on class_schedule_data for each row
begin
select class_schedule_data_id.nextval into:new.id from dual;
end;


insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_getcoursestudent_url','https://api.eeo.cn/partner/api/course.api.php?action=getCourseStudent');

insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_deletecoursestudent_url','https://api.eeo.cn/partner/api/course.api.php?action=delCourseStudent');


insert into system_registry(pk1,registry_key,registry_value)
values(system_registry_seq.nextval,'classin_label_url','https://api.eeo.cn/partner/api/course.api.php?action=addClassLabels');
