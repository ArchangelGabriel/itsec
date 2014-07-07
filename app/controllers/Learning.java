package controllers;

import models.h2.Product;
import models.h2.StockItem;
import org.apache.commons.lang3.StringUtils;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.learn;
import views.html.list;
import views.html.show;

import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static play.data.Form.form;


public class Learning extends Controller {

    private static final Form<Product> productForm = form(Product.class);

    public static Result list(Long warehouseId) {
        List<StockItem> items = StockItem.find()
                .where()
                .eq("warehouse_id", warehouseId)
                .orderBy("quantity desc")
                .setMaxRows(3)
                .findList();

        if(items != null) {
//            return ok(StringUtils.join(items, "\n"));
            if (request().accepts("text/plain")) {
                return ok(StringUtils.join(items, "\n"));
            }
            return ok(learn.render(items));
        } else {
            return notFound("No warehouse with id " + warehouseId);
        }
    }

    public static Result showBlank() {
        return ok(show.render(productForm));
    }
    public static Result show(Long ean) {
        final Product product = Product.findByEan(ean);
        if(product == null) {
            return notFound(format("Product %s does not exist", ean));
        }

        Form<Product> filledForm = productForm.fill(product);
        return ok(show.render(filledForm));
    }
    public static Result save() {
        Form<Product> boundForm = productForm.bindFromRequest();
        if(boundForm.hasErrors()) {
            flash("error", "Please correct the form below.");
            return badRequest(show.render(boundForm));
        }

        Product product = boundForm.get();
        Product.add(product);
        flash("success", format("Successfully added product %s", product));
        return ok("YES");
    }

}
