package itstep.learning.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RestServlet extends HttpServlet {
    protected RestResponse restResponse = new RestResponse();
    protected HttpServletResponse response;
    private final Gson gson = new GsonBuilder().serializeNulls().create();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        response = resp;
        super.service(req, resp);
    }

    protected  void sendResponse(int code) throws IOException {
        restResponse.setStatus(new RestStatus(code));
        response.setContentType("application/json");
        response.getWriter().print(gson.toJson(restResponse));
        System.out.println(gson.toJson(restResponse));
    }

    protected  void sendResponse() throws IOException {
        sendResponse(200);
    }
    protected  void sendResponse(Object body) throws IOException {
        restResponse.setData(body);;
        sendResponse(200);
    }
}
