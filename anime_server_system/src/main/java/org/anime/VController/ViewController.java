package org.anime.VController;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpSession;

@Controller
public class ViewController {

    @Autowired
    private org.anime.mapper.userMapper userMapper;

    @GetMapping("/view/index")
    public String index(HttpSession session) {
        return "index";
    }

    @GetMapping("/view/login")
    public String login() {
        return "login";
    }

    @GetMapping("/view/register")
    public String register() {
        return "register";
    }

    @GetMapping("/view/recommendation")
    public String recommendation() {
        return "recommendation";
    }

    @GetMapping("/view/list")
    public String list() {
        return "list";
    }

    @GetMapping("/view/chat")
    public String chat() {
        return "chat";
    }

    @GetMapping("/view/anime/{id}")
    public String anime(@PathVariable int id) {
        return "anime";
    }


}
