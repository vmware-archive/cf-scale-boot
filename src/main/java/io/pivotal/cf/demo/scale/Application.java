package io.pivotal.cf.demo.scale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@Controller
@Configuration
@EnableAutoConfiguration
public class Application {

    Log log = LogFactory.getLog(Application.class);

    int requestsServed;

    @Bean
    public Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @RequestMapping("/")
    String home(Map<String, Object> model, HttpServletRequest request) {
        requestsServed++;
        model.put("dyno", cloud().getApplicationInstanceInfo().getProperties().get("dyno"));
        model.put("port", cloud().getApplicationInstanceInfo().getProperties().get("port"));
        model.put("ipAddress", request.getLocalAddr());
//        model.put("applicationName", cloud().getApplicationInstanceInfo().getProperties().get("application_name"));

//        @SuppressWarnings("unchecked")
//        Map<String, Object> limits = (Map<String, Object>) cloud().getApplicationInstanceInfo().getProperties().get("limits");

//        model.put("memory", limits.get("mem"));
//        model.put("disk", limits.get("disk"));
        model.put("requestsServed", requestsServed);
        return "index";
    }

    @RequestMapping("/killSwitch")
    void die() {
        log.fatal("KILL SWITCH ACTIVATED!");
        System.exit(1);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }


}