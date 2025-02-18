package com.ermnvldmr.w.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ermnvldmr.w.config.PropertiesConfig;
import org.springframework.stereotype.Service;
import com.ermnvldmr.w.domain.User;
import com.ermnvldmr.w.form.UserCredentialsRegister;
import com.ermnvldmr.w.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    private final PropertiesConfig propertiesConfig;

    private final UserRepository userRepository;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public UserService(
            PropertiesConfig propertiesConfig,
            UserRepository userRepository) {
        this.propertiesConfig = propertiesConfig;
        this.userRepository = userRepository;

        this.algorithm = Algorithm.HMAC256(this.propertiesConfig.getJwtSecret());
        this.verifier = JWT.require(algorithm).build();
    }

    public User findByLogin(String login) {
        return login == null ? null : userRepository.findByLogin(login);
    }

    public User findByLoginAndPassword(String login, String password) {
        return login != null && password != null ?
                userRepository.findByLoginAndPassword(login,
                        password,
                        this.propertiesConfig.getShaSalt()) : null;
    }

    public User findById(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    public User findByJwt(String jwt) {
        try {
            DecodedJWT decodedJwt = verifier.verify(jwt);
            return this.findById(decodedJwt.getClaim("userId").asLong());
        } catch (JWTVerificationException exception){
            return null;
        }
    }

    public List<User> findAll() {
        return userRepository.findAllByOrderByIdDesc();
    }

    public String createUserJwt(User user) {
        try {
            return JWT.create()
                    .withClaim("userId", user.getId())
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Can't create JWT.");
        }
    }

    public User addUserByCredentials(UserCredentialsRegister userCredentials) {
        User user = new User();
        user.setName(userCredentials.getName());
        user.setLogin(userCredentials.getLogin());
        user.setAdmin(false);
        userRepository.save(user);
        userRepository.updatePasswordSha(user.getId(),
                                        userCredentials.getLogin(),
                                        userCredentials.getPassword(),
                                        this.propertiesConfig.getShaSalt());
        return user;
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
