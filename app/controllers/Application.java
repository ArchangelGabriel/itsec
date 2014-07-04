package controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import models.h2.Product;
import models.h2.StockItem;
import models.mysql.User;
import models.h2.Warehouse;
import play.Routes;
import play.data.Form;
import play.mvc.*;
import play.mvc.Http.Session;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import providers.MyUsernamePasswordAuthProvider.MyLogin;
import providers.MyUsernamePasswordAuthProvider.MySignup;

import views.html.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;


import com.feth.play.module.pa.PlayAuthenticate;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthProvider;
import com.feth.play.module.pa.user.AuthUser;

import play.libs.F.Function;
import play.libs.WS;

import static java.lang.String.format;

public class Application extends Controller {

    public static final String FLASH_MESSAGE_KEY = "message";
    public static final String FLASH_ERROR_KEY = "error";
    public static final String USER_ROLE = "user";

//    public static Result index() {
//        return ok(index.render());
//    }

    public static Result index() {
        Warehouse warehouse = new Warehouse();
        warehouse.name = "My Warehouse";

        Product product = new Product();
        product.name = "Stainless steel paperclips, small, 1000pcs";
        product.ean = 1234L;
        product.description = "1000 blue paperclips.";

        StockItem item = new StockItem();
        item.quantity = 15L;
        item.product = product;
        item.warehouse = warehouse;
        warehouse.stock.add(item);
        List<String> output = new LinkedList<String>();

        output.add(format("My warehouse is called '%s'", warehouse));
        output.add(format("It contains %d %s", warehouse.stock.size(), warehouse.stock.size() == 1 ? "item" : "items"));
        output.add(format("The first is: %s", warehouse.stock.get(0)));
        return ok(output.toString());
    }


    public static Result submit() {
        return ok(submit.render());
    }

    public static Result search() {
        return ok(search.render());
    }

    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart sample = body.getFile("sample");
        if (sample != null) {
//            String fileName = picture.getFilename();
//            String contentType = picture.getContentType();
//            File file = picture.getFile();
            return ok("File uploaded");
        } else {
            flash("error", "Missing file");
            return redirect(routes.Application.index());
        }
    }

    public static Result doSubmit() {
        return async(
                WS.url("http://192.168.251.139:8090/tasks/list").get().map(
                        new Function<WS.Response, Result>() {
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