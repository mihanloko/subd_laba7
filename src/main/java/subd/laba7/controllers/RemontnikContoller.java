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
import subd.laba7.entities.Zapch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class RemontnikContoller {

    @RequestMapping(value = "remontnik", method = RequestMethod.GET)
    String home(@RequestParam(name = "pk", defaultValue = "") String pk,
                Model model) {
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

        Product product = new Product();
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

            ArrayList<ProductInfo> listOtch = new ArrayList<>();

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
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("product", product);
        model.addAttribute("productFound", productFound);
        return "remontnik/productInfo";
    }

    @RequestMapping(value = "remontnik/todo", method = RequestMethod.GET)
    String listWorks(@RequestParam(name = "pk", defaultValue = "") String pk,
                     Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        List<Product> products = new ArrayList<>();

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
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("listProducts", products);
        return "remontnik/listWorks";
    }

    @RequestMapping(value = "remontnik/setRem", method = RequestMethod.GET)
    String setRem(@RequestParam(name = "pk", defaultValue = "") String pk,
                  @RequestParam(name = "pk_tovar", defaultValue = "") String pk_tovar,
                  Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        try {
            int int_pk_tovar = Integer.parseInt(pk_tovar);
            statement = connection.prepareStatement("select take_product_for_repair(?);");
            statement.setInt(1, int_pk_tovar);
            statement.execute();
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        return "redirect:todo?pk=".concat(pk);
    }


    @RequestMapping(value = "remontnik/rem", method = RequestMethod.GET)
    String listRem(@RequestParam(name = "pk", defaultValue = "") String pk,
                   Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        List<Product> products = new ArrayList<>();

        try {
            statement = connection.prepareStatement("select * from get_products_by_status(5);");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getString("id"));
                product.setDate(resultSet.getString("Data_oformlenya"));
                product.setNumber(resultSet.getString("zavod_number"));
                product.setName(resultSet.getString("naim"));
                products.add(product);
            }
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("listProducts", products);
        return "remontnik/listRem";
    }

    @RequestMapping(value = "remontnik/finishRem", method = RequestMethod.GET)
    String tovarFinishRem(@RequestParam(name = "pk", defaultValue = "") String pk,
                          @RequestParam(name = "pk_tovar", defaultValue = "") String pk_tovar,
                          @RequestParam(name = "err", defaultValue = "") String err,
                          Model model) {
        model.addAttribute("pk", pk);
        model.addAttribute("err", err);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        Product product = new Product();
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
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("product", product);
        model.addAttribute("productFound", productFound);
        return "remontnik/finishRem";
    }

    @RequestMapping(value = "remontnik/finishRemSubmit", method = RequestMethod.POST)
    String tovarFinishRemSubmit(@RequestParam(name = "pk", defaultValue = "") String pk,
                                @RequestParam(name = "pk_tovar", defaultValue = "") String pk_tovar,
                                @RequestParam(name = "docnum", defaultValue = "") String docnum,
                                @RequestParam(name = "data", defaultValue = "") String data,
                                Model model) {

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        try {
            int int_pk_tovar = Integer.parseInt(pk_tovar);
            int int_pk = Integer.parseInt(pk);

            statement = connection.prepareStatement("insert into \"Remont_otchet_o_tovar\" (\"Nomer\", \"Data_oformlenia\", \"PK_tovar\", \"PK_remontnika\") values (?,?,?,?);");
            statement.setString(1, docnum);
            statement.setDate(2, Date.valueOf(data));
            statement.setInt(3, int_pk_tovar);
            statement.setInt(4, int_pk);
            statement.execute();

            statement = connection.prepareStatement("select update_product_status(?, ?);");
            statement.setInt(1, int_pk_tovar);
            statement.setInt(2, 3);
            statement.execute();
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
            return "redirect:finishRem?pk=".concat(pk).concat("&pk_tovar=").concat(pk_tovar).concat("&err=Invalid date");
        }
        return "redirect:rem?pk=".concat(pk);
    }

    @RequestMapping(value = "remontnik/zapch", method = RequestMethod.GET)
    String zapch(@RequestParam(name = "pk", defaultValue = "") String pk,
                 Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        List<Zapch> zapch = new ArrayList<>();

        try {
            statement = connection.prepareStatement("select * from \"Zapchast\"");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Zapch z = new Zapch();
                z.setId(resultSet.getString("PK_zapcasti"));
                z.setNaim(resultSet.getString("Naim"));
                z.setTip(resultSet.getString("Tip"));
                z.setCena(resultSet.getString("Cena"));
                zapch.add(z);
            }
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("listZapch", zapch);
        return "remontnik/zapch";
    }

    @RequestMapping(value = "remontnik/addZapch", method = RequestMethod.POST)
    String addZpach(@RequestParam(name = "pk", defaultValue = "") String pk,
                    @RequestParam(name = "tip", defaultValue = "") String tip,
                    @RequestParam(name = "naim", defaultValue = "") String naim,
                    @RequestParam(name = "cena", defaultValue = "") String cena,
                    Model model) {

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        try {
            statement = connection.prepareStatement("select insert_zapchast(?, ?, ?);");
            statement.setInt(1, Integer.parseInt(tip));
            statement.setString(2, naim);
            statement.setInt(3, Integer.parseInt(cena));
            statement.execute();
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
            return "redirect:zapch?pk=".concat(pk).concat("&err=Invalid adding");
        }
        return "redirect:zapch?pk=".concat(pk);
    }


    @RequestMapping(value = "remontnik/productZapch", method = RequestMethod.GET)
    String productZapch(@RequestParam(name = "pk", defaultValue = "") String pk,
                        @RequestParam(name = "pk_tovar", defaultValue = "") String pk_tovar,
                        Model model) {
        model.addAttribute("pk", pk);

        Connection connection = BDConnection.getConnection();
        PreparedStatement statement = null;

        List<Zapch> zapch = new ArrayList<>();

        try {
            statement = connection.prepareStatement("select * from \"Spisok_zapchastey\" s, \"Zapchast\" z where s.\"PK_zapcasti\" = z.\"PK_zapcasti\"");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Zapch z = new Zapch();
                z.setId(resultSet.getString("PK_zapcasti"));
                z.setNaim(resultSet.getString("Naim"));
                z.setTip(resultSet.getString("Tip"));
                z.setCena(resultSet.getString("Cena"));
                zapch.add(z);
            }
        } catch (SQLException e) {
            log.error("Ошибка при обработке запроса");
            e.printStackTrace();
        }
        model.addAttribute("listZapch", zapch);
        return "remontnik/zapch";
    }
}
