package com.example.cryptochat.Crypto;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DiffieHelman {
    public BigInteger Key(BigInteger ozelSayi, BigInteger guvenliAsal, BigInteger uretec) {
        BigInteger R1 = uretec.modPow(ozelSayi, guvenliAsal);

        return R1;
    }

    public String KeySha(BigInteger Key) {
        byte[] messageDigest;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            messageDigest = md.digest(Key.toByteArray());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
            String shaKey = no.toString(16);

            while (shaKey.length() < 32) {
                shaKey = "0" + shaKey;
            }
            return shaKey;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error:" + e);
            return null;
        }

    }
}
