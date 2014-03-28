@GrabResolver(name='Spring Snapshot', root='http://repo.spring.io/snapshot')
@Grab('org.springframework.cloud:spring-service-connector:0.9.6.BUILD-SNAPSHOT')
@Grab('org.springframework.cloud:cloudfoundry-connector:0.9.6.BUILD-SNAPSHOT')
@Grab('org.thymeleaf:thymeleaf-spring4:2.1.2.RELEASE')

import org.springframework.cloud.Cloud
import org.springframework.cloud.CloudFactory
import javax.servlet.http.HttpServletRequest
import groovy.util.logging.Commons

beans {
	cloudFactory(CloudFactory)

	cloud(cloudFactory: "getCloud")
}

@Controller
@Configuration
@Commons
class WebApplication implements CommandLineRunner {

	int requestsServed

	@Autowired
	Cloud cloud

	@RequestMapping("/")
	String home(Map<String,Object> model, HttpServletRequest request) {
		requestsServed++
		model['instance'] = cloud.applicationInstanceInfo.properties['instance_index']
		model['port'] = cloud.applicationInstanceInfo.properties['port']
		model['ipAddress'] = request.localAddr
		model['applicationName'] = cloud.applicationInstanceInfo.properties['application_name']
		model['memory'] = cloud.applicationInstanceInfo.properties['limits']['mem']
		model['disk'] = cloud.applicationInstanceInfo.properties['limits']['disk']
		model['requestsServed'] = requestsServed
		return "index"
	}

	@RequestMapping("/killSwitch")
    void die() {
        log.fatal("KILL SWITCH ACTIVATED!")
        System.exit(1)
    }

	@Override
    void run(String... args) {
        println "Started..."
    }

}