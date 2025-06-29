package top.meethigher;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

public class KafkaProducerApp {

    public static String pid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String name = runtimeMXBean.getName();
        return name.split("@")[0];
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("PID", pid());
        Properties properties = new Properties();
        properties.load(KafkaProducerApp.class.getClassLoader().getResourceAsStream("producer.properties"));
        new Producer(properties, "meethigher").produce();
    }
}
