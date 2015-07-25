package ca.deflector.passable_shots;

import android.os.Environment;

import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Iterator;

public class EncryptionSystem {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static byte[] encrypt(byte[] payload, byte[] public_key) throws IOException, PGPException {

        File keyring = new File(Environment.getExternalStorageDirectory() + "/passable.key");

        PGPPublicKey key = readPublicKeyFromCol(new FileInputStream(keyring));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()).setProvider("SC"));

        encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(key).setProvider("SC"));

        OutputStream cOut = encGen.open(baos, payload.length);

        cOut.write(payload);
        cOut.close();

        byte[] result = baos.toByteArray();

        return result;
    }

    public static PGPPublicKey readPublicKeyFromCol(InputStream in) throws IOException, PGPException {
        in = PGPUtil.getDecoderStream(in);
        PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(in, new BcKeyFingerprintCalculator());
        PGPPublicKey key = null;
        Iterator<PGPPublicKeyRing> keyRings = pgpPub.getKeyRings();
        while (key == null && keyRings.hasNext()) {
            PGPPublicKeyRing keyRing = keyRings.next();
            Iterator<PGPPublicKey> keys = keyRing.getPublicKeys();
            while (keys.hasNext()) {
                PGPPublicKey k = keys.next();
                if (k.isEncryptionKey()) {
                    key = k;
                    break;
                }
            }
        }
        if (key == null)
            throw new PGPException("Can't find a valid encryption key in key ring.");

        return key;
    }
}