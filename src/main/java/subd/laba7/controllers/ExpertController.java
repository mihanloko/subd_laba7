package subd.laba7.controllers;

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
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
public class ExpertController {

    /**
     * Посмотреть товары которые надо проэкспектировать - список товаров,
     * здесь можно будет выбрать товар для проведения экспертизы *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/check", method = RequestMethod.GET)
    public String checkProductStatus(@RequestParam(name = "id", required = false, defaultValue = "") String id,
                                     Model model) {

        Connection connection = BDConnection.getConnection();


        List<Product> products= new ArrayList<>();

        if (!id.isEmpty()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("select * from get_products_by_status(1);");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setName(resultSet.getString("id"));
                    product.setDate(resultSet.getString("Data_oformlenya"));
                    product.setNumber(resultSet.getString("zavod_number"));
                    product.setName(resultSet.getString("naim"));
                    products.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }
        // в результате получили список products - его надо запихать в таблицу
        return "clientPage";
    }

    /**
     * Забрать на экспертизу - для изменения статуса товара
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/check", method = RequestMethod.GET)
    public String takeProduct(@RequestParam(name = "id", required = false, defaultValue = "") String id,
                                     Model model) {
        Connection connection = BDConnection.getConnection();
        if (!id.isEmpty()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("select take_product_for_expertise(4);");
                statement.executeQuery();
                statement = connection.prepareStatement(" insert into \"Expert_otchet_o_tovar\" (\"PK_expertn_otcheta_o_tovar\", \"PK_experta\", \"PK_tovar\")\n" +
                        " values (default, ?, ?);");
                statement.setString(1, "1"); // todo где то будет взят из глобальной области pk_experta
                statement.setString(2, id); // PK_TOVAR
                statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }
        // в результате изменили статус товара
        return "clientPage";
    }

    /**
     * Посмотреть товары, которые за ним числятся - список товаров, здесь можно
     * будет выбрать товар для дальнейшего оформления отчета *
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/check", method = RequestMethod.GET)
    public String checkProductTodo(@RequestParam(name = "id", required = false, defaultValue = "") String id,
                                     Model model) {

        Connection connection = BDConnection.getConnection();


        List<Product> products= new ArrayList<>();

        if (!id.isEmpty()) {
            PreparedStatement statement = null;
            try {
                //todo здесь должен передавать pk_experta
                statement = connection.prepareStatement("select * from get_products_experta(1);");
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Product product = new Product();
                    product.setName(resultSet.getString("id"));
                    product.setDate(resultSet.getString("Data_oformlenya"));
                    product.setNumber(resultSet.getString("zavod_number"));
                    product.setName(resultSet.getString("naim"));
                    products.add(product);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }
        // в результате получили список products - его надо запихать в таблицу
        return "clientPage";
    }

    /**
     * Занести результаты экспертизы
     * @param id
     * @param model
     * @return
     */
    @RequestMapping(value = "expert/check", method = RequestMethod.GET)
    public String takeProductForOtchet(@RequestParam(name = "id", required = false, defaultValue = "") String id,
                              Model model) {
        Connection connection = BDConnection.getConnection();
        if (!id.isEmpty()) {
            PreparedStatement statement = null;
            try {
                //statement = connection.prepareStatement("select * from \"Expert_otchet_o_tovar\" e where e.\"PK_tovar\" = ?;");
               // statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }
        // resultSet - здесь будут все поля необходимые для заполнения формы отчета
        return "clientPage";
    }
}
