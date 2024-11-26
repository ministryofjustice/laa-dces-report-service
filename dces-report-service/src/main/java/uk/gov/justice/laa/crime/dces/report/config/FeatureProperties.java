package uk.gov.justice.laa.crime.dces.report.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Feature flags should be defined with a canonical name that is in lowercase kebab-case, like `my-good-name`.
 * Following this convention and not deviating from it should lead to consistent naming of the bean properties and
 * operating system environment variables.
 * <p>
 * The feature flag will be exposed as a method on the `Feature` record as a method in camelCase, like `myGoodName`.
 * So a component can inject the `Feature` and use `if (feature.myGoodName()) {...}` to test for the feature.
 * <p>
 * Additionally, the annotation `@ConditionalOnProperty(prefix = "feature", name = "my-good-name")` (note the use
 * of the lowercase kebab-case name) can be used on a component class or configuration bean method to instantiate that
 * Spring context bean only if the feature is enabled.
 * <p>
 * Inside Kubernetes, users enable and disable features by editing the `feature` secret in the appropriate namespace.
 * You can do this in AWS Console Secrets Manager, find the secret by its description, and then edit the key-values
 * inside the secret in AWS. Each key name will be an environment variable, so they should be uppercase without dashes.
 * 'my-good-name` will map to `FEATURE_MYGOODNAME` in the secrets and in the environment. Use `true` or `false` as the
 * values. You may need to delete the Kubernetes secret and pod to get them to refresh/restart with the new values.
 * <p>
 * Outside of Kubernetes (for example, on your local development machine), you can normally just define an environment
 * variable using the uppercase without dashes name in the Run/Debug configuration, like `FEATURE_MYGOODNAME=true`.
 * <p>
 * Note: Spring Boot's rules for environment variables are strict to make valid names on Windows, macOS and Linux (see
 * <a href="https://docs.spring.io/spring-boot/reference/features/external-config.html#features.external-config.typesafe-configuration-properties.relaxed-binding.environment-variables">here</a>
 * for more details). The rules for properties in this class are more relaxed, but stick to camelCase for clarity.
 * <p>
 * Keep it simple with feature naming, just a few dash-separated words after `feature.` - otherwise you may hit one of
 * <a href="https://github.com/spring-projects/spring-boot/issues?q=is%3Aissue+ConfigurationProperties+environment+variables+dashes">these issues</a>.
 * If you plan to use `@Value` binding, then keeping it to a single word after `feature.` is advisable.
 *
 * @param runDailyReport   initialize the `feature.run-daily-report` (`FEATURE_RUNDAILYREPORT`) feature flag.
 *                           This enables the daily reports.
 */
@ConfigurationProperties(prefix = "feature")
public record FeatureProperties(boolean runDailyReport   /* feature.run-daily-report */) {
}