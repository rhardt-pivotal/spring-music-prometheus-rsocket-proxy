Spring Music for Prometheus RSocket Proxy
==========================================

This is a version of the [Spring Music Cloud Foundry App](https://github.com/cloudfoundry-samples/spring-music) customized to interact with the [Prometheus RSocket Proxy](https://github.com/micrometer-metrics/prometheus-rsocket-proxy).

The instructions included here are for running both Spring Music and the RSocket Proxy on the same Pivotal Application Service foundation and having them communicate via c2c networking.
This could be adapted to Kubernetes (with NodePorts or TCP Ingress) or PAS TCP Routing with minimal additional work. 


## Deploy Prometheus RSocket Proxy

First we need to have an instance of [Prometheus RSocket Proxy](https://github.com/micrometer-metrics/prometheus-rsocket-proxy) running on PAS.
 
  

~~~
$ git clone https://github.com/micrometer-metrics/prometheus-rsocket-proxy
$ cd prometheus-rsocket-proxy
$ ./gradlew clean build
$ curl -O https://gist.githubusercontent.com/rhardt-pivotal/1e760862e4a2a4c722be6292b1af64f0/raw/b8dd7f5f03826a81037e00d51b9a42c90b88e5e9/manifest.yml
$ # edit manifest.yml.  Change "<your-cf-apps-domain>" to your cf apps domain.
$  cf push
~~~

At this point you should be able to point your browser to https://prom-proxy.your.cf.apps.domain/metrics/proxy and see some Prometheus-formatted metrics.  
These are the metrics for the proxy app itself.
 

## Deploy Spring Music

Now you should be able to deploy this app and point it to the proxy app you deployed in the previous step.
From the root dir of this project...
* If you changed the 'apps.internal' route of the proxy app in the previous step, edit `src/resources/application-cloud.yml` to reflect the new path.
~~~
$ cd spring-music-prometheus-rsocket-proxy
$ ./gradlew clean build
$ cf push  # manifest.yml specifies 3 instances
~~~

At this point, your app is running alongside the proxy and trying to connect via RSocket, but CFAR/PAS is likely preventing east-west network communication with it's restrictive default network policy.  
Add a network policy to allow communication.

~~~
$ cf add-network-policy spring-music --destination-app prom-proxy --protocol tcp --port 7001
~~~

Now you should be able to view https://prom-proxy.your.cf.apps.domain/metrics/proxy and see metrics for all 3 instances of Spring Music. 

## Customizing Metrics
The `app-name` and `instance_id` metrics are added in the `src/main/java/org/cloudfoundry/samples/music/config/RSocketPrometheusConfig.java` file.  You can add other custom metrics here as well.
