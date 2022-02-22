package se.edinjakupovic;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.NonNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class WireMockInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    public static final WireMockServer WIRE_MOCK;

    static {
        WIRE_MOCK = new WireMockServer(new WireMockConfiguration().dynamicPort());
        WIRE_MOCK.start();
    }

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(Map.of("receiver.baseurl", WIRE_MOCK.baseUrl()))
                .applyTo(applicationContext);
    }

}
