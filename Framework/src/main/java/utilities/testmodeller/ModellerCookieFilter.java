package utilities.testmodeller;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;

public class ModellerCookieFilter {
    private HashMap <String, String> cookies = new HashMap<String, String>();

    public RequestSpecification applyCookies(RequestSpecification spec)
    {
        for (String key : cookies.keySet()) {
            spec = spec.cookie(key, cookies.get(key));
        }

        return spec;
    }

    public void saveCookies(Response rsp)
    {
        for (String key : rsp.getCookies().keySet()) {
            cookies.put(key, rsp.getCookie(key));
        }
    }
}
