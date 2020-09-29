package test;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendEmailUtil {
    public static void main(String[] args) throws MessagingException {
        sendMail("ware1111@126.com","<p>创建课节失败</p><br><label>课节信息:<span>123456</span></label><br><label>创建结果:<span>创建课节失败</span></label><br><label>失败原因:<span><strong>网络原因</strong></span></label>");
    }
    public static boolean sendMail(String emailAddress, String emailMsg) throws MessagingException {

        // 谁发送(发送方)
        String from = "835292859@qq.com";

        // 发给谁
        String to = emailAddress;

        // 发送者的用户名和密码(邮箱登录用)
        final String username = "835292859@qq.com"; // 此处填写发送的邮箱名
        final String password = "bpektnetmpbqbgaf"; // 此处填写登录的邮箱密码

        // 定义properties对象，设置环境信息
        Properties properties = new Properties();

        /*
         * mail.smtp.host ：指定连接的邮件服务器的主机名。如：163邮箱就填写smtp.163.com
         * 若在本地测试的话，需要在本地安装smtp服务器
         */
        properties.setProperty("mail.smtp.host", "smtp.qq.com");

        // mail.smtp.auth：指定客户端是否要向邮件服务器提交验证
        properties.setProperty("mail.smtp.auth", "true");

        /*
         * mail.transport.protocol：指定邮件发送协议：smtp。smtp：发邮件；pop3：收邮件
         * mail.store.protocol:指定邮件接收协议
         */
        properties.setProperty("mail.transport.protocol", "smtp");

        // 获取session对象
        Session session = Session.getInstance(properties);

        // 当设置为true，JavaMail AP就会将其运行过程和邮件服务器的交互命令信息输出到console中，用于JavaMail的调试
        session.setDebug(true);
        try {

            // 创建邮件对象
            MimeMessage message = new MimeMessage(session);

            // 设置邮件发送方
            message.setFrom(new InternetAddress(from));
            // 设置邮件发送的主题<邮件标题>
            message.setSubject("邮件发送设置");
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(from));
            message.addRecipients(Message.RecipientType.CC,InternetAddress.parse(from));
            // 设置邮件发送的内容
            message.setContent(emailMsg, "text/html;charset=utf-8");
            Transport transport = session.getTransport();

            // 连接邮件服务器，“”中填写邮件服务器主机名
            transport.connect("smtp.qq.com", 587, username, password);
            transport.sendMessage(message, new Address[]{new InternetAddress(from)});
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
