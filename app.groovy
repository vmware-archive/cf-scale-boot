@Grab('io.pivotal.cfenv:java-cfenv-boot:1.0.1.RELEASE')
@Grab(group='org.springframework.boot', module='spring-boot-starter-actuator', version='2.1.8.RELEASE')
@Grab(group='org.springframework.boot', module='spring-boot-starter-thymeleaf', version='2.1.8.RELEASE')

import io.pivotal.cfenv.core.CfEnv
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import groovy.util.logging.Commons

beans {
	cfEnv(CfEnv)
}

@Controller
@Configuration
@Commons
class WebApplication implements CommandLineRunner {

	int requestsServed

	@Value('${cfscaleboot.flushout:false}')
	boolean flushOut = false

	@Autowired
	CfEnv cfEnv;

	@RequestMapping("/")
	String home(Map<String,Object> model, HttpServletRequest request) {
		requestsServed++
		model['instance'] = cfEnv.app.instanceIndex
		model['port'] = cfEnv.app.port
		model['ipAddress'] = request.localAddr
		model['applicationName'] = cfEnv.app.applicationName
		model['memory'] = cfEnv.app.map.limits.mem
		model['disk'] = cfEnv.app.map.limits.disk
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
