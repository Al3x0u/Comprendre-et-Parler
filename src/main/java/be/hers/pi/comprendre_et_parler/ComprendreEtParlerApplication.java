package be.hers.pi.comprendre_et_parler;

import be.hers.pi.comprendre_et_parler.DAOs.DatabaseConnector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@SpringBootApplication
public class ComprendreEtParlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComprendreEtParlerApplication.class, args);
    }

    @PostConstruct
    public void init(){
        DatabaseConnector.initialize();
    }

    @PreDestroy
    public void cleanup() throws SQLException {
        DatabaseConnector.closeInstance();
    }
}
