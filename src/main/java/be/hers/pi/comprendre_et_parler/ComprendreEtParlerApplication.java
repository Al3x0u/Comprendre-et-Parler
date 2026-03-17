package be.hers.pi.entendre_et_parler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ComprendreEtParlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComprendreEtParlerApplication.class, args);
    }
    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("<html><body><h1>Hello %s!</h1></body></html>", name);
    }

}
