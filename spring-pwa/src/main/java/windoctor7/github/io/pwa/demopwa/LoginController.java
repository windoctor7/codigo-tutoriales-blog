package windoctor7.github.io.pwa.demopwa;

import org.progressify.spring.annotations.StaleWhileRevalidate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @StaleWhileRevalidate
    @GetMapping("/login")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="Ascari") String name, Model model) {
        model.addAttribute("name", name);
        return "login";
    }

}