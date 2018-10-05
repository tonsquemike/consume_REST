package consumeapi_lib;


import static consumeapi_lib.ConsumeAPI_LIB.GET;
import java.util.concurrent.Callable;

public class Task implements Callable<String> {

    private String HOST;
    private String PORT;
    private String params;

    public Task(String HOST, String PORT, String params) {
        this.HOST   = HOST;
        this.PORT   = PORT;
        this.params = params;
    }

    @Override
    public String call() throws Exception {
        // some code here
        //System.out.println("make GET "+HOST +" PORT "+PORT);
        String response = GET(getParams(), HOST, PORT);
        return getParams()+":::"+response;
    }

    /**
     * @return the params
     */
    public String getParams() {
        return params;
    }

    /**
     * @param params the params to set
     */
    public void setParams(String params) {
        this.params = params;
    }
    
}