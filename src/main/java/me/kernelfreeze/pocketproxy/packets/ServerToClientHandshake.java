package me.kernelfreeze.pocketproxy.packets;

import lombok.Getter;
import lombok.Setter;
import me.kernelfreeze.pocketproxy.PocketPlayer;
import me.kernelfreeze.pocketproxy.Security;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

/**
 * @author KernelFreeze
 * @since 5/06/17
 */
public class ServerToClientHandshake extends DataPacket {
    @Getter
    @Setter
    private KeyPair publicKey;

    @Getter
    @Setter
    private byte[] serverToken;

    public ServerToClientHandshake(PocketPlayer p) {
        super(NetworkType.SERVER_TO_CLIENT_HANDSHAKE_PACKET);

        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            generator.initialize(new ECGenParameterSpec("secp384r1"));
            KeyPair serverKeyPair = generator.generateKeyPair();

            byte[] token = Security.generateRandomToken();
            byte[] serverKey = Security.getServerKey(serverKeyPair, token);
            p.enableEncryption(serverKey);

            publicKey = serverKeyPair;
            serverToken = token;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void encode() {
        writeString(Base64.getEncoder().encodeToString(publicKey.getPublic().getEncoded()));
        writeBytes(serverToken);
    }
}
