package top.meethigher;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class KafkaConsumerApp {
    public static String pid() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String name = runtimeMXBean.getName();
        return name.split("@")[0];
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("PID", pid());

    }
}
