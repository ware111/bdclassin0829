package test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.controller.ClassinCourseClassController;
import com.blackboard.classin.entity.ClassBean;
import com.blackboard.classin.entity.ClassinCourseClass;
import com.blackboard.classin.entity.UserPhone;

import java.io.*;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Test {
    static class MyTread extends Thread{
        int j;

        public int getJ() {
            return j;
        }

        public void setJ(int j) {
            this.j = j;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println(j);
            }
        }
    }
    public static void main(String[] args) throws IOException {
        String datas="{\n" +
                "    \"ClassID\": 25672,\n" +
                "    \"CourseID\" : 116576,\n" +
                "    \"Cmd\" : \"End\",\n" +
                "    \"CloseTime\" : 1499718000,\n" +
                "    \"StartTime\" : 1499653800,\n" +
                "    \"SID\" : 1000082,\n" +
                "    \"Data\" : {\n" +
                "        \"inoutEnd\" : {\n" +
                "            \"1002646\" : {\n" +
                "                \"Total\" : 965,\n" +
                "                \"Details\" : [\n" +
                "                    {\n" +
                "                        \"Type\" : \"In\",\n" +
                "                        \"Device\": 0,\n" +
                "                        \"Time\" : 1499673085\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"Type\" : \"Out\",\n" +
                "                        \"Time\" : 1499674050\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"Identity\": 1\n" +
                "            },\n" +
                "            \"1002647\" : {\n" +
                "                \"Total\" : 964,\n" +
                "                \"Details\" : [\n" +
                "                    {\n" +
                "                        \"Type\" : \"In\",\n" +
                "                        \"Device\": 0,\n" +
                "                        \"Time\" : 1499673094\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"Type\" : \"Out\",\n" +
                "                        \"Time\" : 1499674058\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"Identity\": 3\n" +
                "            },\n" +
                "            \"1002648\" : {\n" +
                "                \"Total\" : 827,\n" +
                "                \"Details\" : [\n" +
                "                    {\n" +
                "                        \"Type\" : \"In\",\n" +
                "                        \"Device\": 0,\n" +
                "                        \"Time\" : 1499673196\n" +
                "                    },\n" +
                "                    {\n" +
                "                        \"Type\" : \"Out\",\n" +
                "                        \"Time\" : 1499674023\n" +
                "                    }\n" +
                "                ],\n" +
                "                \"Identity\": 1\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        HashMap<String, String> paraMap = new HashMap<>();
        JSONObject totalJson = JSONObject.parseObject(datas);
        int classID = (int) totalJson.get("ClassID");
//        ClassinCourseClass classInfo = classinCourseClassMapper.findByClassId(classID);
//        String className = classInfo.getClassName();
//        String closeTime = (String) totalJson.get("CloseTime");
//        String startTime = (String) totalJson.get("StartTime");
//        String teacherBBId = classInfo.getUserName();
//        String teacherPhone = classInfo.getTeacherPhone();
        JSONObject dataJson = (JSONObject) totalJson.get("Data");

       // JSONObject dataJson = JSONObject.parseObject(data+"");
        JSONObject inOutEndJson = (JSONObject) dataJson.get("inoutEnd");
//        UserPhone userPhone = userPhoneMapper.findByPhone(teacherPhone);
//        String teacherUid = userPhone.getClassinUid();
     //   JSONObject inOutEndJson = JSONObject.parseObject(inoutEnd);
        JSONObject inOutUIDJson = (JSONObject) inOutEndJson.get(1002646+"");
        //JSONObject inOutUIDJson = JSONObject.parseObject(uid);
        int total_time = (int) inOutUIDJson.get("Total");
        String checkin="";
        if (total_time!=0){
            checkin = "出勤";
        }else {
            checkin = "缺勤";
        }
        JSONArray detailsArray = (JSONArray) inOutUIDJson.get("Details");
        for (Object json : detailsArray){
            JSONObject parse = (JSONObject) JSONObject.parse(json.toString());
            System.out.println(parse.get("Type"));
        }
        Process hostname = Runtime.getRuntime().exec("hostname");
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(hostname.getInputStream()));
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>主机名ip地址"+s);
        }

        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(format+"00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timestamp = date.getTime()/1000;
        System.out.println(timestamp);

        int i = 0;
        while (i < 5){
            if (i==2){
                continue;
            }
            i++;
        }
        //JSONObject inOutDetaiJson = JSONObject.parseObject(details);
//        JSONArray detailsArray = JSONObject.parseArray(details);
//        String wrongDataPath = System.getProperty("user.dir");
//        File file = new File(wrongDataPath);
//        File[] files = file.listFiles();
//        for (File temp : files) {
//            String name = temp.getName();
//            if (name.contains("createClassinClass.jsp")){
//                temp.delete();
//            }
//        }
//        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("mailAddress.properties");
//        Properties properties = new Properties();
//        properties.load(resource);
//        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>路径>"+properties.get("address"));
//        String datas = "{\"name\":\"tom\"}";
//        Object o = JSONObject.toJSON(datas);
//        JSONObject jsonObject = new JSONObject();
//        JSONObject jsonObject1 = JSONObject.parseObject(datas);
//        System.out.println(jsonObject1.get("name"));
//        MyTread myTread = new MyTread();
//        myTread.setJ(1);
//        myTread.start();
//        new MyTread().start();
//        new Date(1599062400);
//        SimpleDateFormat date11 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String s = 1599062400 + "000";
//        Long aLong = Long.valueOf(s);
//        String format11 = date11.format(new Date(aLong));
//        String todayTime= format11;
//        System.out.println(todayTime);
//        new Thread(){
//            @Override
//            public void run() {
//                for (int i = 0; i < 1000; i++){
//                    System.out.println(Thread.currentThread().getId());
//                }
//            }
//        }.start();
//        Test test = new Test();
//        ClassBean bean = new ClassBean();
//        bean.setClass_number("123");
//        Object o = JSONObject.toJSON(bean);
//        System.out.println(o.toString());
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println(123);
//            }
//        },0,5000);
//        String s = test.testGoto();
//        System.out.println(s);
//        test.test();
//        JSONArray jsonArray = new JSONArray();
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("name","tom");
//        jsonArray.add(jsonObject);
//        System.out.println(jsonArray);
//        String str = "AdministratorBlackboard 23612390033";
//        String[] split = str.split(" ");
//        System.out.println(split[1]);
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        stringStringHashMap.put("name","tom");
//        String s = JSONObject.toJSON(stringStringHashMap).toString();
//        Object parse = JSONObject.parse(s);
//        long timeStamp = TimeStampUtil.getTimeStamp("2020-08-11 18:30:00");
//        System.out.println("2020-08-11 18:30:00".split(" ")[0]);
//        HashMap<String, String> stringStringHashMap = new HashMap<>();
//        System.out.println(stringStringHashMap.get("key"));
//        String data = HttpClient.doGet("https://coursehqy.pku.edu.cn/v2/schedule/get-course-list?kcwybm=126-04831420-0006170389-1");
//        ClassScheduleBean classScheduleBean = JSONObject.parseObject(data, ClassScheduleBean.class);
//        System.out.println(classScheduleBean.getList().size());
    }
    int i = 0;
    int j = 1;
    int[] k={1,2,3,4};
    int m = 0;
    int n = 0;
    public String testGoto(){

        System.out.println(123);
        fag:

        for(; i <2;i++){
            if (i==2){
                break fag;
            }
        }
        return i+"";
    }
    public void test() {
//        System.out.println(m);
        n++;
        if (n==2){
            n=0;
           // System.out.println(m);
            return;
        }
        while (m < k.length) {
            if (k[m]>3) {
                //System.out.println(123);
            } else {
                if (n==0) {
                    System.out.println("m" + m);
                }
               // i = 1;
//                if ()
                if (m>=k.length){
                    break;
                }
                System.out.println(m);
                test();
                if (m>=k.length){
                    break;
                }
                i = 0;

                if (n==0) {
                    System.out.println("i" + i + "m" + m);
                }

            }
            //System.out.println(1234);

            m++;
        }
    }
}



