package top.meethigher;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

public class KafkaConsumerApp {
    public static String pid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String name = runtimeMXBean.getName();
        return name.split("@")[0];
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("PID", pid());
        Properties properties = new Properties();
        properties.load(KafkaConsumerApp.class.getClassLoader().getResourceAsStream("consumer.properties"));
        new Consumer(properties, "meethigher").consume();
    }
}
