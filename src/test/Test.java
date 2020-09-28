package test;

import blackboard.data.user.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.controller.ClassinCourseClassController;
import com.blackboard.classin.entity.ClassBean;
import com.blackboard.classin.entity.ClassinCourseClass;
import com.blackboard.classin.entity.UserPhone;
import com.blackboard.classin.util.SystemUtil;
import org.springframework.util.LinkedMultiValueMap;

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
        FileInputStream fileInputStream = new FileInputStream("D:\\json.txt");
        byte[] data = new byte[100000];
        String datas="";
        while (fileInputStream.read(data) != -1){
            datas = new String(data);
        }
       // System.out.println(datas);
        HashMap<String, String> paraMap = new HashMap<>();
        JSONObject totalJson = JSONObject.parseObject(datas);
        int courseID = (int) totalJson.get("CourseID");
        int classID = (int) totalJson.get("ClassID");
        long closeTime = (int) totalJson.get("CloseTime");
        long startTime = (int) totalJson.get("StartTime")-1200;
        JSONObject dataJson = (JSONObject)totalJson.get("Data");;
        JSONObject inOutEndJson = (JSONObject)dataJson.get("inoutEnd");
        String checkin="";
        String late="";
        String back="";


        Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
        //出勤学生数
        int checkinStudent = users.size();

        //学生迟到总数
        int laterTotal=0;

        //学生早退总数
        int leaveEarly=0;
        Iterator<Map.Entry<String, Object>> iterator = users.iterator();
        while (iterator.hasNext()){
            String uid = iterator.next().getKey();
            if (uid.equals("123")){
                continue;
            } else {
                JSONObject inOutJson = (JSONObject)inOutEndJson.get(uid) ;
                JSONArray inOutArray = (JSONArray) inOutJson.get("Details");
                JSONObject inObject = (JSONObject) inOutArray.get(0);
                long inTime = Long.valueOf(inObject.get("Time").toString());
                if (inTime > startTime){
                    laterTotal++;
                }
                JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
                long outTime = Long.valueOf(outObject.get("Time").toString());
                if (outTime < closeTime){
                    leaveEarly++;
                }
            }
        }
        JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
        //移出学生人数
        int tickOutPeoples = tickOutJson.size();

        //移出学生次数
        int tickOutCount=0;
        Iterator<Map.Entry<String, Object>> tickoutIterator = tickOutJson.entrySet().iterator();
        while (tickoutIterator.hasNext()){
            String uid = tickoutIterator.next().getKey();
            JSONArray uidJsonArray = (JSONArray) tickOutJson.get(uid);
            tickOutCount += uidJsonArray.size();
        }
        JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
        //奖励人数
        int awardPeoples = awardJson.size();
        //奖励次数
        int awardCount=0;
        Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
        while (awardIterator.hasNext()){
            String uid = awardIterator.next().getKey();
            JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
            int total = (int)uidJsonArray.get("Total");
            awardCount += total;
        }
        JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
        //举手人数
        int handsupPeoples = handsupJson.size();
        //举手次数
        int handsupCount=0;
        Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
        while (handsupIterator.hasNext()){
            String uid = handsupIterator.next().getKey();
            JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
            int total = (int)uidJsonArray.get("Total");
            handsupCount += total;
        }

        JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
        //举手人数
        int authorizePeoples = authorizeJson.size();
        //举手次数
        int authorizeCount=0;
        int authorizeTime=0;
        Iterator<Map.Entry<String, Object>> authorizeIterator = authorizeJson.entrySet().iterator();
        while (authorizeIterator.hasNext()){
            String uid = authorizeIterator.next().getKey();
            JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
            int total = (int)uidJsonArray.get("Count");
            int totalTime = (int)uidJsonArray.get("Total");
            authorizeTime += totalTime;
            authorizeCount += total;
        }

        JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
        //举手人数
        int screenshareTime=0;
        //举手次数
        int screenshareCount=0;
        screenshareTime= (int)screenshareJson.get("Total");
        screenshareCount = (int)screenshareJson.get("Count");

        JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
        //定时器次数
        int timerCount=0;
        //计时器次数
        int computeTimerCount=0;
        timerCount= (int)timerJson.get("Count");
        computeTimerCount = (int)timerJson.get("Timing_Count");

        JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
        //定时器次数
        int diceCount=0;
        diceCount= (int)diceJson.get("Count");
        JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
        //抢答器次数
        int responderCount=0;
        responderCount= (int)responderJson.get("Count");

        JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
        //抢答器次数
        int answerCount=0;

        answerCount= (int)answerJson.get("Count");
        double averageAccuracy=0;
        averageAccuracy= new Double(answerJson.get("AverageAccuracy")+"");

        JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");
        //抢答器次数
        int smallboardCount=0;

        smallboardCount= (int)smallboardJson.get("Count");
        int totalTime=0;
        totalTime= (int)smallboardJson.get("Total");

//        ArrayList<Integer> integers = new ArrayList<>();
//        LinkedHashSet<Integer> integers1 = new LinkedHashSet<>();
//        integers1.add(1);
//        integers1.add(1);
//        integers.add(1);
//        integers.add(1);
//        for (int a : integers1){
//            System.out.println(a);
//        }

        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
        Properties properties = new Properties();
        properties.load(resource);
        String textFile = properties.getProperty("textFile");
        String av = properties.getProperty("av");

        JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
        //文本课件使用时长
        int textCoursewareTime=0;
        //文本课件使用数量
        int textCoursewareCount=0;
        //音视频课件使用时长
        int avCoursewareTime=0;
        //音视频课件使用数量
        int avCoursewareCount=0;

        LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
        LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
        JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
        for (Object file:coursewareArray){
            JSONObject fileJson = (JSONObject) file;
            String fileName = (String) fileJson.get("FileName");
            String format = fileName.substring(fileName.indexOf(".")+1);
            int endTime = (int) fileJson.get("EndTime");
            int beginTime = (int) fileJson.get("StartTime");
            int duration = endTime - beginTime;
            if (av.contains(format)) {
                avCoursewareTime+=duration;
                avFileNames.add(format);
            }else if (textFile.contains(format)){
                textFileNames.add(format);
                textCoursewareTime+=duration;
            }
        }

        avCoursewareCount = avFileNames.size();
        textCoursewareCount = textFileNames.size();
        System.out.println(avCoursewareCount);
        System.out.println(avCoursewareTime);
        System.out.println(textCoursewareCount);
        System.out.println(textCoursewareTime);
//        System.out.println(smallboardCount);
//        System.out.println(totalTime);
//        Process hostname = Runtime.getRuntime().exec("hostname");
//        BufferedReader stdInput = new BufferedReader(new
//                InputStreamReader(hostname.getInputStream()));
//        String s;
//        while ((s = stdInput.readLine()) != null) {
//            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>主机名ip地址"+s);
//        }
//
//        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss"));
//        Date date = null;
//        try {
//            date = new SimpleDateFormat("yyyy-MM-dd").parse(format+"00:00:00");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        long timestamp = date.getTime()/1000;
//        System.out.println(timestamp);

//        int i = 0;
//        while (i < 5){
//            if (i==2){
//                continue;
//            }
//            i++;
//        }
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



