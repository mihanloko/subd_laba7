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
import java.sql.ResultSet;
import java.sql.Statement;

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
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select \"Client_PK_client_seq\".nextval");

        }
        catch (Exception e) {
            log.error("Ошибка добавления товара", e);
        }

        attributes.addAttribute("pk", pk);
        return "redirect:home";
    }

}
