package test;

import blackboard.data.user.User;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blackboard.classin.controller.ClassinCourseClassController;
import com.blackboard.classin.entity.*;
import com.blackboard.classin.util.FileUtil;
import com.blackboard.classin.util.SearchUtil;
import com.blackboard.classin.util.SystemUtil;
import com.blackboard.classin.util.TimeStampUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
//        HashMap<String, Integer> stringLongHashMap = new HashMap<>();
//        stringLongHashMap.put("tom",3);
//        stringLongHashMap.put("jack",1);
//        stringLongHashMap.put("jim",2);
//        Map<String, Integer> stringIntegerMap = SystemUtil.sortMapByValue(stringLongHashMap);
//        Set<String> strings = stringIntegerMap.keySet();
//        Object[] objects = strings.toArray();
//        String object = (String)objects[strings.size() - 1];
//        System.out.println(object);

//        String s = "1_2_3";
//        System.out.println(s.lastIndexOf("_"));
//        String s = TimeStampUtil.timeStampToTimeNotSecond(1581868973 + "");
//        System.out.println(s);
//        LinkedList<Integer> objects = new LinkedList<>();
//        objects.add(1);
//        objects.add(2);
//        objects.add(3);
//        System.out.println(objects.get(0));
//        String s = TimeStampUtil.timeStampToDate(1604764800+"");
//        LocalDate parse = LocalDate.parse(s);
//        System.out.println(parse.getDayOfWeek().getValue());
//        String s = TimeStampUtil.timeStampToDate(1609848000 + "");
//        System.out.println(s);
//        String a = "1";
//        String s = a.split(",")[0];
//        System.out.println(s);
        String days = "1,2,3";
        int classes = 6;
        String day = "3";
        String[] weekDays = days.split(",");
        String startTimeStamp = "1604892271";
        String hour="12";
        String minute = "12";
        String[] startTimeStamps = new String[classes];
        String[] endTimeStamps = new String[classes];
        String[] firstStartTimeStamps = new String[weekDays.length];
        String[] firstEndTimeStamps = new String[weekDays.length];
        int[] diffDays = new int[classes];
        //课节数与周课节数的余数
        int remainder = classes % weekDays.length;
        //需要新建课节周数
        int weeks = classes / weekDays.length;
        if (remainder > 0){
            weeks = 1 + weeks;
        }
        //System.out.println("weeks"+weeks);
        int classDays = weekDays.length;
        int flag=-1;
        for (int i = 0; i < classDays; i++){
            Integer weekDay = Integer.valueOf(day);
            Integer selectedWeekDay = Integer.valueOf(weekDays[i]);
            if (selectedWeekDay < weekDay){
                int differDay = 7 - weekDay+selectedWeekDay;
//                System.out.println(selectedWeekDay);
//                System.out.println(weekDay);
//                System.out.println(differDay);
                diffDays[i]=differDay;
                long beginTimeStamp = Long.valueOf(startTimeStamp)+differDay*24*60*60;
                long endTimeStamp = new Integer(hour) * 60 * 60 + new Integer(minute) * 60 + beginTimeStamp;
                startTimeStamps[i] = beginTimeStamp+"";
                firstStartTimeStamps[i] = beginTimeStamp+"";
                endTimeStamps[i] = endTimeStamp+"";
                firstEndTimeStamps[i] = endTimeStamp+"";
            } else{
                int differDay = selectedWeekDay-weekDay;
                diffDays[i]=differDay;
                long beginTimeStamp = Long.valueOf(startTimeStamp)+differDay*24*60*60;
                long endTimeStamp = new Integer(hour) * 60 * 60 + new Integer(minute) * 60 + beginTimeStamp;
                startTimeStamps[i] = beginTimeStamp+"";
                endTimeStamps[i] = endTimeStamp+"";
                firstStartTimeStamps[i] = beginTimeStamp+"";
                firstEndTimeStamps[i] = endTimeStamp+"";

            }
        }

        if (weeks >= 2) {
            for (int i = 2; i <= weeks; i++) {

            }
        }

        if (weeks >= 2) {
            for (int i = 2; i <= weeks; i++) {
                for (int n = weekDays.length; n < diffDays.length; n++){
                    diffDays[n] = i*7;
//                    System.out.println(diffDays[n]);
                }
               for (int m = 0; m < firstStartTimeStamps.length; m++) {
                    if (i == weeks) {
//                        System.out.println("#########################"+m);
                        if (remainder != 0 && m >= remainder) {
//                            System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"+remainder);
                            break;
                        }
                    }
                    flag++;
                    if (firstEndTimeStamps.length + flag >= endTimeStamps.length ){
                        break;
                    }
//                    System.out.println(firstStartTimeStamps[m]);
//                    startTimeStamps[firstEndTimeStamps.length + flag] = Long.valueOf(firstStartTimeStamps[m]) + (i-1)*7 * 24 * 60 * 60 + "";
//                    endTimeStamps[firstEndTimeStamps.length + flag] = Long.valueOf(firstEndTimeStamps[m]) + (i-1)*7 * 24 * 60 * 60 + "";
//                    System.out.println("startTime"+startTimeStamps[firstEndTimeStamps.length + flag]);
//                    System.out.println("endTime"+endTimeStamps[firstEndTimeStamps.length + flag]);
                   // System.out.println(firstEndTimeStamps.length + flag);
                }
            }
        }

        for (int i = 0; i < startTimeStamps.length; i++){
            System.out.println("startTime"+TimeStampUtil.timeStampToDate(startTimeStamp));
//            System.out.println("endTime"+endTimeStamps[i]);
//            System.out.println(i);
        }




