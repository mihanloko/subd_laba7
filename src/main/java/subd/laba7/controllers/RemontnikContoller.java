package subd.laba7.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
public class RemontnikContoller {

    @RequestMapping(value = "remontnik", method = RequestMethod.GET)
    String home(@RequestParam(name = "pk", defaultValue = "") String pk, Model model) {
        model.addAttribute("pk", pk);
        return "remontnik/home";
    }

    @RequestMapping(value = "remontnik/todo", method = RequestMethod.GET)
    String listWorks() {
        return "remontnik/listWorks";
    }
}
