package com.blackboard.classin.util;


import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.platform.BbServiceManager;
import blackboard.platform.LicenseUtil;
import blackboard.platform.context.Context;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.constants.Constants;
import com.blackboard.classin.entity.BBUser;
import com.blackboard.classin.entity.SystemRegistry;
import com.blackboard.classin.entity.Teacher;
import com.blackboard.classin.entity.UserPhone;
import com.blackboard.classin.mapper.SystemRegistryMapper;
import com.blackboard.classin.mapper.UserPhoneMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * Created by wangyan on 2018/8/15.
 */
public class SystemUtil {

	public static JSONObject buildResultMap(int errno, String error){
		JSONObject jsonObject1 = new JSONObject();
		JSONObject jsonObject2 = new JSONObject();
		
		jsonObject2.put("errno", errno);  
		jsonObject2.put("error", error);
		
		jsonObject1.put("error_info",jsonObject2 );
        return jsonObject1;
    }
	
	public static void main(String[] args) {
		//System.out.println(buildResultMap(1,"aa"));
//		System.out.println(LicenseUtil.getHashValue("Snnu@edu"));
//		int vDurationMin = (Integer.parseInt("28") / 60) + 1;
//		System.out.println(vDurationMin);
		System.out.println(getlinkNo());
	}
	
