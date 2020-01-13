package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import subd.laba7.database.BDConnection;

import java.sql.*;
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

}
