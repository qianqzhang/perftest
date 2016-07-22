package perf;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObject;
import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by zhangqianqian on 2016/7/21.
 */

public class dlOSSFileTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(dlOSSFileTask.class);
    private static AtomicLong totalReqNum = new AtomicLong(0);
    private static AtomicLong succReqNum = new AtomicLong(0);
    private List<Long> resultLst;

    private OSSClient client;
    private static int durationInMin;
    private static String bucketName;
    private static String key;

    public dlOSSFileTask(OSSClient ossClient, int durationInMin, String bucketName, String key, List<Long> resultLst) {
        this.client = ossClient;
        this.bucketName = bucketName;
        this.durationInMin = durationInMin;
        this.key = key;
        this.resultLst = resultLst;
    }

    @Override
    public void run() {

        long startTM = System.currentTimeMillis();
        long durationInMill = durationInMin * 60 * 1000;
        long endTM = startTM + durationInMill;
        long s;
        int i = 0;
        while(true) {
            totalReqNum.incrementAndGet();
            try {
                s = System.currentTimeMillis();
              //  logger.info("thread:{} start to get", Thread.currentThread().getName());
                OSSObject object = client.getObject(new GetObjectRequest(bucketName, key));
                InputStream is = object.getObjectContent();
                ByteStreams.toByteArray(is);
                is.close();
              //  logger.info("thread:{} end to get", Thread.currentThread().getName());
                resultLst.add(System.currentTimeMillis() - s);
                succReqNum.incrementAndGet();
                i++;
                if(i % 10 == 0){
                    logger.info("thread:{} got {}", Thread.currentThread().getName(), i);
                }
            } catch (Exception e) {
                logger.error("getObject Exception, key:{}", key, e);
            }
            if(System.currentTimeMillis() >= endTM){
                logger.info("task ends. threadName:{}", Thread.currentThread().getName());
                break;
            }
        }
        logger.info("thread:{} succeed {}", Thread.currentThread().getName(), i);
    }
}
