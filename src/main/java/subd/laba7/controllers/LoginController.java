package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import subd.laba7.database.BDConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@Controller
@Slf4j
public class LoginController {

    private static final int CONSULTANT = 0, EXPERT = 1, REPAIR = 2;

    @RequestMapping(value = "login")
    public String login(@RequestParam(name = "error", required = false, defaultValue = "-1") int error,
                        Model model) {
        if (error == -1)
            model.addAttribute("error", null);
        else {
            switch (error) {
                case 1:
                    model.addAttribute("error", "Неверный номер");
                    break;
                case 2:
                    model.addAttribute("error", "Невозможная роль");
                    break;
            }
        }
        return "enterPage";
    }

    @RequestMapping(value = "checkNumber", method = RequestMethod.POST)
    public String login(@RequestParam(name = "role") int role,
                        @RequestParam(name = "number") String number,
                        RedirectAttributes attributes) {
        Connection connection = BDConnection.getConnection();
        try {
            PreparedStatement statement = null;
            switch (role) {
                case CONSULTANT:
                    statement = connection.prepareStatement("select \"PK_kosultant\" " +
                            "from \"Konsultant\" where \"Tabelny_nomer\" = ?;");
                    break;
                case EXPERT:
                    statement = connection.prepareStatement("select \"PK_experta\" " +
                            "from \"Expert\" where \"Tabelny_nomer\" = ?;");
                    break;
                case REPAIR:
                    statement = connection.prepareStatement("select \"PK_remontnika\" " +
                            "from \"Remontnik\" where \"Tabelny_nomer\" = ?;");
                    break;
                default:
                    return "redirect:login?error=2";
            }
            statement.setString(1, number);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String pk = resultSet.getString(1);
                attributes.addAttribute("pk", pk);
                switch (role) {
                    case CONSULTANT:
                        return "redirect:cons";
                    case EXPERT:
                        return "redirect:exp";
                    case REPAIR:
                        return "redirect:rep";
                }
            }
            else {
                return "redirect:login?error=1";
            }
        }
        catch (Exception e) {
            log.error("Ошибка запроса", e);
        }

        return "redirect:enterPage";
    }

}