//        LocalDateTime now = LocalDateTime.now();
//        System.out.println(now.getDayOfWeek().getValue());
//        int[] a = new int[2];
//        for (int i = 0; i < a.length;i++){
//            a[i] = i;
//        }
//        System.out.println(a[1]);
//        String s = "1";
//        String[] split = s.split(",");
//        System.out.println(split.length);
//        String property = System.getProperty("user.dir");
//        Properties properties = new Properties();
//        BufferedWriter writer = new BufferedWriter(new FileWriter(property+File.separator+"batchClassData.properties"));
//        FileInputStream fileInputStream = new FileInputStream(property + File.separator + "batchClassData.properties");
//        properties.load(fileInputStream);
//        properties.put("abc","456");
//        properties.store(writer,"");

//        Properties properties = new Properties();
//        properties.store();
//        double a = 20.5;
//        String format = String.format("%.1f", a / 8);
//        System.out.println(format);
//        ObjectMapper objectMapper = new ObjectMapper();
//        ClassBean bean = new ClassBean();
//        String s = objectMapper.writeValueAsString(bean);
//        System.out.println(s);
//        List<CourseClassConditionData> courseDatas =new ArrayList<>();
//        CourseClassConditionData courseClassConditionData = new CourseClassConditionData();
//        courseClassConditionData.setLate("123");
//        courseDatas.add(courseClassConditionData);
//        CourseClassConditionData courseClassConditionData1 = new CourseClassConditionData();
//        courseClassConditionData1.setLate("234");
//        courseDatas.add(courseClassConditionData1);
//        String fileName = System.getProperty("user.dir") + File.separator+"课程课节汇总数据_" + "高数" + ".csv";
//        BufferedWriter bufferedWriters = null;
//        bufferedWriters = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream
//                (fileName)), "gbk"));
//        bufferedWriters.append("课节名称," + "课节ID," + "课程ID," + "开课时间," + "结束时间," + "教室姓名," + "教师bbid," +
//                "教师手机号," + "实际上课时长(分钟)," + "出勤," + "迟到," + "早退," + "总数," + "学生出勤," +
//                "学生迟到," + "学生早退," + "奖励次数," + "奖励人数," + "举手次数," + "举手人数," + "授权次数," +
//                "授权人数," + "抢答器次数," + "抢答器平均正确率," + "文本课件数量," + "文本课件累计时长," + "音视频课件数量,"
//                + "音视频课件累计时长(分钟)," + "全体禁言次数," + "全体禁言累计时长(分钟)," + "移出学生次数," + "移出学生人数,"
//                + "授权累计时长(分钟)," + "桌面共享次数," + "桌面共享累计时长(分钟)," + "定时器次数," + "骰子次数," + "抢答器次数,"
//                + "小黑板次数," + "小黑帮累计时长(分钟)," + "\n");
//        JSONArray jsonArray = new JSONArray();
//        for (CourseClassConditionData data : courseDatas) {
//            String json = data.getJson();
//            JSONObject jsonObject = (JSONObject) JSONObject.parse(json);
//            bufferedWriters.append(data.getClassName() + "," + data.getClassId() + "," + data.getCourseId() + "," + data.getStartTime() + ","
//                    + data.getCloseTime() + "," + data.getTeacherName() + "," + data.getTeacherBBId() + "," +
//                    data.getTeacherPhone() + "," + data.getTeacheInClassTime() + "," + data.getCheckin() + "," + data.getLate() + ","
//                    + data.getBack() + "," + data.getStudentTotal() + "," + data.getCheckinStudent() + "," +
//                    data.getLaterTotal() + "," + data.getLeaveEarly() + "," + data.getAwardCount() + "," + data.getAwardPeoples() + ","
//                    + data.getHandsupCount() + "," + data.getHandsupPeoples() + "," + data.getAuthorizeCount() + "," +
//                    data.getAuthorizePeoples() + "," + data.getAnswerCount() + "," + data.getAverageAccuracy() + "\n");
//        }
//        bufferedWriters.flush();
//
//        System.out.println(fileName);
//        System.out.println("操作完毕");
//        Object o = JSONObject.toJSON(new CourseClassConditionData());
//        JSONObject parse = (JSONObject)JSONObject.parse(o.toString());
//        System.out.println(parse.get("textCoursewareCount"));
//        System.out.println(new StudentDetail().getAnswerCorrectTimes());
//        System.out.println(System.currentTimeMillis()/1000);
//        BigDecimal bigDecimal = BigDecimal.valueOf(0.5);
//        System.out.println(bigDecimal);
//        FileInputStream fileInputStream = new FileInputStream("D:\\json.txt");
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
//        StringBuilder datas = new StringBuilder();
//        String data;
//        while ((data=bufferedReader.readLine()) != null){
//            datas.append(data+"\n");
//        }
//        System.out.println(datas.toString());
////        System.out.println(datas);
//        JSONObject totalJson = JSONObject.parseObject(datas+"");
//        int courseID = (int) totalJson.get("CourseID");
//        int classID = (int) totalJson.get("ClassID");
//        long closeTime = (int) totalJson.get("CloseTime") - 1200;
//        long startTime = (int) totalJson.get("StartTime");
//        JSONObject dataJson = (JSONObject) totalJson.get("Data");
//        JSONObject inOutEndJson = (JSONObject) dataJson.get("inoutEnd");
//        Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
//        Iterator<Map.Entry<String, Object>> iteratorInOut = users.iterator();
//        HashMap<String, Integer> answerSum = new HashMap<>();
//        HashMap<String, Integer> rightSum = new HashMap<>();
//        while (iteratorInOut.hasNext()) {
//            String uid = iteratorInOut.next().getKey();
//            JSONObject studentJson = (JSONObject) inOutEndJson.get(uid);
//            int identity = (int) studentJson.get("Identity");
//            if (identity != 3) {
//            }
//            String checkin = "";
//            String late = "";
//            String back = "";
//            int totalTime = (int) studentJson.get("Total");
//            if (totalTime>0) {
//                checkin = "出勤";
//            } else {
//                checkin = "缺勤";
//            }
//            JSONArray details = (JSONArray) studentJson.get("Details");
//            JSONObject inData = (JSONObject) details.get(0);
//            JSONObject outData = (JSONObject) details.get(details.size() - 1);
//            long firstInTime = (int) inData.get("Time");
//            long endOutTime = (int) outData.get("Time");
//            //学生实际授课时长
//            long studentClassTime = endOutTime - firstInTime;
//            if (firstInTime > startTime) {
//                late = "迟到";
//            }
//            if (endOutTime < closeTime) {
//                back = "早退";
//            }
//            JSONObject muteJson = (JSONObject) dataJson.get("muteEnd");
//            JSONObject speakerJson = (JSONObject) muteJson.get("Persons");
//            JSONObject talkJson = (JSONObject) speakerJson.get(uid);
//            if(talkJson != null) {
//                int talkTotalTime = (int) talkJson.get("Total");
//            }
//            JSONObject stageJson = (JSONObject) dataJson.get("stageEnd");
//            JSONObject stageUidJson = (JSONObject) stageJson.get(uid);
//            if (stageUidJson != null) {
//                //下台次数
//                int downCount = (int) stageUidJson.get("DownCount");
//                //上台次数
//                int upCount = (int) stageUidJson.get("UpCount");
//                //下台时长
//                int downTotal = (int) stageUidJson.get("DownTotal");
//                //上台时长
//                int upTotal = (int) stageUidJson.get("UpTotal");
//            }
//            JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
//            //移出次数
//            int tickOutCount = 0;
//            JSONArray tickoutDetailJson = (JSONArray) tickOutJson.get(uid);
//            //////////////////修改1
//            if (tickoutDetailJson != null) {
//                tickOutCount = tickoutDetailJson.size();
//            }
//            JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
//            //奖励次数
//            int awardCount = 0;
//            JSONObject awardDetailJson = (JSONObject) awardJson.get(uid);
//            /////////////////修改
//            if(awardDetailJson != null) {
//                awardCount = (int) awardDetailJson.get("Total");
//            }
//            JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
//            JSONObject handsupDetailJson = (JSONObject) handsupJson.get(uid);
//            /////////////////修改4
//            if(handsupDetailJson != null) {
//                int handsupCount = (int) handsupDetailJson.get("Total");
//            }
//            JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
//            //授权次数
//            int authorizeCount = 0;
//            //授权时长
//            int authorizeTime=0;
//            JSONObject authorizeDetailJson = (JSONObject) authorizeJson.get(uid);
//            authorizeCount = (int)authorizeDetailJson.get("Count");
//            authorizeTime = (int) authorizeDetailJson.get("Total");
//            JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
//            //抢答器次数
//            int responderCount = 0;
//            //使用抢答器抢中次数
//            int responderSurse = 0;
//            JSONObject responderPersonsJson = (JSONObject)responderJson.get("Persons");
//            JSONObject responderDetailJson = (JSONObject) responderPersonsJson.get(uid);
//            ////////////////////////修改2
//            if(responderDetailJson != null) {
//                responderCount = (int) responderDetailJson.get("Count");
//                responderSurse = (int) responderDetailJson.get("SCount");
//            }
//            JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
//            JSONArray answerDetailArray = (JSONArray) answerJson.get("Answers");
//            for (Object o : answerDetailArray){
//                JSONObject anserDetailJson = (JSONObject)o;
//                String correctItems = (String) anserDetailJson.get("CorrectItems");
//                JSONObject studentAnswerJson = (JSONObject) anserDetailJson.get(uid);
//                if (studentAnswerJson != null){
//                    String selectedItem = (String) studentAnswerJson.get("SelectedItem");
//                    if (correctItems.equals(selectedItem)){
//                        if (rightSum.get(uid) != null){
//                            Integer rightCount = rightSum.get(uid);
//                            rightSum.put(uid,++rightCount);
//                        } else{
//                            rightSum.put(uid,1);
//                        }
//                    }else{
//                        if (rightSum.get(uid) == null){
//                            rightSum.put(uid,0);
//                        }
//                    }
//                    if (answerSum.get(uid) != null){
//                        Integer anserCount = answerSum.get(uid);
//                        answerSum.put(uid,++anserCount);
//                    }else {
//                        answerSum.put(uid,1);
//                    }
//                }
//            }
//            JSONObject equipmentsJson = (JSONObject) dataJson.get("equipmentsEnd");
//            JSONObject equipmentsDetailJson = (JSONObject) equipmentsJson.get(uid);
//            if (equipmentsDetailJson != null){
//                JSONObject cameraJson = (JSONObject) equipmentsDetailJson.get("Camera");
//                int cameraTotalTime = (int)cameraJson.get("Total");
//                System.out.println(cameraTotalTime);
//            }
//        }
//
//        answerSum.forEach((key,value)->{
//            System.out.println("key"+key);
//            System.out.println("value"+value);
//        });
//        Set<Map.Entry<String, Integer>> anserSet = answerSum.entrySet();
//        Iterator<Map.Entry<String, Integer>> iteratorAnswer = anserSet.iterator();
//        while (iteratorAnswer.hasNext()) {
//            Map.Entry<String, Integer> next = iteratorAnswer.next();
////            String uid = next.getKey();
////            System.out.println("*******************"+uid);
//            //答题器次数
//            Integer answerCount = next.getValue();
//           // System.out.println(answerCount);
//        }

