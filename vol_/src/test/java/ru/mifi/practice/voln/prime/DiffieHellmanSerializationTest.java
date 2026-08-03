package ru.mifi.practice.voln.prime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigInteger;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Диффи–Хеллман: сериализация ключей")
final class DiffieHellmanSerializationTest {

    @Test
    @Timeout(5)
    @DisplayName("Hex общего секрета до расчёта бросает исключение")
    void sharedSecretHexThrowsBeforeCalc() {
        DiffieHellman dh = new DiffieHellman(BigInteger.valueOf(23), BigInteger.valueOf(5));
        assertThrows(NullPointerException.class, dh::sharedSecretHex,
            "shared secret hex before calculation doesnt throw");
    }

    @Test
    @Timeout(5)
    @DisplayName("Hex закрытого ключа восстанавливается (малые p,g)")
    void privateKeyHexRoundTripSmall() {
        DiffieHellman dh = new DiffieHellman(BigInteger.valueOf(23), BigInteger.valueOf(5));
        assertThat("private key doesnt survive hex round trip",
            DiffieHellman.fromHex(dh.privateKeyHex()), is(dh.getPrivateKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Hex открытого ключа восстанавливается (малые p,g)")
    void publicKeyHexRoundTripSmall() {
        DiffieHellman dh = new DiffieHellman(BigInteger.valueOf(23), BigInteger.valueOf(5));
        assertThat("public key doesnt survive hex round trip",
            DiffieHellman.fromHex(dh.publicKeyHex()), is(dh.getPublicKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Hex общего секрета восстанавливается (малые p,g)")
    void sharedSecretHexRoundTripSmall() {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        DiffieHellman dh = new DiffieHellman(p, g);
        DiffieHellman other = new DiffieHellman(p, g);
        dh.calculateSharedSecret(other.getPublicKey());
        assertThat("shared secret doesnt survive hex round trip",
            DiffieHellman.fromHex(dh.sharedSecretHex()), is(dh.getSharedSecret()));
    }

    @Test
    @Timeout(5)
    @DisplayName("toHex/fromHex открытого ключа взаимно обратны (малые p,g)")
    void publicKeyToFromHexRoundTripSmall() {
        DiffieHellman dh = new DiffieHellman(BigInteger.valueOf(23), BigInteger.valueOf(5));
        assertThat("public key doesnt survive to and from hex conversion",
            DiffieHellman.fromHex(DiffieHellman.toHex(dh.getPublicKey())), is(dh.getPublicKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Закрытый ключ в файле восстанавливается (малые p,g)")
    void privateKeyFileRoundTripSmall(@TempDir Path tmp) throws Exception {
        DiffieHellman a = new DiffieHellman(BigInteger.valueOf(23), BigInteger.valueOf(5));
        Path privPath = tmp.resolve("priv.hex");
        a.writePrivateKey(privPath);
        assertThat("private key doesnt survive file round trip",
            DiffieHellman.readKey(privPath), is(a.getPrivateKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Открытый ключ в файле восстанавливается (малые p,g)")
    void publicKeyFileRoundTripSmall(@TempDir Path tmp) throws Exception {
        DiffieHellman a = new DiffieHellman(BigInteger.valueOf(23), BigInteger.valueOf(5));
        Path pubPath = tmp.resolve("pub.hex");
        a.writePublicKey(pubPath);
        assertThat("public key doesnt survive file round trip",
            DiffieHellman.readKey(pubPath), is(a.getPublicKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет в файле восстанавливается (малые p,g)")
    void sharedSecretFileRoundTripSmall(@TempDir Path tmp) throws Exception {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        DiffieHellman a = new DiffieHellman(p, g);
        DiffieHellman b = new DiffieHellman(p, g);
        a.calculateSharedSecret(b.getPublicKey());
        Path shPath = tmp.resolve("shared.hex");
        a.writeSharedSecret(shPath);
        assertThat("shared secret doesnt survive file round trip",
            DiffieHellman.readKey(shPath), is(a.getSharedSecret()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет из файла в диапазоне (малые p,g)")
    void sharedSecretFileInRangeSmall(@TempDir Path tmp) throws Exception {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);
        DiffieHellman a = new DiffieHellman(p, g);
        DiffieHellman b = new DiffieHellman(p, g);
        a.calculateSharedSecret(b.getPublicKey());
        Path shPath = tmp.resolve("shared.hex");
        a.writeSharedSecret(shPath);
        assertThat("shared secret read from file is outside range one to prime",
            DiffieHellman.readKey(shPath), both(greaterThanOrEqualTo(BigInteger.ONE)).and(lessThan(p)));
    }

    @Test
    @Timeout(5)
    @DisplayName("Hex закрытого ключа восстанавливается (случайные параметры)")
    void privateKeyHexRoundTripRandom() {
        DiffieHellman x = new DiffieHellman(64);
        assertThat("private key doesnt survive hex round trip",
            DiffieHellman.fromHex(x.privateKeyHex()), is(x.getPrivateKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Hex открытого ключа восстанавливается (случайные параметры)")
    void publicKeyHexRoundTripRandom() {
        DiffieHellman x = new DiffieHellman(64);
        assertThat("public key doesnt survive hex round trip",
            DiffieHellman.fromHex(x.publicKeyHex()), is(x.getPublicKey()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Hex общего секрета восстанавливается (случайные параметры)")
    void sharedSecretHexRoundTripRandom() {
        DiffieHellman x = new DiffieHellman(64);
        DiffieHellman y = new DiffieHellman(x.getPrime(), x.getGenerator());
        x.calculateSharedSecret(y.getPublicKey());
        assertThat("shared secret doesnt survive hex round trip",
            DiffieHellman.fromHex(x.sharedSecretHex()), is(x.getSharedSecret()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет совпадает у сторон (случайные параметры)")
    void sharedSecretMatchesRandom() {
        DiffieHellman x = new DiffieHellman(64);
        DiffieHellman y = new DiffieHellman(x.getPrime(), x.getGenerator());
        x.calculateSharedSecret(y.getPublicKey());
        y.calculateSharedSecret(x.getPublicKey());
        assertThat("shared secrets of parties dont match",
            x.getSharedSecret(), is(y.getSharedSecret()));
    }

    @Test
    @Timeout(5)
    @DisplayName("Общий секрет в файле восстанавливается (случайные параметры)")
    void sharedSecretFileRoundTripRandom(@TempDir Path tmp) throws Exception {
        DiffieHellman x = new DiffieHellman(64);
        DiffieHellman y = new DiffieHellman(x.getPrime(), x.getGenerator());
        x.calculateSharedSecret(y.getPublicKey());
        Path s = tmp.resolve("s.hex");
        DiffieHellman.writeKey(s, x.getSharedSecret());
        assertThat("shared secret doesnt survive file round trip",
            DiffieHellman.readKey(s), is(x.getSharedSecret()));
    }
}
