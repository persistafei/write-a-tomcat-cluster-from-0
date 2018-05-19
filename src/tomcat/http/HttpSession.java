package tomcat.http;

public class HttpSession {
    private String sessionId;

    public HttpSession(){
        sessionId= System.nanoTime()+"";
    }

    public String getSessionId() {
        return sessionId;
    }


}
