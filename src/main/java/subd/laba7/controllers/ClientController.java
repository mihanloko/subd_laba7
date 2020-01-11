package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import subd.laba7.database.BDConnection;
import subd.laba7.entities.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
@Slf4j
public class ClientController {

    @RequestMapping(value = "client/check", method = RequestMethod.GET)
    public String checkProductStatus(@RequestParam(name = "id", required = false, defaultValue = "") String id,
                                     Model model) {

        Connection connection = BDConnection.getConnection();

        Product product = null;

        if (!id.isEmpty()) {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("select  a.\"Nomer\", t.\"Naim\", s.\"Status\", z.\"Data_oformlenya\"\n " +
                        "from \"Tovar\" t, \"Akt_o_prinyatia_tovar\" a, \"Status_tovar\" s, \"Zayavlenie_na_rem_tovar\" z\n " +
                        "where t.\"PK_tovar\" = a.\"PK_tovar\" and t.\"PK_status_tovar\" = s.\"PK_status_tovar\" " +
                        "and z.\"PK_tovar\" = t.\"PK_tovar\" and a.\"Nomer\" = ?;");
                statement.setString(1, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    product = new Product();
                    product.setName(resultSet.getString("Naim"));
                    product.setDate(resultSet.getString("Data_oformlenya"));
                    product.setNumber(resultSet.getString("Nomer"));
                    product.setStatus(resultSet.getString("Status"));
                }
            } catch (SQLException e) {
                log.error("Ошибка при подготовке запроса на состояние товара", e);
            }
        }

        model.addAttribute("product", product);
        return "clientPage";
    }

}