    /**
     * @param plainText
     *            明文
     * @return 32位密文
     */
    public static String MD5Encode(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    /**
     * 验证请求的有效性
     *
     * @param userId
     * @param timeCheck
     * @param pstr
     * @return
     */
    public static boolean validPstr(String userId, String timeCheck, String pstr) {
        String secret = "";
        String key = "yst&#bpii*9d^i97kkyI";
        secret = MD5Encode(userId + timeCheck + key);
        return secret.equalsIgnoreCase(pstr)?true:false;
    }


    /**
     * 根据课程PK获取课程对象
     * @param course_id
     * @return
     */
    public static Course getCourseById(String course_id){

        CourseDbLoader courseDbLoader = null;
        int pk1 = Integer.valueOf(course_id.split("_")[1]);
        Course course = null;
        Id cId = null;
        try {
            cId = Id.generateId(Course.DATA_TYPE, pk1);
        } catch (PersistenceException e) {
            return null;
        }
        try{
            courseDbLoader = CourseDbLoader.Default.getInstance();
            course = courseDbLoader.loadById(cId);
        }catch(PersistenceException p){
            p.printStackTrace();
        }
        return course;
    }


    /**
     * 查询某一个bb课程ID所对应的课程
     * @param bbCousrId
     * @return
     */
    public static Course getCourseBybbCourseId(String bbCousrId){

        CourseDbLoader courseDbLoader = null;
//        int pk1 = Integer.valueOf(course_id.split("_")[1]);
        Course course = null;
//        Id cId = null;
//        try {
//            cId = Id.generateId(Course.DATA_TYPE, pk1);
//        } catch (PersistenceException e) {
//            return null;
//        }
        try{
            courseDbLoader = CourseDbLoader.Default.getInstance();
            course = courseDbLoader.loadByCourseId(bbCousrId);
        }catch(PersistenceException p){
            p.printStackTrace();
        }
        return course;
    }

    
    /**
	 * 判断是否管理员
	 * @return
	 */
	public static boolean isAdministrator() {
		Context _ctx = BbServiceManager.getContextManager().getContext();
		Id userPk = _ctx.getUserId();
		UserDbLoader userDbLoader = null;
		try {
			userDbLoader = UserDbLoader.Default.getInstance();
			User user = userDbLoader.loadById(userPk);

			// 获取用户的系统角色
			User.SystemRole role = user.getSystemRole();
			// 系统管理员有权限进入系统
			if (role.getFieldName().equals("SYSTEM_ADMIN")) {
				System.out.println(" user "+user.getId() +" is Admin,Classin");
				return true;
			}
		}catch(Exception e){
			return false;
		}
		return false;
	}

    /**
     * 判断当前用户在课程中是否是教师
     * @param courseId
     * @return
     */
    public static boolean isTeacher() {
    	
    	//获取课程当前的用户的注册关系
        Context _ctx = BbServiceManager.getContextManager().getContext();
        CourseMembership courseMembership = _ctx.getCourseMembership();
        //该用户注册了课程并且是该课程的教师、助教角色
        if(courseMembership != null 
        		&& (courseMembership.getRoleAsString().equals("INSTRUCTOR") 
        				|| courseMembership.getRoleAsString().equals("TEACHING_ASSISTANT"))) {
        	return true;
        }
    	return false;
    }

    /**
     * 判断当前用户在课程中是否是授课教师
     * @param courseId
     * @return
     */
    public static boolean isClassTeacher() {

        //获取课程当前的用户的注册关系
        Context _ctx = BbServiceManager.getContextManager().getContext();
        CourseMembership courseMembership = _ctx.getCourseMembership();
        //该用户注册了课程并且是该课程的教师、助教角色
        if(courseMembership != null
                && (courseMembership.getRoleAsString().equals("INSTRUCTOR"))) {
            return true;
        }
        return false;
    }

    /**
     * 判断当前用户在课程中是否是助教老师
     * @param courseId
     * @return
     */
    public static boolean isAssistantTeacher() {

        //获取课程当前的用户的注册关系
        Context _ctx = BbServiceManager.getContextManager().getContext();
        CourseMembership courseMembership = _ctx.getCourseMembership();
        //该用户注册了课程并且是该课程的教师、助教角色
        if(courseMembership != null
                && (courseMembership.getRoleAsString().equals("TEACHING_ASSISTANT"))) {
            return true;
        }
        return false;
    }

    /**
     * 获取当前用户信息
     * @return
     */
    public static User getCurrentUser() {
    	
    	//使用BB API获取已登录的用户信息
        Context _ctx = BbServiceManager.getContextManager().getContext();
        Id userPk = _ctx.getUserId();
        System.out.println("=========================="+userPk);
        User user = null;
        UserDbLoader uloader = null;
        try {
			uloader = UserDbLoader.Default.getInstance();
			user = uloader.loadById(userPk);
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
    	return user;
    }


    /**
     * 根据用户名获取用户信息
     * @return
     */
    public static User getUserByUserName(String userName) {

        //使用BB API获取已登录的用户信息
        Context _ctx = BbServiceManager.getContextManager().getContext();
        Id userPk = _ctx.getUserId();
        User user = null;
        UserDbLoader uloader = null;
        try {
            uloader = UserDbLoader.Default.getInstance();
            user = uloader.loadByUserName(userName);
        } catch (PersistenceException e) {
            e.printStackTrace();
        }
        return user;
    }
    
    /**
     * 生成随机6位数字
     * @return
     */
    public static String getlinkNo() {
        String linkNo = "";
        // 用字符数组的方式随机
        String model = "0123456789";
        char[] m = model.toCharArray();
        for (int j = 0; j < 6; j++) {
            char c = m[(int) (Math.random() * 10)];
            // 保证六位随机数之间没有重复的
            if (linkNo.contains(String.valueOf(c))) {
                j--;
                continue;
            }
            linkNo = linkNo + c;
        }
        return linkNo;
    }

    /**
     * 获取姓名
     * @author panhaiming
     * @date 20200820
     * */
    public  String getName(String name){
        return name;
    }

    /**
     * 查询是否是课程下学生
     * @return
     */
	public static boolean isStudent() {
		//获取课程当前的用户的注册关系
        Context _ctx = BbServiceManager.getContextManager().getContext();
        CourseMembership courseMembership = _ctx.getCourseMembership();
        //该用户注册了课程并且是该课程的教师、助教角色
        if(courseMembership != null 
        		&& (courseMembership.getRoleAsString().equals("STUDENT"))) {
        	return true;
        }
    	return false;
	}
	
	/**
	 * 根据课程ID获取课程
	 * @param courseId
	 * @return
	 */
	public static Course getCourseByCourseId(String courseId) {
		try {
			Course course = CourseDbLoader.Default.getInstance().loadByCourseId(courseId);
			return course;
		} catch (KeyNotFoundException e) {
			return null;
		} catch (PersistenceException e) {
			return null;
		}
	}
	
	/**
	 * 根据userID获取用户
	 * @param courseId
	 * @return
	 */
	public static User getUserByUserId(String userId) {
		try {
			User user = UserDbLoader.Default.getInstance().loadByUserName(userId);
			return user;
		} catch (KeyNotFoundException e) {
			return null;
		} catch (PersistenceException e) {
			return null;
		}
	}

	//获取Bb下老师
	public static List<BBUser> getBbTeachers(String courseId, UserPhoneMapper userPhoneMapper) throws PersistenceException, IOException {
        CourseMembershipDbLoader courseMembershipDbLoader  =CourseMembershipDbLoader.Default.getInstance();
        List<CourseMembership.Role> roleList= new ArrayList<>();
        roleList.add(CourseMembership.Role.INSTRUCTOR);
        Id course_Id=Id.generateId(Course.DATA_TYPE, Integer.parseInt(courseId));
        List<CourseMembership> memberships = courseMembershipDbLoader.loadByCourseIdAndRoles(course_Id, roleList);
        ArrayList<BBUser> users = new ArrayList<>();
        String phone = "";
        int i = 0;
        for (CourseMembership courseMembership: memberships) {
            User user = UserDbLoader.Default.getInstance().loadById(courseMembership.getUserId());
            BBUser bbUser = new BBUser();
            UserPhone userPhone = userPhoneMapper.findPhoneByUserId(user.getUserName());
            if (userPhone != null ) {
                phone = userPhone.getPhone();
                bbUser.setPhone(phone);
            } else {
                bbUser.setPhone("   (请绑定手机号)");
            }
            bbUser.setUserName(user.getFamilyName()+user.getMiddleName()+user.getGivenName());
            users.add(bbUser);
        }
        return users;
    }

    //获取Bb下助教
    public static List<BBUser> getBbAssistantTeachers(String courseId,UserPhoneMapper userPhoneMapper) throws PersistenceException, IOException {
        CourseMembershipDbLoader courseMembershipDbLoader  =CourseMembershipDbLoader.Default.getInstance();
        List<CourseMembership.Role> roleListAssistant= new ArrayList<>();
        roleListAssistant.add(CourseMembership.Role.TEACHING_ASSISTANT);
        Id course_Id=Id.generateId(Course.DATA_TYPE, Integer.parseInt(courseId));
        List<CourseMembership> memberships = courseMembershipDbLoader.loadByCourseIdAndRoles(course_Id, roleListAssistant);
        ArrayList<BBUser> users = new ArrayList<>();
        String phone = "";
        for (CourseMembership courseMembership: memberships) {
            User user = UserDbLoader.Default.getInstance().loadById(courseMembership.getUserId());
            BBUser bbUser = new BBUser();
            UserPhone userPhone = userPhoneMapper.findPhoneByUserId(user.getUserName());
            if (userPhone != null) {
                phone = userPhone.getPhone();
                bbUser.setPhone(phone);
            } else {
                bbUser.setPhone("   (请绑定手机号)");
            }
            bbUser.setUserName(user.getFamilyName()+user.getMiddleName()+user.getGivenName());
            users.add(bbUser);
        }
        return users;
    }

    public static void  getUid(UserPhone user, SystemRegistryMapper systemRegistryMapper,UserPhoneMapper userPhoneMapper) throws IOException {
            long currentCreateClassTime = System.currentTimeMillis() / 1000;
            String sID = "SID=" + Constants.SID;
            String safeKey = "safeKey=" + SystemUtil.MD5Encode(Constants.SECRET + currentCreateClassTime);
            String timeStamp = "timeStamp=" + currentCreateClassTime;
            String nickname = "nickname=" + user.getUserId();
            String param_pwd = "password=" + "password";
            String param_telephone = "telephone=" + user.getPhone();
            String param_identity = "";

            StringBuilder strsBuilder = new StringBuilder();
            strsBuilder.append(sID).append("&").append(safeKey).append("&").append(timeStamp).append("&").append(param_telephone)
                    .append("&").append(nickname).append("&").append(param_pwd).append("&");

            String classin_register_url = systemRegistryMapper.getURLByKey("classin_register_url");
            String resultRegisterMapStr = HttpClient.doPost(classin_register_url, strsBuilder.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultRegisterMap = new HashMap<String, Object>();
            if (resultRegisterMapStr != null && !"".equals(resultRegisterMapStr)) {
                resultRegisterMap = objectMapper.readValue(resultRegisterMapStr, Map.class);
                //解析返回的数据
                Map<String, Object> errorInfo = (Map<String, Object>) resultRegisterMap.get("error_info");
                String errno = errorInfo.get("errno").toString();
                String error = errorInfo.get("error").toString();
                String classinUid;
                UserPhone userInfo = new UserPhone();
                if ("1".equals(errno) || "135".equals(errno)) {
                    classinUid = resultRegisterMap.get("data").toString();
                    userInfo.setClassinUid(classinUid);
                    userInfo.setUserId(user.getUserId());
                    userInfo.setPhone(user.getPhone());
                    userPhoneMapper.updatePhone(userInfo);
                }
            }
    }

}