//        Set<Map.Entry<String, Integer>> rightSet = rightSum.entrySet();
//        Iterator<Map.Entry<String, Integer>> iteratorRight = rightSet.iterator();
//        while (iteratorRight.hasNext()) {
////            String uid = iteratorRight.next().getKey();
//            //答题正确次数
//            Integer rightCount = iteratorRight.next().getValue();
//          //  System.out.println(rightCount);
//        }

//        FileInputStream fileInputStream = new FileInputStream("D:\\json.txt");
//        byte[] data = new byte[100000];
//        String datas="";
//        while (fileInputStream.read(data) != -1){
//            datas = new String(data);
//        }
//       // System.out.println(datas);
//        HashMap<String, String> paraMap = new HashMap<>();
//        JSONObject totalJson = JSONObject.parseObject(datas);
//        int courseID = (int) totalJson.get("CourseID");
//        int classID = (int) totalJson.get("ClassID");
//        long closeTime = (int) totalJson.get("CloseTime");
//        long startTime = (int) totalJson.get("StartTime")-1200;
//        JSONObject dataJson = (JSONObject)totalJson.get("Data");;
//        JSONObject inOutEndJson = (JSONObject)dataJson.get("inoutEnd");
//        String checkin="";
//        String late="";
//        String back="";
//
//
//        Set<Map.Entry<String, Object>> users = inOutEndJson.entrySet();
//        //出勤学生数
//        int checkinStudent = users.size();
//
//        //学生迟到总数
//        int laterTotal=0;
//
//        //学生早退总数
//        int leaveEarly=0;
//        Iterator<Map.Entry<String, Object>> iterator = users.iterator();
//        while (iterator.hasNext()){
//            String uid = iterator.next().getKey();
//            if (uid.equals("123")){
//                continue;
//            } else {
//                JSONObject inOutJson = (JSONObject)inOutEndJson.get(uid) ;
//                JSONArray inOutArray = (JSONArray) inOutJson.get("Details");
//                JSONObject inObject = (JSONObject) inOutArray.get(0);
//                long inTime = Long.valueOf(inObject.get("Time").toString());
//                if (inTime > startTime){
//                    laterTotal++;
//                }
//                JSONObject outObject = (JSONObject) inOutArray.get(inOutArray.size() - 1);
//                long outTime = Long.valueOf(outObject.get("Time").toString());
//                if (outTime < closeTime){
//                    leaveEarly++;
//                }
//            }
//        }
//        JSONObject tickOutJson = (JSONObject) dataJson.get("kickoutEnd");
//        //移出学生人数
//        int tickOutPeoples = tickOutJson.size();
//
//        //移出学生次数
//        int tickOutCount=0;
//        Iterator<Map.Entry<String, Object>> tickoutIterator = tickOutJson.entrySet().iterator();
//        while (tickoutIterator.hasNext()){
//            String uid = tickoutIterator.next().getKey();
//            JSONArray uidJsonArray = (JSONArray) tickOutJson.get(uid);
//            tickOutCount += uidJsonArray.size();
//        }
//        JSONObject awardJson = (JSONObject) dataJson.get("awardEnd");
//        //奖励人数
//        int awardPeoples = awardJson.size();
//        //奖励次数
//        int awardCount=0;
//        Iterator<Map.Entry<String, Object>> awardIterator = awardJson.entrySet().iterator();
//        while (awardIterator.hasNext()){
//            String uid = awardIterator.next().getKey();
//            JSONObject uidJsonArray = (JSONObject) awardJson.get(uid);
//            int total = (int)uidJsonArray.get("Total");
//            awardCount += total;
//        }
//        JSONObject handsupJson = (JSONObject) dataJson.get("handsupEnd");
//        //举手人数
//        int handsupPeoples = handsupJson.size();
//        //举手次数
//        int handsupCount=0;
//        Iterator<Map.Entry<String, Object>> handsupIterator = handsupJson.entrySet().iterator();
//        while (handsupIterator.hasNext()){
//            String uid = handsupIterator.next().getKey();
//            JSONObject uidJsonArray = (JSONObject) handsupJson.get(uid);
//            int total = (int)uidJsonArray.get("Total");
//            handsupCount += total;
//        }
//
//        JSONObject authorizeJson = (JSONObject) dataJson.get("authorizeEnd");
//        //举手人数
//        int authorizePeoples = authorizeJson.size();
//        //举手次数
//        int authorizeCount=0;
//        int authorizeTime=0;
//        Iterator<Map.Entry<String, Object>> authorizeIterator = authorizeJson.entrySet().iterator();
//        while (authorizeIterator.hasNext()){
//            String uid = authorizeIterator.next().getKey();
//            JSONObject uidJsonArray = (JSONObject) authorizeJson.get(uid);
//            int total = (int)uidJsonArray.get("Count");
//            int totalTime = (int)uidJsonArray.get("Total");
//            authorizeTime += totalTime;
//            authorizeCount += total;
//        }
//
//        JSONObject screenshareJson = (JSONObject) dataJson.get("screenshareEnd");
//        //举手人数
//        int screenshareTime=0;
//        //举手次数
//        int screenshareCount=0;
//        screenshareTime= (int)screenshareJson.get("Total");
//        screenshareCount = (int)screenshareJson.get("Count");
//
//        JSONObject timerJson = (JSONObject) dataJson.get("timerEnd");
//        //定时器次数
//        int timerCount=0;
//        //计时器次数
//        int computeTimerCount=0;
//        timerCount= (int)timerJson.get("Count");
//        computeTimerCount = (int)timerJson.get("Timing_Count");
//
//        JSONObject diceJson = (JSONObject) dataJson.get("diceEnd");
//        //定时器次数
//        int diceCount=0;
//        diceCount= (int)diceJson.get("Count");
//        JSONObject responderJson = (JSONObject) dataJson.get("responderEnd");
//        //抢答器次数
//        int responderCount=0;
//        responderCount= (int)responderJson.get("Count");
//
//        JSONObject answerJson = (JSONObject) dataJson.get("answerEnd");
//        //抢答器次数
//        int answerCount=0;
//
//        answerCount= (int)answerJson.get("Count");
//        double averageAccuracy=0;
//        averageAccuracy= new Double(answerJson.get("AverageAccuracy")+"");
//
//        JSONObject smallboardJson = (JSONObject) dataJson.get("smallboardEnd");
//        //抢答器次数
//        int smallboardCount=0;
//
//        smallboardCount= (int)smallboardJson.get("Count");
//        int totalTime=0;
//        totalTime= (int)smallboardJson.get("Total");
//
////        ArrayList<Integer> integers = new ArrayList<>();
////        LinkedHashSet<Integer> integers1 = new LinkedHashSet<>();
////        integers1.add(1);
////        integers1.add(1);
////        integers.add(1);
////        integers.add(1);
////        for (int a : integers1){
////            System.out.println(a);
////        }
//
//        InputStream resource = ClassinCourseClassController.class.getClassLoader().getResourceAsStream("FileFormat.properties");
//        Properties properties = new Properties();
//        properties.load(resource);
//        String textFile = properties.getProperty("textFile");
//        String av = properties.getProperty("av");
//
//        JSONObject coursewareJson = (JSONObject) dataJson.get("sharewidgetEnd");
//        //文本课件使用时长
//        int textCoursewareTime=0;
//        //文本课件使用数量
//        int textCoursewareCount=0;
//        //音视频课件使用时长
//        int avCoursewareTime=0;
//        //音视频课件使用数量
//        int avCoursewareCount=0;
//
//        LinkedHashSet<String> avFileNames = new LinkedHashSet<>();
//        LinkedHashSet<String> textFileNames = new LinkedHashSet<>();
//        JSONArray coursewareArray = (JSONArray) coursewareJson.get("Files");
//        for (Object file:coursewareArray){
//            JSONObject fileJson = (JSONObject) file;
//            String fileName = (String) fileJson.get("FileName");
//            String format = fileName.substring(fileName.indexOf(".")+1);
//            int endTime = (int) fileJson.get("EndTime");
//            int beginTime = (int) fileJson.get("StartTime");
//            int duration = endTime - beginTime;
//            if (av.contains(format)) {
//                avCoursewareTime+=duration;
//                avFileNames.add(format);
//            }else if (textFile.contains(format)){
//                textFileNames.add(format);
//                textCoursewareTime+=duration;
//            }
//        }
//
//        HashMap<String, Integer> abc = new HashMap<>();
//        abc.put("123",1);
//        abc.put("123",3);
//        System.out.println(abc.get("222"));

//        avCoursewareCount = avFileNames.size();
//        textCoursewareCount = textFileNames.size();
//        System.out.println(avCoursewareCount);
//        System.out.println(avCoursewareTime);
//        System.out.println(textCoursewareCount);
//        System.out.println(textCoursewareTime);
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



