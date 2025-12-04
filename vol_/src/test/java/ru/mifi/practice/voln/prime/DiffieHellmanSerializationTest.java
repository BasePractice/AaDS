package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigInteger;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Диффи–Хеллман: сериализация ключей")
class DiffieHellmanSerializationTest {

    @Test
    @DisplayName("Строковая сериализация/десериализация (малые p,g)")
    void stringRoundTripSmallParams() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);

        DiffieHellman dh = new DiffieHellman(p, g);
        assertThrows(IllegalStateException.class, dh::sharedSecretHex);
        DiffieHellman other = new DiffieHellman(p, g);
        dh.calculateSharedSecret(other.getPublicKey());
        assertEquals(dh.getPrivateKey(), DiffieHellman.fromHex(dh.privateKeyHex()));
        assertEquals(dh.getPublicKey(), DiffieHellman.fromHex(dh.publicKeyHex()));
        assertEquals(dh.getSharedSecret(), DiffieHellman.fromHex(dh.sharedSecretHex()));
        String hex = DiffieHellman.toHex(dh.getPublicKey());
        BigInteger parsed = DiffieHellman.fromHex(hex);
        assertEquals(dh.getPublicKey(), parsed);
    }

    @Test
    @DisplayName("Файловая сериализация/десериализация (малые p,g)")
    void fileRoundTripSmallParams(@TempDir Path tmp) throws Exception {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);

        DiffieHellman a = new DiffieHellman(p, g);
        DiffieHellman b = new DiffieHellman(p, g);
        a.calculateSharedSecret(b.getPublicKey());

        Path privPath = tmp.resolve("priv.hex");
        Path pubPath = tmp.resolve("pub.hex");
        Path shPath = tmp.resolve("shared.hex");

        a.writePrivateKey(privPath);
        a.writePublicKey(pubPath);
        a.writeSharedSecret(shPath);

        BigInteger priv = DiffieHellman.readKey(privPath);
        BigInteger pub = DiffieHellman.readKey(pubPath);
        BigInteger sh = DiffieHellman.readKey(shPath);

        assertEquals(a.getPrivateKey(), priv);
        assertEquals(a.getPublicKey(), pub);
        assertEquals(a.getSharedSecret(), sh);
        assertTrue(sh.compareTo(BigInteger.ONE) >= 0);
        assertTrue(sh.compareTo(p) < 0);
    }

    @Test
    @DisplayName("Строки и файлы для случайных параметров по битности")
    void stringAndFileRandomParams(@TempDir Path tmp) throws Exception {
        DiffieHellman x = new DiffieHellman(64);
        DiffieHellman y = new DiffieHellman(x.getPrime(), x.getGenerator());
        x.calculateSharedSecret(y.getPublicKey());
        y.calculateSharedSecret(x.getPublicKey());

        assertEquals(x.getPrivateKey(), DiffieHellman.fromHex(x.privateKeyHex()));
        assertEquals(x.getPublicKey(), DiffieHellman.fromHex(x.publicKeyHex()));
        assertEquals(x.getSharedSecret(), DiffieHellman.fromHex(x.sharedSecretHex()));
        assertEquals(x.getSharedSecret(), y.getSharedSecret());

        Path s = tmp.resolve("s.hex");
        DiffieHellman.writeKey(s, x.getSharedSecret());
        BigInteger sRead = DiffieHellman.readKey(s);
        assertEquals(x.getSharedSecret(), sRead);
    }
}
