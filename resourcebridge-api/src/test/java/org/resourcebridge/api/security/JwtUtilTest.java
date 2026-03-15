package org.resourcebridge.api.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private final JwtUtil jwtUtil = new JwtUtil();

    @Test
    void generateToken_producesNonNullToken() {
        String token = jwtUtil.generateToken("user@email.com", "COORDINATOR");
        assertThat(token).isNotBlank();
    }

    @Test
    void isTokenValid_returnsTrueForFreshToken() {
        String token = jwtUtil.generateToken("user@email.com", "COORDINATOR");
        assertThat(jwtUtil.isTokenValid(token)).isTrue();
    }

    @Test
    void extractEmail_returnsCorrectEmail() {
        String token = jwtUtil.generateToken("jane@email.com", "STAFF");
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("jane@email.com");
    }

    @Test
    void extractRole_returnsCorrectRole() {
        String token = jwtUtil.generateToken("user@email.com", "STAFF");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("STAFF");
    }

    @Test
    void extractRole_returnsCoordinatorRole() {
        String token = jwtUtil.generateToken("coord@email.com", "COORDINATOR");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("COORDINATOR");
    }

    @Test
    void isTokenValid_returnsFalseForGarbageToken() {
        assertThat(jwtUtil.isTokenValid("not.a.real.token")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForTamperedToken() {
        String token = jwtUtil.generateToken("user@email.com", "STAFF");
        assertThat(jwtUtil.isTokenValid(token + "tampered")).isFalse();
    }

    @Test
    void isTokenValid_returnsFalseForEmptyString() {
        assertThat(jwtUtil.isTokenValid("")).isFalse();
    }
}
