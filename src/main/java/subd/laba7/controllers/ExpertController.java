package subd.laba7.controllers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import subd.laba7.database.BDConnection;
import subd.laba7.entities.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


@Controller
@Slf4j
public class ExpertController {

    @RequestMapping(value = "expert/main", method = RequestMethod.GET)
    public String mainExpertPAge(@RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                                     Model model) {
        model.addAttribute("pk", pk);
        return "expert/expertMainPage";
    }

    /**
     * Посмотреть товары которые надо проэкспектировать - список товаров,
     * здесь можно будет выбрать товар для проведения экспертизы *
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/check", method = RequestMethod.GET)
    public String checkProductStatus(@RequestParam(name = "pk_tovara", required = false, defaultValue = "") String pk_tovara,
                                     @RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                                     Model model) {
        Connection connection = BDConnection.getConnection();
        List<Product> products= new ArrayList<>();
        PreparedStatement statement = null;
        if (!pk_tovara.equals("")) {
            try {
                int int_pk_tovara = Integer.parseInt(pk_tovara);
                int int_pk = Integer.parseInt(pk);
                statement = connection.prepareStatement("select take_product_for_expertise(?);");
                statement.setInt(1, int_pk_tovara);
                statement.execute();
                statement = connection.prepareStatement(" insert into \"Expert_otchet_o_tovar\" (\"PK_expertn_otcheta_o_tovar\", \"PK_experta\", \"PK_tovar\") values (default, ?, ?);");
                statement.setInt(1, int_pk); // todo где то будет взят из глобальной области pk_experta
                statement.setInt(2, int_pk_tovara); // PK_TOVAR
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }
        try {
            statement = connection.prepareStatement("select * from get_products_by_status(1);");
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
            e.printStackTrace();
            log.error("Ошибка при подготовке запроса на состояние товара", e);
        }
        // в результате получили список products - его надо запихать в таблицу
        model.addAttribute("listProducts", products);
        model.addAttribute("pk", pk);
        return "expert/expertPage";
    }

    /**
     * Посмотреть товары, которые за ним числятся - список товаров, здесь можно
     * будет выбрать товар для дальнейшего оформления отчета *
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/checkProductTodo", method = RequestMethod.GET)
    public String checkProductTodo(@RequestParam(name = "pk_tovara", required = false, defaultValue = "") String pk_tovara,
                                   @RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                                   Model model) {

        Connection connection = BDConnection.getConnection();
        List<Product> products= new ArrayList<>();
        PreparedStatement statement = null;

        int int_pk = Integer.parseInt(pk);
        if (!pk_tovara.equals("")) {
            try {
                // перейти на страницу для заполнения данных
                int int_pk_tovara = Integer.parseInt(pk_tovara);
                statement = connection.prepareStatement("select take_product_for_expertise(?);");
                statement.setInt(1, int_pk_tovara);
                statement.execute();
                statement = connection.prepareStatement(" insert into \"Expert_otchet_o_tovar\" (\"PK_expertn_otcheta_o_tovar\", \"PK_experta\", \"PK_tovar\") values (default, ?, ?);");
                statement.setInt(1, int_pk); // todo где то будет взят из глобальной области pk_experta
                statement.setInt(2, int_pk_tovara); // PK_TOVAR
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }
        try {
            statement = connection.prepareStatement("select * from get_products_experta(?);");
            statement.setInt(1, int_pk);
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
            e.printStackTrace();
            log.error("Ошибка при подготовке запроса на состояние товара", e);
        }
        model.addAttribute("listProducts", products);
        model.addAttribute("pk", pk);
        // в результате получили список products - его надо запихать в таблицу
        return "expert/expertPage2";
    }

    /**
     * Занести результаты экспертизы
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/takeProductForOtchet", method = RequestMethod.GET)
    public String takeProductForOtchet(@RequestParam(name = "pk_tovara", required = false, defaultValue = "") String pk_tovara,
                                       @RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                                       @RequestParam(name = "numberOtchet", required = false, defaultValue = "") String numberOtchet,
                                       @RequestParam(name = "dataOform", required = false) Date dataOform,
                                       @RequestParam(name = "defects", required = false, defaultValue = "") String defects,
                                       Model model) {
        Connection connection = BDConnection.getConnection();
        int int_pk = Integer.parseInt(pk);
        int int_pk_tovara = Integer.parseInt(pk_tovara);

        if (!(numberOtchet.equals("") && dataOform == null && defects.equals(""))) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("select update_expert_otchet(?, ?, ?, ?, ?);");
                statement.setInt(1, int_pk_tovara);
                statement.setInt(2, int_pk);
                statement.setString(3, numberOtchet);
                statement.setDate(4, dataOform);
                statement.setString(5, defects);
                statement.execute();

                statement = connection.prepareStatement("update \"Tovar\" set \"PK_status_tovar\" = 6 where \"PK_tovar\" = ?;");
                statement.setInt(1, int_pk_tovara);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
            // resultSet - здесь будут все поля необходимые для заполнения формы отчета
            model.addAttribute("pk_tovara", pk_tovara);
            model.addAttribute("pk", pk);
            return "redirect:main";
        }
        model.addAttribute("pk_tovara", pk_tovara);
        model.addAttribute("pk", pk);
        return "expert/expertOtchetForm";
    }
}
