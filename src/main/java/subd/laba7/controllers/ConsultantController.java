package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import subd.laba7.database.BDConnection;
import subd.laba7.entities.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



@Controller
@Slf4j
public class ConsultantController {

    @RequestMapping(value = "consultant/home", method = RequestMethod.GET)
    public String home(@RequestParam(name = "pk") String pk, Model model) {
        model.addAttribute("pk", pk);
        return "consultant/consultantHome";
    }

    @RequestMapping(value = "consultant/takeProduct", method = RequestMethod.GET)
    public String take(@RequestParam(name = "pk") String pk, Model model) {
        model.addAttribute("pk", pk);
        return "consultant/takingPage";
    }

    @RequestMapping(value = "consultant/takingProduct", method = RequestMethod.GET)
    public String takingProduct(@RequestParam(name = "pk") String pk,
                                @RequestParam(name = "fio") String fio,
                                @RequestParam(name = "number") String number,
                                @RequestParam(name = "naim") String naim,
                                @RequestParam(name = "dataStart") String dataStart,
                                @RequestParam(name = "zavNumber") String zavNumber,
                                @RequestParam(name = "prod") String prod,
                                @RequestParam(name = "inf") String inf,
                                @RequestParam(name = "passport") String passport,
                                @RequestParam(name = "dataProd") String dataProd,
                                @RequestParam(name = "sost") String sost,
                                @RequestParam(name = "problem") String problem,
                                RedirectAttributes attributes) {

        Connection connection = BDConnection.getConnection();

        try {
            String pkTov, pkClient;
            PreparedStatement statement = connection.prepareStatement("select insert_client(?, ?)");
            statement.setString(1, fio);
            statement.setString(2, number);
            statement.execute();
            Statement statement1 = connection.createStatement();
            ResultSet resultSet = statement1.executeQuery("select last_value from \"Client_PK_client_seq\";");
            resultSet.next();
            pkClient = resultSet.getString("last_value");

            statement = connection.prepareStatement("insert into \"Tovar\"(\"PK_tovar\", \"Naim\", \"Data_nachala_garantii\", \"Zavodsokoi_nomer\", \"Prodavez\", \"PK_status_tovar\") " +
                    "values(DEFAULT, ?, ?, ?, ?, 1)");
            statement.setString(1, naim);
            statement.setDate(2, Date.valueOf(dataProd));
            statement.setString(3, zavNumber);
            statement.setString(4, prod);
            statement.execute();

            resultSet = statement1.executeQuery("select last_value from \"Tovar_PK_tovar_seq\";");
            resultSet.next();
            pkTov = resultSet.getString("last_value");

            String aktNumber = String.valueOf(Math.abs((new Random()).nextInt()));

            statement = connection.prepareStatement
                    ("insert into \"Zayavlenie_na_rem_tovar\"(\"PK_zayvlene_na_removt\", \"Nomer\", \"Data_oformlenya\", \"PK_client\", \"PK_kosultant\", \"PK_tovar\") " +
                    "values (DEFAULT, ?, (select now()), ?, ?, ?);");
            statement.setString(1, aktNumber);
            statement.setInt(2, Integer.parseInt(pkClient));
            statement.setInt(3, Integer.parseInt(pk));
            statement.setInt(4, Integer.parseInt(pkTov));
            statement.execute();

            statement = connection.prepareStatement
                    ("insert into \"Akt_o_prinyatia_tovar\"(\"PK_akt_o_prin_tovar\", \"Nomer\", \"Poln_inform_o_izdel\", \"Kontaktn_dann_zayavitela\", \"Psportnye_dann_zayavitela\", \"Data_prodazhi\", \"Opisanie_sost_tovar\", \"Podrobn_opisan_obl_probl\", \"PK_kosultant\", \"PK_tovar\") " +
                    "values (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            statement.setString(1, aktNumber);
            statement.setString(2, inf);
            statement.setString(3, number);
            statement.setString(4, passport);
            statement.setDate(5, Date.valueOf(dataProd));
            statement.setString(6, sost);
            statement.setString(7, problem);
            statement.setInt(8, Integer.parseInt(pk));
            statement.setInt(9, Integer.parseInt(pkTov));
            statement.execute();

        }
        catch (Exception e) {
            log.error("Ошибка добавления товара", e);
        }

        attributes.addAttribute("pk", pk);
        return "redirect:home";
    }

    /**
     * Посмотреть товары которые надо вернуть - список товаров,
     * здесь можно будет выбрать товар и перейти к оформлению об акте возврата
     * @return
     */
    @RequestMapping(value = "consultant/takingProductAkt", method = RequestMethod.GET)
    public String checkProductStatus(@RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                                     Model model) {
        Connection connection = BDConnection.getConnection();
        List<Product> products= new ArrayList<>();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("select * from get_products_by_status(3);");
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
        return "consultant/tableResult";
    }



        @RequestMapping(value = "consultant/oformProductAktVozvrataFirst", method = RequestMethod.GET)
    public String takingProductOformAktFirst(
                @RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                @RequestParam(name = "pk_tovara", required = false, defaultValue = "") String pk_tovara,
                                             Model model) {
        model.addAttribute("pk", pk);
        model.addAttribute("pk_tovara", pk_tovara);
        return "consultant/oformAktVozvrata";
    }


    @RequestMapping(value = "consultant/oformProductAktVozvrataSecond", method = RequestMethod.GET)
    public String takingProductOformAktSecond(
                                @RequestParam(name = "pk", required = false, defaultValue = "") String pk,
                                @RequestParam(name = "pk_tovara", required = false, defaultValue = "") String pk_tovara,
                                @RequestParam(name = "number", required = false, defaultValue = "") String numberOtchet,
                                @RequestParam(name = "dataOform", required = false, defaultValue = "") String dataOform,
                                @RequestParam(name = "problem", required = false, defaultValue = "") String problem,
                                RedirectAttributes attributes) {

        Connection connection = BDConnection.getConnection();
        int int_pk = Integer.parseInt(pk);
        int int_pk_tovara = Integer.parseInt(pk_tovara);
        PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("insert into \"Akt_vosvrata_tovar\" (\"PK_akt_vozvrata\", \"Nomer\", \"Data\", \"Sostoyanie_tovar\", \"PK_kosultant\", \"PK_tovar\") values (default, ?, ?, ?, ?, ?);");
                statement.setString(1, numberOtchet);
                statement.setDate(2, Date.valueOf(dataOform));
                statement.setBoolean(3, problem.equals("on"));
                statement.setInt(4, int_pk);
                statement.setInt(5, int_pk_tovara);
                statement.execute();

                statement = connection.prepareStatement("select update_product_status(?, ?);");
                statement.setInt(1, int_pk_tovara);
                statement.setInt(2, 7);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
            // resultSet - здесь будут все поля необходимые для заполнения формы отчета
        attributes.addAttribute("pk", pk);
        return "redirect:home";
    }
}
