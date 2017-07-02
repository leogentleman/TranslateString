package translate;

public class Translate {

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20160609000023059";
    private static final String SECURITY_KEY = "qQzVYPXt9q254fuJwvHu";

    public static String translate(String query, String lan) {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        return api.getTransResult(query, "auto", lan);
    }

}
