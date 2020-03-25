package org.fjun.api;

import org.fjun.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserApi {

    @Autowired
    private UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public boolean login(@RequestParam String name, @RequestParam String passwd) {
        return userService.login(name, passwd);
    }

    @RequestMapping("/validate_name")
    @ResponseBody
    public boolean validateName(@RequestParam String name) {
        return userService.validate(name);
    }

}
