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
                    "  \"private_key_id\": \"d3e13195fb59fc7d4c562c7ad2e6a33e3b868e43\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC/BL/EWquwQuuM\\n8+ltkBg18zrEBxn22pn5jm11KfR+feiyS1N8OzkqLRzma6P250FclzE2WnSqlPQD\\nMGN9dBndfsgQc81N0u+JGUeVROmEbhvpjjF3OozATR347k8y96B1hgcrdkLa6i5A\\nhU8joAjKtlXCKnVz6reINkRbk6F0lJtXsvsR3RAVv6Zi5NNBeEgX3OqhbXir2tgP\\n3XIHuPYlDYSw/rF71xtd7PGcfd9zobM4Ls68h5J/jKxF2KvB7XuozHq9EwxTw/GH\\nlSZ1+N26h6o/K5EPDObU9TzNfClXEp/mF9fxQlUwhcz4UjkpzEhyLnYELYYhajO8\\njYCKKCxjAgMBAAECggEAG3+ZBbDR14GCvFtepsWgHqAhvReSns1QbjMZQc0k49LH\\nXWdnoNI7w/0TlH45b/FRoP0o+fyin+3qhM0B/Yzprk+yiF/LVeFAV3m6qtvtTAG3\\n5fLw3wugOIzBfFT+NMWeJV0CijivnikpOGOYhXXj3h7KkLWhC+tluWeqho5lp0z/\\nsilglftw34a/m23ngws70i90ue2GMUXKPVdjDmqki6OZgl+tQNaFKzaV/cifXTVg\\nya5VMXAnEk/NDlfsz6KDXFu7W3xiE/7LIeHFlmptjaaxl14pp8aKVLumpGqwflKI\\nzQtBla4gMD50+u2AFKC3z0xSywXB6DydNL9wNWKKKQKBgQDia2BELnRufagXs/rb\\nJwpPpKdMrL3x3i77gDqmoWxxFR40hXO1oDMFJVSH4l86qZ51Axx7PPsEZnjhwwHN\\nYHR42eHYhA+QAi41Rf0xLavvgWKCGffi1J5T/PcF3+fed0S0I4RTMNpnF/yshBQx\\neSrjoJk/scMOFjZXqptrT0dD6wKBgQDX+WLoLo6OOJNQYLLVYthChAZ5KiW1Gazn\\n6czeiOPSkG0Ty/F0IYujcxcgFnEMClCTh63dQKRaCp48EJqSXKk+Gby+U/4HpNLF\\nV20EyOBvLXUBxolUcSgkq6Dxeb8xycHC6DJVQSPCIMRa7pQZrTeLRIxE19IxORYb\\nt7u06FCzaQKBgFDlGjzh+Blt7buiQzM/jJCN/HQJl8etoU2cL9hO/kacy/Dp6UBw\\nHypsfvoZp/p2lbWqyedCD1EwJ2pJ2P/+wT5YYjeQX1sNXGMOQXrrnW1x5bcj/gvd\\n+T96tuszZ011gWd29RUf9Dg3OsCMZAaVCvzO6VH3egAzcXD69JJvfb/HAoGADBFl\\nU2/2iz/iC2W72GyThPP3oKGYRcxPew33Yp/niwaC8c49ia2uLc3qhuJ6IqAY7IVy\\nU6CfDmD1gomy+WawFkY946yxzx+In3pJHzFSSSQTG6xKVCuWgKYOci/JXTDW4Ns0\\npMrlAp3uGt6l6+Ff7yo/ZEFi/1kfQSE9Hdi1WbECgYBoSNloM1QYTMt3a7URxC2w\\nxkbsWW5bap4ESD91nJRMHJxgMF8jIufLV6584ZobRPfHq2d/yEZgIGTV/EYDeKIQ\\nuiNkHyd7qjBcZ/QaRqYetJzxeovCVNgiDrfOoSz4IIteqkX4DkdhjWVZXaPK577F\\nKsBt9fXd72B5t9u7UHPYmw==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-p4epr@amassingerp.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"115092086340631677713\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-p4epr%40amassingerp.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}\n";
            InputStream stream = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(stream)
                    .createScoped(firebaseMessagingScope);
                  googleCredentials.refresh ();
            // getAccessToken handles token refresh if expired
            return googleCredentials.getAccessToken ().getTokenValue ();
        } catch (Exception e) {
            Log.e("AccessToken", "getAccessToken: " + e.getLocalizedMessage());
            return null;
        }
    }
}
