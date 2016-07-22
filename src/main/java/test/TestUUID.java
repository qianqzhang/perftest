package test;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;


/**
 * Created by zhangqianqian on 2016/6/16.
 */
public class TestUUID {

    public static void main(String[] args) throws IOException {

//        if(args.length < 1){
//            System.out.println("you should input uuid");
//            return;
//        }

       // String uuidStr = args[0];
        String uuidStr = "96041660-32f7-11e6-a4a0-85817f6ce77c";

        UUID uuid = UUID.fromString(uuidStr);
        long time;
        try {
            Calendar uuidEpoch = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            uuidEpoch.clear();
            uuidEpoch.set(1582, 9, 15, 0, 0, 0); // 9 = October
            long epochMillis = uuidEpoch.getTime().getTime();

            time = (uuid.timestamp() / 10000L) + epochMillis;
        }catch(Exception e){
            System.out.println("got exception " + e.getMessage());
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a z");
        String date = sdf.format(new Date(time));
        System.out.println(uuidStr +" create time: " + date);

        String test = "hello world";
        System.out.println("hashcode is " + test.hashCode());
   }
}
