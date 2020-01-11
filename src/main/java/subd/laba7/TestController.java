package subd.laba7;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.sql.*;
import java.util.ArrayList;

@Controller
public class TestController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String test(Model model) {

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://kozlov.pro:5432/obd",
                    "obd", "obd");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from \"Tovar\"");
            ArrayList<LineClass> lines = new ArrayList<>();
            while (resultSet.next()) {
                lines.add(new LineClass(resultSet.getString(1), resultSet.getString(4)));
            }
            model.addAttribute("lines", lines);

        } catch (SQLException e) {
            System.out.println("произошла какая то хуйня");
            System.out.println(e);
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        model.addAttribute("name", "world");
        model.addAttribute("formClass", new FormClass());
        return "mainPage";
    }

    @RequestMapping(value = "/formData", method = RequestMethod.POST)
    public String form(FormClass formClass) {
        System.out.println(formClass);
        return "redirect:/";
    }

}
