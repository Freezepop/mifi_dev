package sls;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public interface shorter {
    static String make_shorted_link(String url, String userid) throws NoSuchAlgorithmException {
        String sum_data = url + userid;
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] hashed_string = digest.digest(sum_data.getBytes());
        String link_id = Base64.getUrlEncoder().withoutPadding().encodeToString(hashed_string);
        return link_id.substring(0, 8);
    }
}
