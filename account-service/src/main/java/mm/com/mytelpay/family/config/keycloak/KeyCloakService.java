package mm.com.mytelpay.family.config.keycloak;

import lombok.Getter;
import mm.com.mytelpay.family.business.BaseBusiness;
import mm.com.mytelpay.family.business.account.dto.keycloak.UpdateKeycloakInfoReqDTO;
import mm.com.mytelpay.family.exception.AccountErrorCode;
import mm.com.mytelpay.family.exception.error.BusinessEx;
import mm.com.mytelpay.family.exception.error.RequestEx;
import mm.com.mytelpay.family.model.entities.Account;
import mm.com.mytelpay.family.models.dto.CommonResponseDTO;
import mm.com.mytelpay.family.repo.AccountInfoRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;

@Service
@Getter
public class KeyCloakService extends BaseBusiness {

    private static final String NO_USER_KEYCLOAK_FOUND_WITH_MSISDN_MESSAGE = "No user found in keycloak with msisdn {}";
    private static final String REFRESH_TOKEN_STRING = "refresh_token";


    @Value("${keycloak.auth-server-url}")
    public String serverURL;
    @Value("${keycloak.realm}")
    public String realm;
    @Value("${keycloak.resource}")
    public String clientID;
    @Value("${keycloak.credentials.secret}")
    public String clientSecret;
    @Value("${kc.admin.username}")
    public String adminUsername;
    @Value("${kc.admin.password}")
    public String adminPassword;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private AccountInfoRepository accountInfoRepository;

    public KeycloakBuilder newKeycloakBuilderWithPasswordCredentials(String username, String password) {
        return KeycloakBuilder.builder()
                .realm(realm)
                .serverUrl(serverURL)
                .clientId(clientID)
                .clientSecret(clientSecret)
                .username(username)
                .password(password);
    }

    public ResponseEntity<CommonResponseDTO> refreshToken(String refreshToken, String requestId) {

        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", this.clientID);
        requestParams.add("client_secret", this.clientSecret);
        requestParams.add(REFRESH_TOKEN_STRING, refreshToken);
        requestParams.add("grant_type", REFRESH_TOKEN_STRING);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        String refreshTokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", this.serverURL, this.realm);
        try {
            CommonResponseDTO commonResponse = generateDefaultResponse(requestId, null);
            ResponseEntity<?> response = restTemplate.postForEntity(refreshTokenUrl, request, Object.class);
            Object obj = response.getBody();
            commonResponse.setResult(obj);
            return ResponseEntity.ok(commonResponse);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RequestEx(requestId, AccountErrorCode.Business.MAXIMUM_REFRESH_TOKEN, null);
        }
    }

    public void logoutByRefreshToken(String refreshToken) {
        MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("client_id", this.clientID);
        requestParams.add("client_secret", this.clientSecret);
        requestParams.add(REFRESH_TOKEN_STRING, refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String logoutUrl = String.format("%s/realms/%s/protocol/openid-connect/logout", this.serverURL, this.realm);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestParams, headers);
        try {
            restTemplate.postForEntity(logoutUrl, request, Object.class);
        } catch (Exception exception) {
            ExceptionUtils.getStackTrace(exception);
            logger.error("Logout keycloak failed");
        }
    }

    public void logoutByKeycloakId(String keycloakUserId) {
        if (StringUtils.isEmpty(keycloakUserId)) return;

        Keycloak keycloakAdmin = newKeycloakBuilderWithPasswordCredentials(adminUsername, adminPassword).build();
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        UserResource userResource = usersResource.get(keycloakUserId);
        userResource.logout();
    }

    public Response createKeycloakUser(String msisdn, String email, String fullName, String password) {
        CredentialRepresentation credentialRepresentation = createPasswordCredentials(password);
        Keycloak keycloakAdmin = newKeycloakBuilderWithPasswordCredentials(adminUsername,
                adminPassword).build();

        UserRepresentation newKeycloakUser = new UserRepresentation();
        newKeycloakUser.setUsername(msisdn);
        newKeycloakUser.setCredentials(Collections.singletonList(credentialRepresentation));
        newKeycloakUser.setEmail(email);
        newKeycloakUser.setEnabled(true);
        newKeycloakUser.setEmailVerified(true);
        setKeycloakName(fullName, newKeycloakUser);

        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        return usersResource.create(newKeycloakUser);
    }

