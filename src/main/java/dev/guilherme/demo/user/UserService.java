package dev.guilherme.demo.user;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserModel saveUser(UserDTO dto) {

        UserModel userModel = new UserModel();

        userModel.setName(dto.name());
        userModel.setEmail(dto.email());
        userModel.setPassword(passwordEncoder.encode(dto.password()));

        return userRepository.save(userModel);
    }

    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public UserModel findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    }

    public UserModel updateUser(Long id, UserDTO dto) {
        UserModel userModel = findById(id);
        userModel.setName(dto.name());
        userModel.setEmail(dto.email());

        return userRepository.save(userModel);
    }

    public void changePassword(Long id, String newPassword) {

        UserModel userModel = findById(id);

        userModel.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(userModel);
    }


    public List<UserModel> findAll() {
        return userRepository.findAll();
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
