package edu.miu.cs.cs544.service;

import edu.miu.cs.cs544.controller.UserAlreadyExistException;
import edu.miu.cs.cs544.domain.UserModel;
import edu.miu.cs.cs544.domain.PasswordResetToken;
import edu.miu.cs.cs544.domain.RoleType;
import edu.miu.cs.cs544.domain.User;
import edu.miu.cs.cs544.domain.VerificationToken;
import edu.miu.cs.cs544.repository.PasswordResetTokenRepository;
import edu.miu.cs.cs544.repository.UserRepository;
import edu.miu.cs.cs544.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;


    @Override
    public User registerUser(UserModel userModel) throws UserAlreadyExistException {
        if (emailExists(userModel.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email address: "
                    + userModel.getEmail());
        }
        User user = new User();
        user.setUserName(userModel.getUserName());
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setRoleType(RoleType.CLIENT);
        user.setUserPass(passwordEncoder.encode(userModel.getPassword()));
        userRepository.save(user);
        return user;
    }
    private boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user,token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            return "invalidToken";
        }
        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0){
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        //Activate the user
        user.setActive(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public VerificationToken generateNewVerificationToken(String existingToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(existingToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
     PasswordResetToken passwordResetToken = new PasswordResetToken(user,token);
     passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public String ValidatePasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if(passwordResetToken == null){
            return "invalidToken";
        }
        User user = passwordResetToken.getUser();
        Calendar calendar = Calendar.getInstance();
        if((passwordResetToken.getExpiryDate().getTime() - calendar.getTime().getTime()) <= 0){
            passwordResetTokenRepository.delete(passwordResetToken);
            return "expired";
        }

        return "valid";
    }

    @Override
    public Optional<User> getUserByPasswordToken(String token) {
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(token).getUser());
    }

    @Override
    public void changeUserPassword(User user, String password) {
        user.setUserPass(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword,user.getUserPass());
    }


}
