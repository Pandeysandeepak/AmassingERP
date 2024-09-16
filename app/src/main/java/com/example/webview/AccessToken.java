package com.example.webview;

import android.util.Log;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AccessToken {
    private static final String firebaseMessagingScope =
            "https://www.googleapis.com/auth/firebase.messaging";

    public static String getAccessToken() {
        try {
            String jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"amassingerp\",\n" +
                    "  \"private_key_id\": \"1c95765498119700a8b1754c44a2140c743560f8\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDV4BbzO/3LY/oD\\nrva8Lkce6RVjRa7g3utCIccmrbRm3BkQVPGcYAjmDyVRQRE95hiTLEA7GlpeP02s\\nQFKPTGipgGVQ7DCEhKqPy+K6r89FrYvn3p6euMiaKq/tQkSggtaEhjSEki+yIuvL\\no96YboWhz0vegTRDTOWqhJk+82PoDzZ6dSxz3uLMax63XjKn03ENXBi49Wx0SQFc\\n0BrvO9usuNMYnxeVSQlpozI2ZDRmQpqsB2WGWjX3sGR36V1JeZ+ZqzYDGEkLtnYV\\nS2O2YndtjUG060wT/uaHzF+b3CynGn7H/noc/8dLIlFb1kIJ6lj64/BUCmKyNhS5\\nhJVkOiVBAgMBAAECggEAIMH8XI/tN5hIrwS+3M7CbOfACLRKbJMoJGO+Ytxge7qt\\nX2orQXYXbClDYCEfrhk5MrLTOa+NH3oBqxGFCQFn1xeygkT9dLv0eybRbiJHFGyC\\n4mZKFD8dgTjAOQ5D1Z6Ekx1nxisKgXXGI4AmCWHa4jrjwMuNq4XdIr4W0g9UV4f+\\ng3ohABcz09zBMshzPAoYQqVRd+afcYiLx0d3C1anYjd8HokAr08jtT0bFRo7fn2v\\nPq0YHODbssun/5iv2ZGjtyIV2LPirRsho9aY5vCUe7qQW/ZbhyDX48u+c9JZKcR+\\n8/T3oTZEKC9hYznFHBfjgpCrFx98b91JsoqM3ILq4wKBgQD3Nd0NywiqLbUyAbtz\\nSY7gpV5BoHeh8sbqwg5/Zy+TLtIrZ9HCYB7ie/cFrYnkTuZ0pFSM0DormC/IQAZl\\n4+Kv2LWwJ+ynV/sSfxH/RUsZIoatcgM3UhKMrH8EjtRT7UvImlLS3KGW80Ysy+Ak\\nug1YdmDX47LfUipD+lIF+ZXMdwKBgQDdes6KY6Yrnrm9eMfTL0pbxgm6/mP0qMIh\\n5gYQP7+8ujQB0uN/j6FN1TcWcGDd7oy+ZVbADwkPVXUsQ6J9mkAYFbiIIInPimVM\\nxTveWhMnWcLi5phnc/iGMw+DBqa/IU0IjTv1X2QvfXWsD08kmMGxkYTB+o94eesA\\n6iOb8QRiBwKBgQCTj0w+onWulw/0JIKOg1bMkwkbJTyjp3XCxUjAFYiZUzZSgGr9\\nmkB1Mke11OtdIxd+gmSRlO1T/khvQIBRK2CxVCrHVl11Whfc54qHlAYKn8hCGa7/\\nw3adD43V26ez3Q4CVNOhV98Aan/111AYvBGhPy/5TcoEcVgXMYDqXhtjawKBgFIr\\nD5l0IuAoAQz0VF5bOyEVNeL0Ii1QtsVQy4sXu5DPHCBZFyHqLZPsuIVQp/9uUzUH\\n78lNSrUvpUsIxK4IniLRXCbUu4wN8ksrGW0CyfxdZto7ZutqsRdItkaMHyzls+iq\\nTUZZX5fm2dM90sTnD2VLcY9t3/B4euFZ/GAMaO3dAoGBAI3x5KoaaNQkidT74Vvk\\nXV7OiS+wPBSNmmHBfCkjemRx0xbNLoz+3Fu7EzBCz5bCzsMoYNvaFUl0qnSPtBPc\\nCi5D+xZeyrZF8imDwkPYwygOnekqrm5ffc5OW2YKCbr24tvzl6l2JnRCmEu5TwVH\\nH9xi82RWOfWhvvOtAF9lvFi8\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"projectowner@amassingerp.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"108458362909919115516\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/projectowner%40amassingerp.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(firebaseMessagingScope);

            Log.d("AccessToken", "GoogleCredentials created: " + googleCredentials);

            googleCredentials.refresh();

            Log.d("AccessToken", "Refreshed GoogleCredentials: " + googleCredentials);

            if (googleCredentials.getAccessToken() != null) {
                Log.d("AccessToken", "Access token obtained: " + googleCredentials.getAccessToken().getTokenValue());
                return googleCredentials.getAccessToken().getTokenValue();
            } else {
                Log.e("AccessToken", "Failed to obtain access token. GoogleCredentials.getAccessToken() returned null.");
                return null;
            }
        } catch (Exception e) {
            Log.e("AccessToken", "Error obtaining access token: ", e);
            return null;
        }
    }
}
