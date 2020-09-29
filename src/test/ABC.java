package test;

import blackboard.data.registry.SystemRegistryEntry;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.registry.SystemRegistryEntryDbLoader;
import blackboard.persist.registry.SystemRegistryEntryDbPersister;

import java.net.InetAddress;

public class ABC {
    public void test() {
        System.out.println("running.......");

        //判断，只有在数据库中存在该服务器信息，才能进行自动更新操作，确保每次自动更新只能有一个服务器进行
        String hostName = null;
        String key = "plgnhndlSN";
        SystemRegistryEntry entry = null;
        SystemRegistryEntryDbLoader sredl;
        try {
            //获得数据库中保留的应用服务器信息
            sredl = SystemRegistryEntryDbLoader.Default.getInstance();
            entry = sredl.loadByKey(key);

            //获得当前服务器信息
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName().toString(); //获取本机计算机名称
            System.out.println("hostName.......|" + hostName + "|++++entry　: " + entry.getValue());
        } catch (KeyNotFoundException e) {
            //如果数据库中为空，则保存当前服务器信息
            entry = new SystemRegistryEntry();

            try {
                SystemRegistryEntryDbPersister srep = SystemRegistryEntryDbPersister.Default.getInstance();
                entry.setKey(key);
                InetAddress addr = InetAddress.getLocalHost();
                hostName = addr.getHostName().toString(); //获取本机计算机名称
                entry.setValue(hostName);
                srep.persist(entry);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (entry != null) {
            //如果当前服务器信息与数据库中信息一致，则进行跟新操作
            if (hostName.equals(entry.getValue())) {

            }
        }
    }}
