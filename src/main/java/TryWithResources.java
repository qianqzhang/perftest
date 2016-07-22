/**
 * Created by zhangqianqian on 2016/6/23.
 */
public class TryWithResources implements AutoCloseable {

    public TryWithResources() throws Exception {
      //  throw new Exception("Exception from constructor");
    }

    public void doSomething() throws Exception {
     //   throw new Exception("Exception from method");
    }

    @Override
    public void close() throws Exception {
        throw new Exception("Exception from closeable");
    }

    public static void main1(String [] argv) {
        try(TryWithResources r = new TryWithResources()) {
            r.doSomething();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
