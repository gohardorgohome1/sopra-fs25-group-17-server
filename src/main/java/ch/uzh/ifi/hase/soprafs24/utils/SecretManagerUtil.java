package ch.uzh.ifi.hase.soprafs24.utils;

import com.google.cloud.secretmanager.v1.AccessSecretVersionRequest;
import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;

public class SecretManagerUtil {

    public static String getSecret(String projectId, String secretId) throws Exception {
        String versionId = "latest";

        try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
            SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, versionId);
            AccessSecretVersionRequest request = AccessSecretVersionRequest.newBuilder()
                    .setName(secretVersionName.toString())
                    .build();

            AccessSecretVersionResponse response = client.accessSecretVersion(request);
            return response.getPayload().getData().toStringUtf8();
        }
    }
}
