package de.diakonie.onlineberatung.authenticator;

import static de.diakonie.onlineberatung.keycloak_otp_config_spi.keycloakextension.generated.web.model.OtpType.APP;

import de.diakonie.onlineberatung.keycloak_otp_config_spi.keycloakextension.generated.web.model.Challenge;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.authenticators.directgrant.AbstractDirectGrantAuthenticator;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

public class OtpParameterAuthenticator extends AbstractDirectGrantAuthenticator {

  public static final String ID = "otp-parameter-authenticator";
  final AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
      AuthenticationExecutionModel.Requirement.REQUIRED};

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public void authenticate(AuthenticationFlowContext context) {
    MultivaluedMap<String, String> inputData = context.getHttpRequest().getDecodedFormParameters();
    String otp = inputData.getFirst("otp");
    otp = (otp == null) ? inputData.getFirst("totp") : otp;

    if (otp == null) {
      Challenge challengeResponse = new Challenge().error("invalid_grant")
          .errorDescription("Missing totp").otpType(APP);
      context.failure(AuthenticationFlowError.INVALID_CREDENTIALS,
          Response.status(Status.BAD_REQUEST).entity(challengeResponse)
              .type(MediaType.APPLICATION_JSON_TYPE).build());
      return;
    }
    context.success();
  }

  @Override
  public boolean requiresUser() {
    return false;
  }

  @Override
  public boolean configuredFor(KeycloakSession keycloakSession, RealmModel realmModel,
      UserModel userModel) {
    return true;
  }

  @Override
  public void setRequiredActions(KeycloakSession keycloakSession, RealmModel realmModel,
      UserModel userModel) {
    // Nothing to do here because configuredFor always returns true
  }

  @Override
  public String getDisplayType() {
    return "OTP parameter validator";
  }

  @Override
  public String getReferenceCategory() {
    return null;
  }

  @Override
  public boolean isConfigurable() {
    return false;
  }

  @Override
  public Requirement[] getRequirementChoices() {
    return REQUIREMENT_CHOICES;
  }

  @Override
  public boolean isUserSetupAllowed() {
    return false;
  }

  @Override
  public String getHelpText() {
    return "Validates if the otp parameter is set when OTP is enabled for the user";
  }

  @Override
  public List<ProviderConfigProperty> getConfigProperties() {
    return Collections.emptyList();
  }
}