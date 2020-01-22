package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import subd.laba7.database.BDConnection;
import subd.laba7.entities.Product;
import subd.laba7.entities.ProductInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class RemontnikContoller {

    @RequestMapping(value = "remontnik", method = RequestMethod.GET)
    String home(@RequestParam(name = "pk", defaultValue = "") String pk, Model model) {
        model.addAttribute("pk", pk);
        return "remontnik/home";
    }

    @RequestMapping(value = "remontnik/productInfo", method = RequestMethod.GET)
    String tovarInfo(@RequestParam(name = "pk", defaultValue = "") String pk,
                     @RequestParam(name = "pk_tovar", defaultValue = "") String pk_tovar,
                     Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        Product product= new Product();
        boolean productFound = false;

        try {
            int int_pk_tovar = Integer.parseInt(pk_tovar);
            statement = connection.prepareStatement("select * from \"Tovar\" where \"PK_tovar\" = ?;");
            statement.setInt(1, int_pk_tovar);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                productFound = true;
                product.setId(resultSet.getString("PK_tovar"));
                product.setNumber(resultSet.getString("Zavodsokoi_nomer"));
                product.setName(resultSet.getString("Naim"));
            }

            statement = connection.prepareStatement("select * from \"Akt_o_prinyatia_tovar\" where \"PK_tovar\" = ?;");
            statement.setInt(1, int_pk_tovar);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                model.addAttribute("productDefect", resultSet.getString("Podrobn_opisan_obl_probl"));
            }

            ArrayList<ProductInfo> listOtch  = new ArrayList<>();

            statement = connection.prepareStatement("select * from \"Expert_otchet_o_tovar\" where \"PK_tovar\" = ?;");
            statement.setInt(1, int_pk_tovar);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                ProductInfo p = new ProductInfo();
                p.setDate(resultSet.getString("Data_oformlenia"));
                p.setInfo(resultSet.getString("Opisanie_defectov"));
                listOtch.add(p);
            }
            model.addAttribute("listOtch", listOtch);
        }
        catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("product", product);
        model.addAttribute("productFound", productFound);
        return "remontnik/productInfo";
    }

    @RequestMapping(value = "remontnik/todo", method = RequestMethod.GET)
    String listWorks(@RequestParam(name = "pk", defaultValue = "") String pk, Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        List<Product> products= new ArrayList<>();

        try {
            statement = connection.prepareStatement("select * from get_products_by_status(6);");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getString("id"));
                product.setDate(resultSet.getString("Data_oformlenya"));
                product.setNumber(resultSet.getString("zavod_number"));
                product.setName(resultSet.getString("naim"));
                products.add(product);
            }
        }
        catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("listProducts", products);
        return "remontnik/listWorks";
    }
}
