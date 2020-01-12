package subd.laba7.controllers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import subd.laba7.database.BDConnection;
import subd.laba7.entities.LineClass;

import java.sql.*;
import java.util.ArrayList;

@Controller
@Slf4j
public class TestController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String test(Model model) throws SQLException {
        Connection connection = BDConnection.getConnection();

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from \"Tovar\"");
            ArrayList<LineClass> lines = new ArrayList<>();
            while (resultSet.next()) {
                lines.add(new LineClass(resultSet.getString(1), resultSet.getString(4)));
            }
            model.addAttribute("lines", lines);

        model.addAttribute("name", "world");
        return "mainPage";
    }

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public String testParams(@RequestParam(name = "pk") String pk) {
        log.info(pk);
        return "mainPage";
    }
}
