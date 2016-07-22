package perf;

/**
 * Created by zhangqianqian on 2016/7/21.
 */

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * This sample demonstrates how to get started with basic requests to Aliyun OSS
 * using the OSS SDK for Java.
 */
public class AliyunOSSTest {
    private static final Logger logger = LoggerFactory.getLogger(AliyunOSSTest.class);
    private static List<Long> allResultLst = Collections.synchronizedList(new ArrayList());
    private static OSSClient client;
    public static void main(String[] args) throws IOException {

        if (args.length < 7) {
            logger.error("Usage: endpoint, accessKeyId, accessKeySecret, bucketName, threadNum, durationInMin, key");
            System.exit(0);
        }
        String endpoint = args[0];
        String accessKeyId = args[1];
        String accessKeySecret = args[2];
        String bucketName = args[3];
        int threadNum = Integer.parseInt(args[4]);
        int durationInMin = Integer.parseInt(args[5]);
        String key = args[6];

        try {
            ClientConfiguration conf = new ClientConfiguration();
            conf.setMaxConnections(100);
            conf.setConnectionTimeout(500);
            conf.setMaxErrorRetry(3);
            conf.setSocketTimeout(5000);
            client = new OSSClient(endpoint, accessKeyId, accessKeySecret, conf);

        }catch(Exception e){
            logger.error("dlOSSFileTask init failed");
            System.exit(-1);
        }

        Vector<Thread> threads = new Vector<>();
        for (int i = 0; i < threadNum; i++) {
            Thread iThread = new Thread(new dlOSSFileTask(client, durationInMin, bucketName, key, allResultLst));
            threads.add(iThread);
            iThread.start();
            logger.info("submit thread:{}", iThread.getName());
        }

        for (Thread t: threads) {
           try{
               t.join();
           }catch(Exception e){
               logger.error("thread join", e);
           }
        }

        processResultAnalyse();
    }

    private static void processResultAnalyse(){
        logger.info("processResultAnalyse Thread:{} total succ:{}", Thread.currentThread().getName(), allResultLst.size());
        Collections.sort(allResultLst);
        int sum = 0;
        int size = allResultLst.size();
        for(Long l:allResultLst){
        //    logger.info("durationInMill:{}", l);
            sum += l.intValue();
        }
        if(size == 0){
            logger.warn("no data in finalResult.");
            return;
        }
        int static_50percent = allResultLst.get(Math.max(size/2-1, 0)).intValue();
        int static_90percent = allResultLst.get(Math.max(9*size/10-1, 0)).intValue();
        int static_95percent = allResultLst.get(Math.max(95*size/100-1, 0)).intValue();
        int static_99percent = allResultLst.get(Math.max(99*size/100-1,0)).intValue();
        int static_max = allResultLst.get(size-1).intValue();
        int static_average = 0;
        if(size != 0) {
            static_average = sum / size;
        }
        logger.info("[processResultAnalyse] static_average:{}ms  static_50percent:{}ms static_90percent:{}ms static_95percent:{}ms static_99percent:{}ms static_max:{}ms",static_average,static_50percent,static_90percent,static_95percent,static_99percent,static_max);
    }
}