    public boolean validateCurrentPassword(String msisdn, String currentPassword) {
        try {
            Keycloak keycloak = this.newKeycloakBuilderWithPasswordCredentials(msisdn,
                    currentPassword).build();
            keycloak.tokenManager().getAccessToken();
            return true;
        } catch (NotAuthorizedException e) {
            return false;
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public void changeKeycloakPassword(Account account, String newPassword) {
        CredentialRepresentation newCredentialRepresentation = createPasswordCredentials(newPassword);
        Keycloak keycloakAdmin = newKeycloakBuilderWithPasswordCredentials(adminUsername, adminPassword).build();
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        try {
            List<UserRepresentation> users = usersResource.search(account.getMsisdn());
            if (users.isEmpty()) {
                logger.error(NO_USER_KEYCLOAK_FOUND_WITH_MSISDN_MESSAGE, account.getMsisdn());
            } else {
                UserResource userResource = usersResource.get(users.get(0).getId());
                userResource.resetPassword(newCredentialRepresentation);
            }
        } catch (NotFoundException exception) {
            logger.error("User not found in keycloak with msisdn {}", account.getMsisdn());
            throw new BusinessEx(AccountErrorCode.ACCOUNT.CHANGE_PASS_UNSUCCESSFULLY, null);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new BusinessEx(AccountErrorCode.ACCOUNT.CHANGE_PASS_UNSUCCESSFULLY, null);
        }
    }

    public String getKeycloakUserIdByUsername(String username) {
        Keycloak keycloakAdmin = newKeycloakBuilderWithPasswordCredentials(adminUsername, adminPassword).build();
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        String keycloakUserId = "";
        try {
            List<UserRepresentation> users = usersResource.search(username);
            if (users.isEmpty()) {
                logger.error(NO_USER_KEYCLOAK_FOUND_WITH_MSISDN_MESSAGE, username);
            } else {
                keycloakUserId = users.get(0).getId();
            }
        } catch (NotFoundException exception) {
            throw new BusinessEx(AccountErrorCode.ACCOUNT.DOES_NOT_EXISTS, null);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return keycloakUserId;
    }

    public void deleteKeyCloakAccount(String msisdn) {
        Keycloak keycloakAdmin = newKeycloakBuilderWithPasswordCredentials(adminUsername, adminPassword).build();
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        List<UserRepresentation> users = usersResource.search(msisdn);
        if (users.isEmpty()) {
            logger.error(NO_USER_KEYCLOAK_FOUND_WITH_MSISDN_MESSAGE, msisdn);
        } else {
            UserResource userResource = usersResource.get(users.get(0).getId());
            userResource.remove();
        }
    }

    public void updateKeycloakInfo(UpdateKeycloakInfoReqDTO request, Account account) {
        Keycloak keycloakAdmin = newKeycloakBuilderWithPasswordCredentials(adminUsername, adminPassword).build();
        UsersResource usersResource = keycloakAdmin.realm(realm).users();
        try {
            List<UserRepresentation> users = usersResource.search(account.getMsisdn());
            if (users.isEmpty()) {
                logger.error(NO_USER_KEYCLOAK_FOUND_WITH_MSISDN_MESSAGE, account.getMsisdn());
            } else {
                UserResource userResource = usersResource.get(users.get(0).getId());
                UserRepresentation keycloakUser = userResource.toRepresentation();
                setKeycloakName(request.getFullName(), keycloakUser);
                updateKeycloakEmail(request.getEmail(), keycloakUser);
                userResource.update(keycloakUser);
            }
        } catch (NotFoundException exception) {
            logger.error("User not found in keycloak with msisdn {}", account.getMsisdn());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    public void updateKeycloakEmail(String email, UserRepresentation keycloakUser) {
        if (StringUtils.isNotEmpty(email)) {
            keycloakUser.setEmail(email);
        }
    }

    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    private void setKeycloakName(String fullName, UserRepresentation keycloakUser) {
        if (StringUtils.isNotEmpty(fullName)) {
            String[] nameParts = fullName.split("\\s+");
            if (nameParts.length == 1) {
                keycloakUser.setFirstName(nameParts[0]);
                keycloakUser.setLastName(null);
            } else {
                keycloakUser.setFirstName(nameParts[0]);
                keycloakUser.setLastName(fullName.substring(nameParts[0].length() + 1));
            }
        }
    }

}