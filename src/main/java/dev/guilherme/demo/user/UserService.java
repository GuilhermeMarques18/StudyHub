package dev.guilherme.demo.user;

import dev.guilherme.demo.user.dtos.UpdateUserDTO;
import dev.guilherme.demo.user.dtos.UserDTO;
import dev.guilherme.demo.user.exception.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService implements UserDetailsService {

    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
    }


    public UserModel saveUser(UserDTO dto) {
        
        if (existsByEmail(dto.email())) {
        throw new UserAlreadyExistsException("Email já cadastrado");
        }   
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

    public UserModel updateUser(Long id, UpdateUserDTO dto) {
        UserModel userModel = findById(id);
        userModel.setName(dto.name());
        userModel.setEmail(dto.email());

        return userRepository.save(userModel);
    }

    public List<UserModel> findAll() {
        return userRepository.findAll();
    }


    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
