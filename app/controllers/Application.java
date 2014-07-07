package controllers;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import com.ning.http.client.FluentCaseInsensitiveStringsMap;
import com.ning.http.multipart.FilePart;
import com.ning.http.multipart.MultipartRequestEntity;
import com.ning.http.multipart.Part;

import models.h2.Product;
import models.h2.StockItem;
import models.mysql.User;
import models.h2.Warehouse;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.Routes;
import play.data.Form;
import play.libs.F;
import play.mvc.*;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import scala.Int;
import views.html.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;


import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

import play.libs.WS;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

public class Application extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";

    public static Result index() {
        return ok(index.render());
    }

    public static Result submit() {
        return ok(submit.render());
    }

    public static Result search() {
        return ok(search.render());
    }

    public static Result upload() throws IOException {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart sample = body.getFile("sample");
        if (sample.getFile() != null) {
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//
//            // Build up the Multiparts
//            List<Part> parts = new ArrayList<Part>();
//            parts.add(new FilePart("file", new File("filename")));
//            Part[] partsA = parts.toArray(new Part[parts.size()]);
//
//            // Add it to the MultipartRequestEntity
//            MultipartRequestEntity reqE = new MultipartRequestEntity(partsA, null);
//            reqE.writeRequest(bos);
//            InputStream reqIS = new ByteArrayInputStream(bos.toByteArray());
//            WS.WSRequestHolder req = WS.url("http://192.168.251.145:8090/tasks/create/file")
//                    .setContentType(reqE.getContentType());
//
//            req.post(reqIS);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

// Build up the Multiparts
            List<Part> parts = new ArrayList<>();
            parts.add(new FilePart("file", sample.getFile()));
            Part[] partsA = parts.toArray(new Part[parts.size()]);

// Add it to the MultipartRequestEntity
            MultipartRequestEntity reqE = new MultipartRequestEntity(partsA, new FluentCaseInsensitiveStringsMap());
            reqE.writeRequest(bos);
            InputStream reqIS = new ByteArrayInputStream(bos.toByteArray());
            WS.WSRequestHolder req = WS.url("http://192.168.251.145:8090/tasks/create/file")
                    .setContentType(reqE.getContentType());
            req.post(reqIS);

            return ok("Whatever");
        } else {
            return ok("No have file");
        }
    }

    public static Result doSubmit() {
        return async(
                WS.url("http://192.168.251.139:8090/tasks/list").get().map(
                        new F.Function<WS.Response, Result>() {
                            public Result apply(WS.Response response) {
                                return ok("Response: " + response.asJson());
                            }
                        }
                )
        );
    }



    public static User getLocalUser(final Session session) {
        final AuthUser currentAuthUser = PlayAuthenticate.getUser(session);
        final User localUser = User.findByAuthUserIdentity(currentAuthUser);
        return localUser;
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result restricted() {
        final User localUser = getLocalUser(session());
        return ok(restricted.render(localUser));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result profile() {
        final User localUser = getLocalUser(session());
        return ok(profile.render(localUser));
    }

    public static Result login() {
        return ok(login.render(MyUsernamePasswordAuthProvider.LOGIN_FORM));
    }

    public static Result doLogin() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MyLogin> filledForm = MyUsernamePasswordAuthProvider.LOGIN_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(login.render(filledForm));
        } else {
            // Everything was filled
            return UsernamePasswordAuthProvider.handleLogin(ctx());
        }
    }

    public static Result signup() {
        return ok(signup.render(MyUsernamePasswordAuthProvider.SIGNUP_FORM));
    }

    public static Result jsRoutes() {
        return ok(Routes.javascriptRouter("jsRoutes", controllers.routes.javascript.Signup.forgotPassword())).as("text/javascript");
    }

    public static Result doSignup() {
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<MySignup> filledForm = MyUsernamePasswordAuthProvider.SIGNUP_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(signup.render(filledForm));
        } else {
            // Everything was filled
            // do something with your part of the form before handling the user
            // signup
            return UsernamePasswordAuthProvider.handleSignup(ctx());
        }
    }

    public static String formatTimestamp(final long t) {
        return new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date(t));
    }

}