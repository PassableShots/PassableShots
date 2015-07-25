package ca.deflector.passable_shots;

import android.os.Environment;

import org.spongycastle.openpgp.PGPCompressedDataGenerator;
import org.spongycastle.openpgp.PGPEncryptedData;
import org.spongycastle.openpgp.PGPEncryptedDataGenerator;
import org.spongycastle.openpgp.PGPException;
import org.spongycastle.openpgp.PGPLiteralData;
import org.spongycastle.openpgp.PGPLiteralDataGenerator;
import org.spongycastle.openpgp.PGPPublicKey;
import org.spongycastle.openpgp.PGPPublicKeyRing;
import org.spongycastle.openpgp.PGPPublicKeyRingCollection;
import org.spongycastle.openpgp.PGPUtil;
import org.spongycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.spongycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.spongycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Iterator;

public class EncryptionSystem {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public static void initKey(File keyring) throws IOException, PGPException {
        key = readPublicKeyFromCol(new FileInputStream(keyring));

    }

    public static void initKey(byte[] keyringBytes) throws IOException, PGPException {
        ByteArrayInputStream bais = new ByteArrayInputStream(keyringBytes);
        key = readPublicKeyFromCol(bais);
        bais.close();
    }

    public static void initKey() throws IOException, PGPException {
        File keyring = new File(Environment.getExternalStorageDirectory() + "/passable.key");
        initKey(keyring);
    }

    private static PGPPublicKey key = null;


    public static byte[] encrypt(byte[] payload) throws IOException, PGPException {
        ByteArrayOutputStream packetBaos = new ByteArrayOutputStream();
        PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
        OutputStream packetOut = lData.open(packetBaos, PGPLiteralData.BINARY, "picture.jpeg", payload.length, new Date());

        packetOut.write(payload);
        packetOut.close();
        byte[]packet =packetBaos.toByteArray();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
                new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                        .setWithIntegrityPacket(true)
                        .setSecureRandom(new SecureRandom())
                        .setProvider("SC"));

        encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(key).setProvider("SC"));

        ByteArrayOutputStream resultBaos = new ByteArrayOutputStream();

        OutputStream encOut = encGen.open(resultBaos, packet.length);
        encOut.write(packet);
        encOut.close();

        byte[] result = resultBaos.toByteArray();

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