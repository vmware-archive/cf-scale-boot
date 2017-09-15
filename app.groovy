@Grab('org.springframework.cloud:spring-cloud-spring-service-connector:1.1.1.RELEASE')
@Grab('org.springframework.cloud:spring-cloud-cloudfoundry-connector:1.1.1.RELEASE')
@Grab(group='org.springframework.boot', module='spring-boot-starter-actuator', version='1.1.9.RELEASE')
@Grab(group='org.springframework.boot', module='spring-boot-starter-thymeleaf', version='1.1.9.RELEASE')

import org.springframework.cloud.Cloud
import org.springframework.cloud.CloudFactory
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
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

	@Value('${cfscaleboot.flushout:false}')
	boolean flushOut = false

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
	void die(HttpServletResponse response) {
		if(flushOut) {
			log.fatal("FLUSHING BEFORE KILL!")
			response.setStatus(HttpServletResponse.SC_OK)
			response.getWriter().println("<html><body><h1>KILL SWITCH ENGAGED</h1></body></html>")
			response.getWriter().flush()
		}
		log.fatal("KILL SWITCH ACTIVATED!")
		System.exit(1)
	}

	@Override
	void run(String... args) {
		println "Started..."
	}

}
