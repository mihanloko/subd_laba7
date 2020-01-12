package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import subd.laba7.database.BDConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;

@Controller
@Slf4j
public class AdminController {
    private static final int CONSULTANT = 0, EXPERT = 1, REPAIR = 2;

    @RequestMapping(value = "admin/add")
    public String addPage() {
        return "registrationPage";
    }

    @RequestMapping(value = "admin/adding", method = RequestMethod.POST)
    public String adding(@RequestParam(name = "fio") String fio,
                            @RequestParam(name = "number") String number,
                            @RequestParam(name = "role") Integer role) {

        Connection connection = BDConnection.getConnection();

        try {
            PreparedStatement statement = null;
            switch (role) {
                case CONSULTANT:
                    statement = connection.prepareStatement("select insert_konsultant(?, ?)");
                    break;
                case EXPERT:
                    statement = connection.prepareStatement("select insert_expert(?, ?)");
                    break;
                case REPAIR:
                    statement = connection.prepareStatement("select insert_remontnik(?, ?)");
                    break;
                default:
                    return "redirect:add";
            }
            statement.setString(1, fio);
            statement.setString(2, number);
            statement.execute();
        }
        catch (Exception e) {
            log.error("Ошибка при добавлении сотрудника", e);
        }
        return "redirect:add";
    }



}
